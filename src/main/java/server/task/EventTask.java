package server.task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.mail.MessagingException;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import server.database.model.Device;
import server.database.model.Event;
import server.database.model.EventStat;
import server.database.model.User;
import server.database.repository.EventRepository;
import server.database.repository.EventStatRepository;
import server.database.repository.SensorTypeRepository;

public class EventTask {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private SensorTypeRepository sensorTypeRepository;
	private EventRepository eventRepository;
	private EventStatRepository eventStatRepository;

	private DateTime date;
	private Device device;

	private User user;
	private DateTime startNight;
	private DateTime endNight;
	private List<Event> eventMotion;
	private List<Event> eventTemp;
	private List<Event> eventTamper;
	private List<Event> eventLum;
	private List<List<Event>> eventLists;
	private String path;
	private EventStat eventStat;

	public EventTask(Device device, DateTime date, SensorTypeRepository sensorTypeRepository,
			EventRepository eventRepository, EventStatRepository eventStatRepository, String path) {
		this.device = device;
		this.date = date.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
		this.sensorTypeRepository = sensorTypeRepository;
		this.eventRepository = eventRepository;
		this.eventStatRepository = eventStatRepository;
		this.path = path;
		user = device.getUser();
		setNightLimits();
		if (startNight == null || endNight == null)
			eventLists = null;
		else {
			eventLists = getEventLists();
			eventStat = eventStatRepository.findByDeviceAndDate(device, date.toDate());
			if (eventStat == null)
				createEventStat();
		}

	}

	public void createEventStat() {
		eventStat = new EventStat();
		eventStat.setDate(date.getMillis());
		eventStat.setDevice(device);
		eventStat.setMvts(eventMotion.size());
		eventStat.setDuration(endNight.getMillis() - startNight.getMillis());
		eventStatRepository.save(eventStat);
	}

	private void setNightLimits() {
		// Time
		DateTime timeSleepStart = null;
		DateTime timeSleepEnd = null;

		if (user.getSleepStart() != null && user.getSleepEnd() != null) {
			timeSleepStart = new DateTime(user.getSleepStart().getTime());
			timeSleepEnd = new DateTime(user.getSleepEnd().getTime());
		} else {
			logger.warn("User id=" + user.getId() + ": missing start/end sleep times from database");
			return;
		}

		// Theoretical start/end of night
		DateTime startTheo = date.minusDays(1).withHourOfDay(timeSleepStart.getHourOfDay())
				.withMinuteOfHour(timeSleepStart.getMinuteOfHour()).withSecondOfMinute(0);
		DateTime endTheo = date.withHourOfDay(timeSleepEnd.getHourOfDay())
				.withMinuteOfHour(timeSleepEnd.getMinuteOfHour()).withSecondOfMinute(0);

		// Margin of error on the start/end theoretical values (hour)
		int margin = 1;
		DateTime startLimit = startTheo.minusHours(margin);
		DateTime endLimit = endTheo.plusHours(margin);

		// Events from startLimit to endLimit
		List<Event> events = eventRepository.findByDeviceAndTypeAndDateBetween(device,
				sensorTypeRepository.findByName("luminescence"), startLimit.toDate(), endLimit.toDate());

		if (events.size() == 0) {
			logger.info("No event found");
			return;
		}

		Double average = eventRepository.getAverageTypeBetween(device.getId(),
				sensorTypeRepository.findByName("luminescence").getId(), startLimit.toDate(), endLimit.toDate());

		logger.info("Finding " + date.toString("dd/MM/yyyy") + " night limits for user id=" + user.getId()
				+ " with lux avg=" + average + ", count=" + events.size());

		startNight = findStartNightLimit(events, startTheo, margin, average);
		Collections.reverse(events);
		endNight = findEndNightLimit(events, endTheo, margin, average);

		logger.info("Found limits (id=" + user.getId() + "): " + "start=" + startNight.toString("dd/MM/yyyy HH:mm")
				+ ", end=" + endNight.toString("dd/MM/yyyy HH:mm"));
	}

