package server.api;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import server.database.model.*;
import server.database.repository.*;
import server.model.EventRequest2;
import server.task.NewBoxRegisteredRunnable;

@Controller
@RequestMapping(path = "/api/event2")
public class SensorEventController2 {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private DeviceRepository deviceRepository;
	@Autowired
	private EventRepository eventRepository;
	@Autowired
	private SensorTypeRepository sensorTypeRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserLinkRepository userLinkRepository;
	@Autowired
	private RoleRepository roleRepository;

	@Value("${report.email.id}")
	private String emailId;
	@Value("${report.email.password}")
	private String emailPassword;
	@Value("${report.email.host}")
	private String emailHost;

	@GetMapping("/{id}")
	public ResponseEntity<Object> arduinoEvent(@PathVariable(name = "id", required = true) Long id) {
		Device device = deviceRepository.findOne(id);
		if (device == null)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		Event event = new Event();
		event.setType(sensorTypeRepository.findByName("motion"));
		//event.setDate(new Date());
		event.setDevice(device);
		eventRepository.save(event);
		return new ResponseEntity<>(HttpStatus.OK);
	}


	@PostMapping
	public ResponseEntity<String> sensorEvent2(@RequestBody EventRequest2 eventRequest) {
		logger.info("Event received: " + eventRequest.toString());
		if (!eventRequest.getType().equals("motion")) {
			logger.error("Bad event received: " + eventRequest.getType());
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}

		logger.info("Size of data : " + Integer.toString(eventRequest.getData().length));
		
		for (int i = 0; i < eventRequest.getData().length; i++) {
			try {
				Event event = new Event();
				//event.setDate(eventRequest.getDate());
				event.setBinValue(true);
				//event.setdValue(eventRequest.getdValue());
				event.setDate(eventRequest.getData()[i]);
				logger.info(eventRequest.getData()[i].toString());


				if (eventRequest.getData()[i].toString().equals("1970-01-01 01:00:42") ){
					Device device = deviceRepository.findBySerial(eventRequest.getSerial());
					User subject = userRepository.findById(device.getUser().getId());
					Role parent = roleRepository.findByName("ROLE_PARENT");
					List<UserLink> subjectRelated = userLinkRepository.findBySubjectAndRole(subject,parent);
					for (UserLink userparent : subjectRelated){
						logger.info("Le lien avec le sujet " + subject.getId() + " a pour parent " + userparent.getUser().getId() + "dont l'adresse email est " + userparent.getUser().getEmail());
						NewBoxRegisteredRunnable runnable = new NewBoxRegisteredRunnable(emailId, emailPassword, emailHost, userparent.getUser().getEmail(), userparent.getUser().getName(), subject.getName(), logger);
						Thread thread = new Thread(runnable);
						thread.start();}
				}


				SensorType sensorType = sensorTypeRepository.findByName(eventRequest.getType());
				event.setType(sensorType);
				Device device = deviceRepository.findBySerial(eventRequest.getSerial());
				event.setDevice(device);
				eventRepository.save(event);
				//return new ResponseEntity<String>(HttpStatus.CREATED);
			} catch (Exception e) {
				logger.error(e.getMessage());
				return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		return new ResponseEntity<String>(HttpStatus.CREATED);
		//return null;
	}
}
