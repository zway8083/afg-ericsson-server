package server;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import server.database.model.Device;
import server.database.model.Event;
import server.database.model.User;
import server.database.repository.DeviceRepository;
import server.database.repository.EventRepository;
import server.database.repository.SensorTypeRepository;
import server.database.repository.UserRepository;

@Controller
@RequestMapping(path = "/get")
public class Get {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private DeviceRepository deviceRepository;
	@Autowired
	private EventRepository eventRepository;
	@Autowired
	private SensorTypeRepository sensorTypeRepository;

	@GetMapping(path = "/users")
	public @ResponseBody String all() {
		List<User> users = userRepository.findAll();
		String response = "";
		for (User user : users) {
			response += "<p>" + user.toString() + "</p>\n";
			List<Device> devices = deviceRepository.findByUser(user);
			for (Device device : devices) {
				response += "<p>-" + device.toString() + "</p>\n";
			}
			response += "\n";
		}
		return response;
	}
	
	@GetMapping(path="/events")
	public @ResponseBody String event() {
		Device device = deviceRepository.findOne(2l);
		Date d1 = new Date(1508556287000l);
		Date d2 = new Date(1508556793000l);
		List<Event> events = eventRepository.findByDeviceAndTypeAndDateBetween(device, sensorTypeRepository.findByName("motion"), d1, d2);
		String response = "";
		for (Event event : events) {
			response += "<p>" + event.toString() + "</p>\n";
		}
		return response;
	}
}
