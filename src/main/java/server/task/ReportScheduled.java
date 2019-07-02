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
import server.database.model.User;
import server.database.repository.*;
import server.exception.NoUserForThisDeviceException;

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
	@Autowired
	private UserRepository userRepository;


	@Scheduled(cron = "0 0 8 * * *")
	//@Scheduled(cron = "*/30 * * * * *") //envoit tout les 15 secondes.


	public void run() {
		logger.info("Report task begin");
		ExecutorService executorService = Executors.newFixedThreadPool(4);

		//List<Device> devices = deviceRepository.findAll();
		List<User> users = userRepository.findBySubject(true);
		for (User user : users) {
			ReportRunnable runnable = new ReportRunnable(path, user, new DateTime(), sensorTypeRepository,
					eventRepository, eventStatRepository, userLinkRepository, id, password, host);
			executorService.execute(runnable);
			logger.info("Started report for user id= " + user.getId());
		}
	}
}
