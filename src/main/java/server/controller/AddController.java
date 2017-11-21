package server.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import server.database.model.AppiotRef;
import server.database.model.Device;
import server.database.model.Raspberry;
import server.database.model.Role;
import server.database.model.User;
import server.database.model.UserLink;
import server.database.repository.AppiotRefRepository;
import server.database.repository.DeviceRepository;
import server.database.repository.RaspberryRepository;
import server.database.repository.RoleRepository;
import server.database.repository.UserLinkRepository;
import server.database.repository.UserRepository;
import server.model.RaspberryLink;

@Controller
@RequestMapping(path = "/add")
public class AddController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserLinkRepository userLinkRepository;
	@Autowired
	private DeviceRepository deviceRepository;
	@Autowired
	private AppiotRefRepository appiotRefRepository;
	@Autowired
	private RaspberryRepository raspberryRepository;

	@GetMapping
	public String addIndex() {
		return "add-index";
	}

	@GetMapping(path = "/user")
	public String addUserForm(Model model) {
		model.addAttribute("user", new User());
		return "user";
	}

	@PostMapping(path = "/user")
	public @ResponseBody String AddUser(@ModelAttribute User user) {	 
		user.setBirth(user.getBirthStr());
		if (user.isSubject()) {
			if (user.getSleepStart() == null)
				user.setSleepStart("21:00");
			if (user.getSleepEnd() == null)
				user.setSleepEnd("7:00");
		} else {
			user.setSleepStart(null);
			user.setSleepEnd(null);
		}
		userRepository.save(user);
		logger.info("User added: " + user.getName());
		return "User added: " + user.getName();
	}

	@GetMapping(path = "/link")
	public String addUserLinkForm(Model model) {
		List<User> users = userRepository.findBySubject(false);
		List<User> subjects = userRepository.findBySubject(true);
		List<Role> allRoles = roleRepository.findAll();

		UserLink userLink = new UserLink();
		List<String> checkedRoles = new ArrayList<>();
		userLink.setCheckedRoles(checkedRoles);

		model.addAttribute("users", users);
		model.addAttribute("subjects", subjects);
		model.addAttribute("userLink", userLink);
		model.addAttribute("allRoles", allRoles);
		return "link";
	}

	@PostMapping(path = "/link")
	public @ResponseBody String AddUserLink(@ModelAttribute(name = "userLink") UserLink userLink) {
		User user = userRepository.findOne(userLink.getUserId());
		User subject = userRepository.findOne(userLink.getSubjectId());
		userLink.setSubject(subject);
		userLink.setUser(user);

		String ret = "Lien ajouté : " + user.getName();

		List<String> checkedRoles = userLink.getCheckedRoles();
		for (int i = 0; i < checkedRoles.size(); i++) {
			String roleStr = checkedRoles.get(i);
			Role role = roleRepository.findByName(roleStr);
			UserLink newLink = null;
			if (i > 0) {
				newLink = new UserLink();
				newLink.setUser(user);
				newLink.setSubject(subject);
				newLink.setRole(role);
			} else
				newLink = userLink;
				newLink.setRole(role);
			try {
				userLinkRepository.save(newLink);
			} catch (DataIntegrityViolationException e) {
				return "Ce lien existe déjà.";
			}
			ret += " -> " + roleStr;
		}
		ret += " -> " + subject.getName();
		logger.info(ret);
		return ret;
	}

	@GetMapping(path = "/device")
	public String addDeviceForm(Model model) {
		model.addAttribute("device", new Device());
		List<User> users = userRepository.findAll();
		model.addAttribute("users", users);
		return "device";
	}

	@PostMapping(path = "/device")
	public @ResponseBody String AddDevice(@ModelAttribute(name = "device") Device device) {
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
	public @ResponseBody String addRole(@ModelAttribute(name = "role") Role role) {
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
	
	@GetMapping(path="/raspberry")
	public String addRaspberryForm(Model model) {
		List<User> subjects = userRepository.findBySubject(true);
		List<Raspberry> raspberries = raspberryRepository.findAll();
		RaspberryLink raspberryLink = new RaspberryLink();
		model.addAttribute("subjects", subjects);
		model.addAttribute("raspberries", raspberries);
		model.addAttribute("raspberryLink", raspberryLink);
		return "raspberry";
	}
	
	@PostMapping(path="/raspberry")
	public @ResponseBody String addRaspberry(@ModelAttribute(name="link") RaspberryLink link) {
		System.out.println(link.getCreate());
		Raspberry raspberry = null;
		if (link.getCreate()) {
			raspberry = new Raspberry();
			raspberry.setId(Raspberry.randomId(raspberryRepository));
		} else {
			raspberry = raspberryRepository.findOne(link.getRaspberryId());
		}
		User user = userRepository.findOne(link.getUserId());
		boolean created = raspberry.addUser(user);
		if (!created) {
			return "Ce lien existe déjà";
		}
		try {
			raspberryRepository.save(raspberry);
		} catch (DataIntegrityViolationException e) {
			return "Ce lien ne peut pas être ajouté : " + user.getName() + " est déjà associé à un Raspberry.";
		}
		System.out.println(raspberry.getId() + " " + raspberry.getUsers().size());
		return raspberry.getId() + " -> " + user.getName();
	}
}
