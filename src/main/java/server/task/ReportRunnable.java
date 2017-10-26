package server.task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.mail.MessagingException;

import org.joda.time.DateTime;
import org.joda.time.Interval;
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

	public ReportRunnable() {
	}
	
	public ReportRunnable(String path, Device device, DateTime date, SensorTypeRepository sensorTypeRepository,
			EventRepository eventRepository, int task) {
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

	@Override
	public void run() {
		User user = device.getUser();
		try {
			DateTime startNight = date.minusDays(1).withHourOfDay(19);
			DateTime endNight = date.withHourOfDay(8);
			logger.info("Requesting events for: " + endNight.toString("dd/MM/yyy") + " - " + user.getFirstName() + " "
					+ user.getLastName());

			List<Event> eventMotion = eventRepository.findByDeviceAndTypeAndBinValueAndDateBetween(device,
					sensorTypeRepository.findByName("motion"), true, startNight.toDate(), endNight.toDate());
			List<Event> eventTemp = eventRepository.findByDeviceAndTypeAndDateBetween(device,
					sensorTypeRepository.findByName("temperature"), startNight.toDate(), endNight.toDate());
			List<Event> eventTamper = eventRepository.findByDeviceAndTypeAndDateBetween(device,
					sensorTypeRepository.findByName("tamper"), startNight.toDate(), endNight.toDate());
			List<Event> eventLum = eventRepository.findByDeviceAndTypeAndDateBetween(device,
					sensorTypeRepository.findByName("luminescence"), startNight.toDate(), endNight.toDate());
			List<List<Event>> eList = Arrays.asList(eventLum, eventMotion, eventTamper, eventTemp);

			ArrayList<String> files = null;
			if ((task & 1) > 0) {
				logger.info("Creating csv files for " + user.getName());
				files = createCsvReport(user, eList);
			}
			if ((task & 4) > 0) {
				try {
					Email email = new Email(id, password, host);
					email.setSubject("Relevés de la nuit du " + startNight.toString("dd/MM/yyyy") + " au "
							+ endNight.toString("dd/MM/yyyy"));
					email.concatBody("Sujet: " + user.getName());

					if (user.getEmail() != null)
						email.addRecipient(user.getEmail());
					Set<User> related = device.getUser().getRelated();
					if (related != null && related.isEmpty() == false)
						for (User usr : related)
							if (usr.getEmail() != null)
								email.addRecipient(usr.getEmail());
					
					if ((task & 5) > 0 && files != null)
						email.addAttachments(files);

					if ((task & 6) > 0) {
						String stats = "";
						stats += "Période : " + startNight.toString("dd/MM/yyyy HH:mm") + " - "
								+ endNight.toString("dd/MM/yyyy HH:mm");
						org.joda.time.Period period = new Interval(startNight, endNight).toPeriod();
						stats += "\nDurée :" + period.getHours() + ":" + period.getMinutes();
						stats += "\nNombre de mouvement : " + eventMotion.size();
						stats += "\nMouvements par tranche horaire :";

						DateTime time = startNight;
						while (time.isEqual(endNight) == false) {
							DateTime endTime = time.plusHours(1).withMinuteOfHour(0).withSecondOfMinute(0);
							if (endTime.isAfter(endNight))
								endTime = endNight;
							Long count = eventRepository.countByDeviceAndTypeAndBinValueAndDateBetween(device,
									sensorTypeRepository.findByName("motion"), true, time.toDate(), endTime.toDate());
							stats += "\n\t" + time.toString("HH:mm") + " - " + endTime.toString("HH:mm : ") + count;
							time = endTime;
						}
						
						Long count = eventRepository.countByDeviceAndTypeAndBinValueAndDateBetween(device,
								sensorTypeRepository.findByName("tamper"), true, startNight.toDate(),
								endNight.toDate());
						stats += "Nombre de secousses : " + count;
						
						email.concatBody(stats);
					}
					email.send();
				} catch (MessagingException e) {
					logger.error("Failed to send email for user " + user.getName() + ": " + e.getMessage());
				}
			}
		} catch (Exception e) {
			logger.error("Report error for " + user.getName() + ": ");
			e.printStackTrace();
		}
	}
}
