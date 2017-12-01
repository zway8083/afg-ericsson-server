package server.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import server.database.model.Device;
import server.database.model.Raspberry;
import server.database.model.Role;
import server.database.model.User;
import server.database.model.UserLink;
import server.database.repository.DeviceRepository;
import server.database.repository.RaspberryRepository;
import server.database.repository.UserLinkRepository;
import server.database.repository.UserRepository;

@Controller
@RequestMapping(path = "/get")
public class GetController {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private DeviceRepository deviceRepository;
	@Autowired
	private RaspberryRepository raspberryRepository;
	@Autowired
	private UserLinkRepository userLinkRepository;

	@GetMapping(path = "/users")
	public String all(Model model) {
		List<User> users = userRepository.findAll();
		HashMap<Long, User> userMap = new HashMap<>();
		HashMap<Long, List<Device>> devicesMap = new HashMap<>();
		HashMap<Long, Raspberry> raspberryMap = new HashMap<>();
		HashMap<Long, List<String>> linksMap = new HashMap<>();
		for (User user : users) {
			userMap.put(user.getId(), user);
			if (user.isSubject()) {
				List<Device> devices = deviceRepository.findByUser(user);
				devicesMap.put(user.getId(), devices);
				Raspberry raspberry = raspberryRepository.findOneByUsers_id(user.getId());
				raspberryMap.put(user.getId(), raspberry);
			} else {
				Collection<Role> roles = user.getRoles();
				List<String> linksStr = new ArrayList<>();
				for (Role role : roles) {
					List<UserLink> links = userLinkRepository.findByUserAndRole(user, role);
					for (UserLink userLink : links) {
						String link = userLink.getRole().getName() + " -> " + userLink.getSubject().getName();;
						linksStr.add(link);
					}
				}
				linksMap.put(user.getId(), linksStr);
			}
		}
		model.addAttribute("users", userMap);
		model.addAttribute("devices", devicesMap);
		model.addAttribute("raspberries", raspberryMap);
		model.addAttribute("linksMap", linksMap);
		return "get-users";
	}
}
