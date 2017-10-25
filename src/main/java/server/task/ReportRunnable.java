package server.task;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.mail.MessagingException;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import server.database.model.Device;
import server.database.model.Event;
import server.database.model.User;
import server.database.repository.EventRepository;
import server.database.repository.SensorTypeRepository;

public class ReportRunnable implements Runnable {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private String path;
	private Device device;
	private DateTime date;
	private int task;

	private String id;
	private String password;
	private String host;
	
	private SensorTypeRepository sensorTypeRepository;
	private EventRepository eventRepository;

	public ReportRunnable(String path, Device device, DateTime date,
			SensorTypeRepository sensorTypeRepository, EventRepository eventRepository, int task) {
		super();
		this.path = path;
		this.device = device;
		this.date = date;
		this.sensorTypeRepository = sensorTypeRepository;
		this.eventRepository = eventRepository;
		this.task = task;
	}

	public ReportRunnable(String path, Device device, DateTime date, SensorTypeRepository sensorTypeRepository,
			EventRepository eventRepository, int task, String id, String password, String host) {
		this(path, device, date, sensorTypeRepository, eventRepository, task);
		this.id = id;
		this.password = password;
		this.host = host;
	}

	private ArrayList<String> createCsvReport(User user, List<List<Event>> eList) {
		ArrayList<String> fileNames = new ArrayList<>();
		for (List<Event> events : eList) {
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
						printWriter.printf("%1$tD %<tT,%2$d\n", event.getDate(), event.getBinValue() ? 1 : 0);
					}
				} else {
					for (Event event : events) {
						printWriter.printf("%1$tD %<tT,%2$f\n", event.getDate(), event.getdValue());
					}
				}
				printWriter.close();
				fileNames.add(fileName);
			} catch (IOException e) {
			}
		}
		return fileNames;
	}

	@Override
	public void run() {
		User user = device.getUser();
		try {
			DateTime startNight = date.minusDays(1).withHourOfDay(19);
			DateTime endNight = date.withHourOfDay(8);
			logger.info("Requesting events for: " + endNight.toString("dd/MM/yyy") + " - " + user.getFirstName() + " "
					+ user.getLastName());
			List<Event> eventMotion = eventRepository.findByDeviceAndTypeAndDateBetween(device,
					sensorTypeRepository.findByName("motion"), startNight.toDate(), endNight.toDate());
			List<Event> eventTemp = eventRepository.findByDeviceAndTypeAndDateBetween(device,
					sensorTypeRepository.findByName("temperature"), startNight.toDate(), endNight.toDate());
			List<Event> eventTamper = eventRepository.findByDeviceAndTypeAndDateBetween(device,
					sensorTypeRepository.findByName("tamper"), startNight.toDate(), endNight.toDate());
			List<Event> eventLum = eventRepository.findByDeviceAndTypeAndDateBetween(device,
					sensorTypeRepository.findByName("luminescence"), startNight.toDate(), endNight.toDate());
			List<List<Event>> eList = Arrays.asList(eventLum, eventMotion, eventTamper, eventTemp);

			ArrayList<String> files = null;
			if ((task & 1) > 0) {
				logger.info("Creating csv files for " + user.getFirstName() + " " + user.getLastName());
				files = createCsvReport(user, eList);
			}
			if ((task & 2) > 0) {
				try {
					Email email = new Email(id, password, host);
					if (files != null)
						email.addAttachments(files);
					email.setSubject("Rapport " + new DateTime().toString("dd/MM/yyyy"));
					email.setBody("Body");
					email.send();
				} catch (MessagingException e) {
					logger.error("Failed to send email to " + user.getFirstName() + " " + user.getLastName() + ": "
							+ e.getMessage());
				}
			}
		} catch (Exception e) {
			logger.error("Report error for " + user.getFirstName() + " " + user.getLastName() + ": ");
			e.printStackTrace();
		}
	}
}