	@SuppressWarnings("unused")
	private List<Event> eventSplit(List<Event> events, Date from, Date to) {
		int max = events.size();
		int i = 0;
		while (i < max && events.get(i).getDate().getTime() < from.getTime())
			i++;
		int j = i;
		while (j < max && events.get(j).getDate().getTime() <= to.getTime())
			j++;

		List<Event> list = new ArrayList<>();
		while (i < j) {
			list.add(events.get(i));
			i++;
		}
		return list;
	}

	private DateTime findEndNightLimit(List<Event> events, DateTime dateTime, int margin, Double average) {
		int i = 0;
		int size = events.size();
		int last = 0;
		while (i < size) {
			if (new DateTime(events.get(i).getDate()).isAfter(startNight.plusHours(10))) {
				i++;
				last = i;
				continue;
			}
			if (events.get(i).getDate().getTime() <= dateTime.minusHours(margin).getMillis())
				break;
			int j = i;
			while (j < size && events.get(j).getdValue() > average)
				j++;
			if (j != i)
				last = j;
			i = ++j;
		}
		return new DateTime(events.get(last).getDate());
	}

	private DateTime findStartNightLimit(List<Event> events, DateTime dateTime, int margin, Double average) {
		int i = 0;
		int size = events.size();
		int last = 0;
		while (i < size) {
			if (events.get(i).getDate().getTime() > dateTime.plusHours(margin).getMillis())
				break;
			int j = i;
			while (j < size && events.get(j).getdValue() > average)
				j++;
			if (j != i)
				last = j;
			i = ++j;
		}
		if (last >= size)
			return null;
		return new DateTime(events.get(last).getDate());
	}

	@SuppressWarnings("unused")
	@Deprecated
	private DateTime findNightLimit(List<Event> events, Double average, boolean endNight) {
		int size = events.size();
		int cur = 0;
		while (cur < size) {
			Event lum = events.get(cur);
			Double value = lum.getdValue();
			if (value < average) {
				boolean cnt = false;
				int i = cur;
				// 'limit' times low lux value in a row = startNight found
				int limit = 5;
				while (i < size && i < cur + limit) {
					if (events.get(i).getdValue() >= average) {
						cnt = true;
						i++;
						break;
					}
					i++;
				}
				while (cnt) {
					if (events.get(i).getdValue() < average) {
						cur = i;
						break;
					}
					i++;
				}
				if (!cnt) {
					return new DateTime(events.get(cur - (endNight && cur > 0 ? 1 : 0)).getDate());
				}
			}
			cur++;
		}
		return null;
	}

	private List<List<Event>> getEventLists() {
		logger.info("Requesting events for: " + endNight.toString("dd/MM/yyy") + " - " + user.getFirstName() + " "
				+ user.getLastName());
		eventMotion = eventRepository.findByDeviceAndTypeAndBinValueAndDateBetween(device,
				sensorTypeRepository.findByName("motion"), true, startNight.toDate(), endNight.toDate());
		eventTemp = eventRepository.findByDeviceAndTypeAndDateBetween(device,
				sensorTypeRepository.findByName("temperature"), startNight.toDate(), endNight.toDate());
		eventTamper = eventRepository.findByDeviceAndTypeAndDateBetween(device,
				sensorTypeRepository.findByName("tamper"), startNight.toDate(), endNight.toDate());
		eventLum = eventRepository.findByDeviceAndTypeAndDateBetween(device,
				sensorTypeRepository.findByName("luminescence"), startNight.toDate(), endNight.toDate());

		return Arrays.asList(eventLum, eventMotion, eventTamper, eventTemp);
	}

