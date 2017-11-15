package server.task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

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
import server.database.model.UserLink;
import server.database.repository.EventRepository;
import server.database.repository.EventStatRepository;
import server.database.repository.SensorTypeRepository;
import server.database.repository.UserLinkRepository;
import server.utils.HTMLGenerator;

public class EventTask {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private SensorTypeRepository sensorTypeRepository;
	private EventRepository eventRepository;
	private EventStatRepository eventStatRepository;
	private UserLinkRepository userLinkRepository;

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
			EventRepository eventRepository, EventStatRepository eventStatRepository,
			UserLinkRepository userLinkRepository, String path) {
		this.device = device;
		this.date = date.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
		this.sensorTypeRepository = sensorTypeRepository;
		this.eventRepository = eventRepository;
		this.eventStatRepository = eventStatRepository;
		this.userLinkRepository = userLinkRepository;
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
			else if (eventStat.getGrade() == null) {
				updateEventStat();
			}
		}

	}
	
	public void updateEventStat() {
		EventStat eventMaxMvts = eventStatRepository.findFirstByOrderByMvtsDesc();
		EventStat eventMinMvts = eventStatRepository.findFirstByMvtsGreaterThanEqualOrderByMvtsAsc(5);
		if (eventMaxMvts == null || eventMinMvts == null) {
			eventStatRepository.save(eventStat);
			return;
		}
		Integer maxMvts = eventMaxMvts.getMvts();
		Integer minMvts = eventMinMvts.getMvts();
		System.out.println("Min=" + minMvts + ", Max=" + maxMvts);
		eventStat.setGrade(100 - (eventStat.getMvts() - minMvts) * 100 / (maxMvts - minMvts));
		System.out.println("Grade=" + eventStat.getGrade());
	}
	
	public void createEventStat() {
		eventStat = new EventStat();
		eventStat.setDate(date.getMillis());
		eventStat.setDevice(device);
		eventStat.setMvts(eventMotion.size());
		eventStat.setDuration(endNight.getMillis() - startNight.getMillis());
		updateEventStat();
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
		int margin = 0;
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

	private List<List<Event>> getEventLists() {
		logger.info("Requesting events for: " + endNight.toString("dd/MM/yyy") + " - " + user.getName());
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
			Email email = new Email(id, password, host, true);
			email.setSubject("Relevés de la nuit du " + startNight.toString("dd/MM/yyyy") + " au "
					+ endNight.toString("dd/MM/yyyy"));

			List<String> recipients = new ArrayList<>();

			if (user.getEmail() != null)
				recipients.add(user.getEmail());

			List<UserLink> links = userLinkRepository.findBySubject(user);
			for (UserLink userLink : links) {
				String address = userLink.getUser().getEmail();
				if (address != null && !recipients.contains(address))
					recipients.add(address);
			}

			if (recipients.isEmpty())
				return null;
			for (String recipient : recipients)
				email.addRecipient(recipient);

			if (files != null)
				email.addAttachments(files);

			Period period = new Period(eventStat.getDuration().getTime());

			String bodyHTML = HTMLGenerator.strongAttributeValue("Sujet", user.getName(), 0)
					+ HTMLGenerator.strongAttributeValue("Période",
							startNight.toString("dd/MM/yyyy HH:mm") + " - " + endNight.toString("dd/MM/yyyy HH:mm"), 0)
					+ HTMLGenerator.strongAttributeValue("Durée",
							period.getHours() + "h et " + period.getMinutes() + "m", 0)
					+ HTMLGenerator.strongAttributeValue("Score", String.valueOf(eventStat.getGrade()) + "%", 0)
					+ HTMLGenerator.strongAttributeValue("Nombre de mouvement", String.valueOf(eventMotion.size()), 0)
					+ HTMLGenerator.strongAttribute("Mouvements par tranche horaire", 0);

			DateTime time = startNight;
			while (time.isEqual(endNight) == false) {
				DateTime endTime = time.plusHours(1).withMinuteOfHour(0).withSecondOfMinute(0);
				if (endTime.isAfter(endNight))
					endTime = endNight;
				Long count = eventRepository.countByDeviceAndTypeAndBinValueAndDateBetween(device,
						sensorTypeRepository.findByName("motion"), true, time.toDate(), endTime.toDate());
				bodyHTML += HTMLGenerator.value(time.toString("HH:mm") + " - " + endTime.toString("HH:mm : ") + count,
						1);
				time = endTime;
			}

			Long count = eventRepository.countByDeviceAndTypeAndBinValueAndDateBetween(device,
					sensorTypeRepository.findByName("tamper"), true, startNight.toDate(), endNight.toDate());
			bodyHTML += HTMLGenerator.strongAttributeValue("Nombre de secousse", String.valueOf(count), 0);

			bodyHTML += HTMLGenerator.strongAttribute("Relevé sur une semaine", 0);
			ArrayList<ArrayList<String>> table = new ArrayList<>();
			ArrayList<String> list = new ArrayList<>(Arrays.asList("Jour", "Duré", "Mouvements"));
			table.add(list);
			for (int i = 7; i >= 0; i--) {
				EventStat stat = eventStatRepository.findByDeviceAndDate(device,
						new java.sql.Date(date.minusDays(i).getMillis()));
				if (stat == null)
					continue;
				DateTime curDate = new DateTime(stat.getDate().getTime());
				DateTimeFormatter fmt = DateTimeFormat.forPattern("EEEE");
				Period period2 = new Period(stat.getDuration().getTime());
				String day = (i == 0 ? "&bull; " : "") + fmt.withLocale(Locale.FRENCH).print(curDate);
				String daytime = period2.getHours() + "h" + period2.getMinutes() + "m";
				String mvts = String.valueOf(stat.getMvts());
				list = new ArrayList<>(Arrays.asList(day, daytime, mvts));
				table.add(list);
			}

			bodyHTML += HTMLGenerator.table(table, 1);

			System.out.println(bodyHTML);
			email.concatBody(bodyHTML);
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
