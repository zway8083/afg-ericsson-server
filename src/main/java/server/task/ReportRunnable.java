package server.task;

import java.util.List;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import server.database.model.Device;
import server.database.repository.EventRepository;
import server.database.repository.EventStatRepository;
import server.database.repository.SensorTypeRepository;

public class ReportRunnable implements Runnable {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private String path;
	private Device device;
	private DateTime date;

	private String id;
	private String password;
	private String host;

	private SensorTypeRepository sensorTypeRepository;
	private EventRepository eventRepository;
	private EventStatRepository eventStatRepository;

	public ReportRunnable() {
	}

	public ReportRunnable(String path, Device device, DateTime date, SensorTypeRepository sensorTypeRepository,
			EventRepository eventRepository, EventStatRepository eventStatRepository, String id, String password,
			String host) {
		this.path = path;
		this.device = device;
		this.date = date;
		this.sensorTypeRepository = sensorTypeRepository;
		this.eventRepository = eventRepository;
		this.eventStatRepository = eventStatRepository;
		this.id = id;
		this.password = password;
		this.host = host;
	}

	@Override
	public void run() {
		try {
			EventTask eventTask = new EventTask(device, date, sensorTypeRepository, eventRepository,
					eventStatRepository, path);
			List<String> recipients = eventTask.sendEmail(id, password, host, eventTask.createCsvReport());
			if (recipients == null)
				logger.warn("Nothing sent for device id = " + device.getId());
		} catch (Exception e) {
			logger.error("Report error for device id = " + device.getId() + ": " + e);
		}
	}
}