	public ArrayList<String> createCsvReport() {
		if (eventLists == null)
			return null;
		logger.info("Creating" + eventLists.size() + "csv files for " + user.getName());
		ArrayList<String> fileNames = new ArrayList<>();
		for (List<Event> events : eventLists) {
			if (events.size() == 0)
				continue;
			String fileName = path + '/' + user.getFirstName() + "_" + user.getLastName() + "_"
					+ date.toString("dd-MM-yyyy") + "_" + events.get(0).getType().getName() + ".csv";
			FileWriter fileWriter = null;
			try {
				fileWriter = new FileWriter(fileName);
				PrintWriter printWriter = new PrintWriter(fileWriter);
				printWriter.println("date,value");
				if (events.get(0).getBinValue() != null) {
					for (Event event : events) {
						if (event.getBinValue() == true)
							printWriter.printf("%1$td/%<tm/%<ty %<tT,%2$d\n", event.getDate(), 1);
					}
				} else {
					for (Event event : events) {
						printWriter.printf("%1$td/%<tm/%<ty %<tT,%2$f\n", event.getDate(), event.getdValue());
					}
				}
				printWriter.close();
				fileNames.add(fileName);
			} catch (IOException e) {
				logger.error("Unable to create file " + new File(fileName).getName() + ":" + e.getMessage());
			}
		}
		return fileNames;
	}

	public List<String> sendEmail(String id, String password, String host, ArrayList<String> files) {
		try {
			Email email = new Email(id, password, host);
			email.setSubject("Relevés de la nuit du " + startNight.toString("dd/MM/yyyy") + " au "
					+ endNight.toString("dd/MM/yyyy"));
			email.concatBody("Sujet: " + user.getName());

			List<String> recipients = new ArrayList<>();

			if (user.getEmail() != null)
				recipients.add(user.getEmail());

			Set<User> related = device.getUser().getRelated();
			if (related != null && related.isEmpty() == false)
				for (User usr : related)
					if (usr.getEmail() != null)
						recipients.add(usr.getEmail());

			if (recipients.isEmpty())
				return null;
			for (String recipient : recipients)
				email.addRecipient(recipient);

			if (files != null)
				email.addAttachments(files);

			String stats = "";
			stats += "Période : " + startNight.toString("dd/MM/yyyy HH:mm") + " - "
					+ endNight.toString("dd/MM/yyyy HH:mm");
			Period period = new Period(eventStat.getDuration().getTime());
			stats += "\r\nDurée : " + period.getHours() + "h et " + period.getMinutes() + "m";
			stats += "\r\nNombre de mouvement : " + eventMotion.size();
			stats += "\r\nMouvements par tranche horaire :";

			DateTime time = startNight;
			while (time.isEqual(endNight) == false) {
				DateTime endTime = time.plusHours(1).withMinuteOfHour(0).withSecondOfMinute(0);
				if (endTime.isAfter(endNight))
					endTime = endNight;
				Long count = eventRepository.countByDeviceAndTypeAndBinValueAndDateBetween(device,
						sensorTypeRepository.findByName("motion"), true, time.toDate(), endTime.toDate());
				stats += "\r\n\t" + time.toString("HH:mm") + " - " + endTime.toString("HH:mm : ") + count;
				time = endTime;
			}

			Long count = eventRepository.countByDeviceAndTypeAndBinValueAndDateBetween(device,
					sensorTypeRepository.findByName("tamper"), true, startNight.toDate(), endNight.toDate());
			stats += "\r\nNombre de secousses : " + count;

			stats += "\r\nRelevé sur une semaine :"
					+ String.format("\r\n   %-10s%-10s%-10s", "Jour", "Duré", "Mouvements");
			for (int i = 7; i >= 0; i--) {
				EventStat stat = eventStatRepository.findByDeviceAndDate(device,
						new java.sql.Date(date.minusDays(i).getMillis()));
				if (stat == null)
					continue;
				DateTime curDate = new DateTime(stat.getDate().getTime());
				DateTimeFormatter fmt = DateTimeFormat.forPattern("EEEE");
				stats += "\r\n" + (i == 0 ? "-> " : "   ");
				Period period2 = new Period(stat.getDuration().getTime());
				stats += String.format("%-10s%-10s%-10s", fmt.withLocale(Locale.FRENCH).print(curDate),
						period2.getHours() + "h" + period2.getMinutes() + "m", stat.getMvts());
			}

			email.concatBody(stats);

			email.send();
			String log = "Email report on " + user.getName() + " sent at: ";
			for (String recipient : recipients)
				log += recipient + " ";
			logger.info(log);
			return recipients;
		} catch (MessagingException e) {
			logger.error("Failed to send email for user " + user.getName() + ": " + e.getMessage());
			return null;
		}
	}

}
