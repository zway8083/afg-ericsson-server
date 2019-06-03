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
	//@Scheduled(cron = "*/15 * * * * *") envoit tout les 15 secondes.
	public void run() throws NoUserForThisDeviceException {
		logger.info("Report task begin");
		ExecutorService executorService = Executors.newFixedThreadPool(4);
		List<User> userson = userRepository.findByEmailON(true);
		List<Device> devices = deviceRepository.findAll();
		for ( Device device : devices) {
			if (device.getUser() == null){
				logger.info("gestion de l'exception. On ne fait rien ");
				//throw new NoUserForThisDeviceException(device.getUser().getId());
			}
			else {
				logger.info( "device numéro " + device.getId() + " associé au user " + device.getUser().getId());
				for (User user : userson) {
					logger.info(user.getName() + " a le rapport coché et a l'ID " + user.getId());
					logger.info("On a le user " + user.getName() + " et le device" + device.getId() + " asssocié au user " + device.getUser().getId());
					logger.info("Comparaison entre ID user " + user.getId() + " et user_id " + device.getUser().getId());
					if (device.getUser().getId() == user.getId()) {
						if (device.getId() == 3)
							continue;
						ReportRunnable runnable = new ReportRunnable(path, device, new DateTime(), sensorTypeRepository,
							eventRepository, eventStatRepository, userLinkRepository, id, password, host);
						executorService.execute(runnable);
						logger.info("Started report for device id= " + device.getId());
					}
				}
			}
		}
	}
}
