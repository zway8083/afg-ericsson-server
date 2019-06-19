package server.task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
import server.database.model.SensorType;
import server.database.model.User;
import server.database.model.UserLink;
import server.database.repository.EventRepository;
import server.database.repository.EventStatRepository;
import server.database.repository.SensorTypeRepository;
import server.database.repository.UserLinkRepository;
import server.exception.MissingSleepTimesException;
import server.exception.NoMotionException;
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
	private DateTime startTheo;
	private DateTime endTheo;
	private DateTime startNight;
	private DateTime endNight;
	private List<Event> eventMotion;
	private List<Event> eventTemp;
	private List<Event> eventTamper;
	private List<Event> eventLum;
	private List<List<Event>> eventLists;
	private String path;
	private EventStat eventStat;

	private static final int MARGIN_END = 3;
	private static final int MARGIN_INIT = 1;
	private static final int WAKEUP_EVENT_INTERVAL = 10 * 60 * 1000; // Milliseconds
	private static final int WAKEUP_GROUP_SIZE = 5;
	private static final int MONTH_GRADE_INTERVAL = 3;

	public EventTask(Device device, DateTime date, SensorTypeRepository sensorTypeRepository,
			EventRepository eventRepository, EventStatRepository eventStatRepository,
			UserLinkRepository userLinkRepository, String path) throws MissingSleepTimesException, NoMotionException {
		this.device = device;
		this.date = date.withMillisOfDay(0);
		this.sensorTypeRepository = sensorTypeRepository;
		this.eventRepository = eventRepository;
		this.eventStatRepository = eventStatRepository;
		this.userLinkRepository = userLinkRepository;
		this.path = path;
		user = device.getUser();

		if (user.getSleepStart() == null || user.getSleepEnd() == null)
			throw new MissingSleepTimesException(user.getId());
		DateTime timeSleepStart = new DateTime(user.getSleepStart().getTime());
		DateTime timeSleepEnd = new DateTime(user.getSleepEnd().getTime());

		// Given theoretical start/end of night
		startTheo = date.minusDays(1).withHourOfDay(timeSleepStart.getHourOfDay())
				.withMinuteOfHour(timeSleepStart.getMinuteOfHour());
		endTheo = date.withHourOfDay(timeSleepEnd.getHourOfDay()).withMinuteOfHour(timeSleepEnd.getMinuteOfHour());
		
		SensorType motion = sensorTypeRepository.findByName("motion");
		eventStat = eventStatRepository.findByDeviceAndDate(device, date.toDate());
		if (eventRepository.countByDeviceAndTypeAndBinValueAndDateBetween(device, motion, true, startTheo.toDate(),
				endTheo.toDate()) < 3)
		{
		    
		     if (eventStat ==null) {
		    	 
		    		throw new NoMotionException(user.getId(), startTheo, endTheo);
		     }  
		}
		else {
			if (eventStat == null) {
				setNightLimits();
				if (startNight == null || endNight == null)
					eventLists = null;
				else {
					eventLists = getEventLists();
					createEventStat();
				}
			} else {
				startNight = new DateTime(eventStat.getStartNight().getTime());
				endNight = new DateTime(eventStat.getEndNight().getTime());
				eventLists = getEventLists();
			}
			
			
		}
	}

	private void setEventStatGrade() {
		Date dateMin = date.minusMonths(MONTH_GRADE_INTERVAL).toDate();
		Date dateMax = date.plus(1).toDate();
		EventStat eventMaxMvts = eventStatRepository.findFirstByDateBetweenOrderByMvtsDesc(dateMin, dateMax);
		EventStat eventMinMvts = eventStatRepository
				.findFirstByDateBetweenAndMvtsGreaterThanEqualOrderByMvtsAsc(dateMin, dateMax, 3);
		if (eventMaxMvts == null || eventMinMvts == null) {
			eventStat.setGrade(100);
		} else {
			Integer mvts = eventStat.getMvts();
			Integer maxMvts = eventMaxMvts.getMvts() > mvts ? eventMaxMvts.getMvts() : mvts;
			Integer minMvts = eventMinMvts.getMvts() < mvts ? eventMinMvts.getMvts() : mvts;
			eventStat.setGrade(100 - (mvts - minMvts) * 100 / (maxMvts - minMvts));
		}
	}

	private void createEventStat() {
		eventStat = new EventStat();
		eventStat.setDate(date.getMillis());
		eventStat.setDevice(device);
		eventStat.setMvts(eventMotion.size());
		eventStat.setStartNight(new java.sql.Date(startNight.getMillis()));
		eventStat.setEndNight(new java.sql.Date(endNight.getMillis()));
		eventStat.setDuration(endNight.getMillis() - startNight.getMillis());
		eventStat.setUser(user);
		setEventStatGrade();
		eventStatRepository.save(eventStat);
	}

	private void setNightLimits() {
		logger.info("Finding " + date.toString("dd/MM/yyyy") + " night limits for user id=" + user.getId());

		DateTime timeLimit = startTheo.plusHours(MARGIN_INIT);
		List<Event> movEvents = eventRepository.findByDeviceAndTypeAndDateBetweenOrderByDateDesc(device,
				sensorTypeRepository.findByName("motion"), startTheo.toDate(), timeLimit.toDate());
		startNight = findNightLimit(startTheo, movEvents);

		timeLimit = endTheo.plusHours(MARGIN_END);
		movEvents = eventRepository.findByDeviceAndTypeAndDateBetweenOrderByDateAsc(device,
				sensorTypeRepository.findByName("motion"), endTheo.toDate(), timeLimit.toDate());
		endNight = findNightLimit(endTheo, movEvents);

		logger.info("Found limits (id=" + user.getId() + "): " + "start=" + startNight.toString("dd/MM/yyyy HH:mm")
				+ ", end=" + endNight.toString("dd/MM/yyyy HH:mm"));
	}

	private DateTime findNightLimit(DateTime timeTheo, List<Event> eventsMov) {
		int i = 0;
		int size = eventsMov.size();

		ArrayList<ArrayList<Event>> eventLists = new ArrayList<>();
		ArrayList<Event> list = new ArrayList<>();

		while (i < size) {
			list.add(eventsMov.get(i));
			while (i + 1 < size && (eventsMov.get(i + 1).getDate().getTime()
					- eventsMov.get(i).getDate().getTime()) < WAKEUP_EVENT_INTERVAL) {
				list.add(eventsMov.get(i + 1));
				i++;
			}
			eventLists.add(list);
			list = new ArrayList<>();
			i++;
		}

		for (ArrayList<Event> events : eventLists) {
			if (events.size() >= WAKEUP_GROUP_SIZE)
				return new DateTime(events.get(0).getDate());
		}

		return timeTheo;
	}

	private List<List<Event>> getEventLists() {
		logger.info("Requesting events for: " + endNight.toString("dd/MM/yyy") + " - " + user.getName());
		eventMotion = eventRepository.findByDeviceAndTypeAndBinValueAndDateBetween(device,
				sensorTypeRepository.findByName("motion"), true, startNight.toDate(), endNight.toDate());
		eventTemp = eventRepository.findByDeviceAndTypeAndDateBetweenOrderByDateAsc(device,
				sensorTypeRepository.findByName("temperature"), startNight.toDate(), endNight.toDate());
		eventTamper = eventRepository.findByDeviceAndTypeAndDateBetweenOrderByDateAsc(device,
				sensorTypeRepository.findByName("tamper"), startNight.toDate(), endNight.toDate());
		eventLum = eventRepository.findByDeviceAndTypeAndDateBetweenOrderByDateAsc(device,
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

	public String createHTMLBody() {
		Period period = new Period(eventStat.getDuration().getTime());
		DateTime dateTime = new DateTime(eventStat.getDate());
		startNight= new DateTime(eventStat.getStartNight());
		endNight= new DateTime(eventStat.getEndNight());

		String bodyHTML = HTMLGenerator.strongAttributeValue("Sujet", user.getName(), 0)
				+ HTMLGenerator.strongAttributeValue("Période",
						"nuit du " + dateTime.minusDays(1).toString("dd/MM/yyyy") + " au "
								+ dateTime.toString("dd/MM/yyyy"),
						0)
				+ HTMLGenerator.strongAttributeValue("Score", String.valueOf(eventStat.getGrade()) + "%", 0)
				+ HTMLGenerator.strongAttributeValue("Nombre de mouvement", String.valueOf(eventStat.getMvts()), 0)
				+ HTMLGenerator.strongAttribute("Valeurs éstimées", 0)
				+ HTMLGenerator.strongAttributeValue("Endormissement", startNight.toString("HH:mm"), 1)
				+ HTMLGenerator.strongAttributeValue("Réveil",endNight.toString("HH:mm") , 1) + HTMLGenerator
						.strongAttributeValue("Durée", period.getHours() + "h et " + period.getMinutes() + "m", 1)
				+ HTMLGenerator.strongAttribute("Mouvements par tranche horaire", 0);

		dateTime = startTheo;
		while (dateTime.isEqual(endNight) == false) {
			DateTime endTime = dateTime.plusHours(1).withMinuteOfHour(0).withSecondOfMinute(0);
			if (endTime.isAfter(endNight))
				endTime = endNight;
			Long count = eventRepository.countByDeviceAndTypeAndBinValueAndDateBetween(device,
					sensorTypeRepository.findByName("motion"), true, dateTime.toDate(), endTime.toDate());
			bodyHTML += HTMLGenerator.value(dateTime.toString("HH:mm") + " - " + endTime.toString("HH:mm : ") + count,
					1);
			dateTime = endTime;
		}

		Long count = eventRepository.countByDeviceAndTypeAndBinValueAndDateBetween(device,
				sensorTypeRepository.findByName("tamper"), true, startNight.toDate(), endNight.toDate());
		bodyHTML += HTMLGenerator.strongAttributeValue("Nombre de secousse", String.valueOf(count), 0);

		bodyHTML += HTMLGenerator.strongAttribute("Relevé sur une semaine", 0);
		ArrayList<ArrayList<String>> table = new ArrayList<>();
		ArrayList<String> list = new ArrayList<>(Arrays.asList("Date", "Durée", "Mouvements", "Score"));
		table.add(list);
		for (int i = 7; i >= 0; i--) {
			EventStat stat = eventStatRepository.findByDeviceAndDate(device,
					new java.sql.Date(date.minusDays(i).getMillis()));
			if (stat == null)
				continue;
			DateTime curDate = new DateTime(stat.getDate().getTime());
			DateTimeFormatter fmt = DateTimeFormat.forPattern("EEEE dd/MM");
			Period period2 = new Period(stat.getDuration().getTime());
			String day = (i == 0 ? "&bull; " : "") + fmt.withLocale(Locale.FRENCH).print(curDate);
			String daytime = period2.getHours() + "h" + period2.getMinutes() + "m";
			String mvts = String.valueOf(stat.getMvts());
			String grade = stat.getGrade() != null ? String.valueOf(stat.getGrade()) + "%" : "";
			list = new ArrayList<>(Arrays.asList(day, daytime, mvts, grade));
			table.add(list);
		}

		bodyHTML += HTMLGenerator.table(table, 1);
		//bodyHTML += HTMLGenerator.strongAttributeValue("Niveau de la Batterie", String.valueOf(eventStat.getMvts()) +"%", 0);

		return bodyHTML;
	}

	public List<String> sendEmail(List<String> recipients, String id, String password, String host,
			ArrayList<String> files) {
		try {
			Email email = new Email(id, password, host, true);
			email.setSubject("Relevés de la nuit du " + startNight.toString("dd/MM/yyyy") + " au "
					+ endNight.toString("dd/MM/yyyy"));

			if (recipients.isEmpty())
				return null;
			for (String recipient : recipients)
				email.addRecipient(recipient);

			if (files != null)
				email.addAttachments(files);

			email.concatBody(createHTMLBody());
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

	public List<String> sendEmail(String id, String password, String host, ArrayList<String> files) {
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
		return sendEmail(recipients, id, password, host, files);
	}
}
