package server.task;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import server.database.model.Device;
import server.database.repository.DeviceRepository;
import server.database.repository.EventRepository;
import server.database.repository.EventStatRepository;
import server.database.repository.SensorTypeRepository;
import server.database.repository.UserLinkRepository;

@Component
public class ReportScheduled {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Value("${report.path}")
	private String path;
	@Value("${report.email.id}")
	private String id;
	@Value("${report.email.password}")
	private String password;
	@Value("${report.email.host}")
	private String host;

	@Autowired
	private DeviceRepository deviceRepository;
	@Autowired
	private SensorTypeRepository sensorTypeRepository;
	@Autowired
	private EventRepository eventRepository;
	@Autowired
	private EventStatRepository eventStatRepository;
	@Autowired
	private UserLinkRepository userLinkRepository;

	@Scheduled(cron = "0 0 8 * * *")
	public void run() {
		logger.info("Report task begin");
		ExecutorService executorService = Executors.newFixedThreadPool(4);

		List<Device> devices = deviceRepository.findAll();
		for (Device device : devices) {
			if (device.getId() == 3)
				continue;
			ReportRunnable runnable = new ReportRunnable(path, device, new DateTime(), sensorTypeRepository,
					eventRepository, eventStatRepository, userLinkRepository, id, password, host);
			executorService.execute(runnable);
			logger.info("Started report for device id= " + device.getId());
		}
	}
}
