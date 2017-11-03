package server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import server.database.model.AppiotRef;
import server.database.model.Device;
import server.database.model.Role;
import server.database.model.User;
import server.database.repository.AppiotRefRepository;
import server.database.repository.DeviceRepository;
import server.database.repository.RoleRepository;
import server.database.repository.UserRepository;

@Controller
@RequestMapping(path = "/add")
public class Add {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private DeviceRepository deviceRepository;
	@Autowired
	private AppiotRefRepository appiotRefRepository;

	@GetMapping
	public String addIndex() {
		return "add-index";
	}

	@GetMapping(path = "/user")
	public String addUserForm(Model model) {
		User user = new User();
		List<String> checkedRoles = new ArrayList<>();
		user.setCheckedRoles(checkedRoles);
		model.addAttribute("user", user);

		List<Role> allRoles = roleRepository.findAll();
		model.addAttribute("allRoles", allRoles);

		List<User> users = userRepository.findAll();
		model.addAttribute("users", users);
		return "user";
	}

	@PostMapping(path = "/user")
	public @ResponseBody String AddUserResult(@ModelAttribute User user) {
		List<String> checkedRoles = user.getCheckedRoles();
		Set<Role> roles = new HashSet<>();
		for (String roleStr : checkedRoles) {
			Role role = roleRepository.findByName(roleStr);
			roles.add(role);
		}
		user.setRoles(roles);

		List<Long> ids = user.getRelatedIds();
		Set<User> related = new HashSet<>();
		for (Long id : ids) {
			User rel = userRepository.findOne(id);
			related.add(rel);
		}
		user.setRelated(related);

		user.setBirth(user.getBirthStr());
		userRepository.save(user);
		logger.info("User added: " + user.getFirstName() + " " + user.getLastName());
		return "User added: " + user.getFirstName() + " " + user.getLastName();
	}

	@GetMapping(path = "/device")
	public String addDeviceForm(Model model) {
		model.addAttribute("device", new Device());
		List<User> users = userRepository.findAll();
		model.addAttribute("users", users);
		return "device";
	}

	@PostMapping(path = "/device")
	public @ResponseBody String AddDeviceResult(@ModelAttribute(name = "device") Device device) {
		User user = userRepository.findOne(device.getUserId());
		device.setUser(user);
		device.setSerial(device.getSerialStr());
		deviceRepository.save(device);
		AppiotRef appiotRef = new AppiotRef(device, device.getGateway());
		appiotRefRepository.save(appiotRef);
		logger.info("Device added: " + device.getSerialStr() + " for user: " + device.getUserId());
		return "Device created";
	}

	@GetMapping(path = "/role")
	public String AddRoleForm(Model model) {
		model.addAttribute("role", new Role());
		return "role";
	}

	@PostMapping(path = "/role")
	public @ResponseBody String addRoleResult(@ModelAttribute(name = "role") Role role) {
		logger.debug("Received Role: name=" + role.getName());
		try {
			roleRepository.save(role);
		} catch (Exception e) {
			String message = "";
			Throwable cause = e.getCause();
			while (cause != null) {
				if (!message.isEmpty())
					message += ": ";
				message += cause.getMessage();
				cause = cause.getCause();
			}
			return message;
		}
		return "Rôle ajouté";
	}
}
