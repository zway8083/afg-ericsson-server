package server.api;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import server.database.model.Device;
import server.database.model.Event;
import server.database.model.SensorType;
import server.database.repository.DeviceRepository;
import server.database.repository.EventRepository;
import server.database.repository.SensorTypeRepository;
import server.model.EventRequest;

@Controller
@RequestMapping(path = "/api/event")
public class SensorEventController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private DeviceRepository deviceRepository;
	@Autowired
	private EventRepository eventRepository;
	@Autowired
	private SensorTypeRepository sensorTypeRepository;

	@GetMapping("/{id}")
	public ResponseEntity<Object> arduinoEvent(@PathVariable(name = "id", required = true) Long id) {
		Device device = deviceRepository.findOne(id);
		if (device == null)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		Event event = new Event();
		event.setType(sensorTypeRepository.findByName("motion"));
		event.setBinValue(true);
		event.setDate(new Date());
		event.setDevice(device);
		event.setUser(device.getUser());
		eventRepository.save(event);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity<String> sensorEvent(@RequestBody EventRequest eventRequest) {
		logger.info("Event received: " + eventRequest.toString());
		if (eventRequest.getdValue() != null && eventRequest.getBinValue() != null
				|| eventRequest.getBinValue() == null && eventRequest.getdValue() == null) {
			logger.error("Bad event received: " + eventRequest.toString());
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
		try {
			Event event = new Event();
			event.setDate(eventRequest.getDate());
			event.setBinValue(eventRequest.getBinValue());
			event.setdValue(eventRequest.getdValue());
			SensorType sensorType = sensorTypeRepository.findByName(eventRequest.getType());
			event.setType(sensorType);
			Device device = deviceRepository.findBySerial(eventRequest.getSerial());
			event.setDevice(device);
			eventRepository.save(event);
			return new ResponseEntity<String>(HttpStatus.CREATED);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
