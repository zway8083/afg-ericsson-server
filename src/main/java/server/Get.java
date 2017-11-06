package server;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import server.database.model.Device;
import server.database.model.User;
import server.database.repository.DeviceRepository;
import server.database.repository.UserRepository;

@Controller
@RequestMapping(path = "/get")
public class Get {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private DeviceRepository deviceRepository;

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
}
