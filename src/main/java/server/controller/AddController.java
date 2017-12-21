package server.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
import server.model.UserForm;

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
		List<Role> roles = roleRepository.findAll();
		model.addAttribute("roles", roles);
		model.addAttribute("userForm", new UserForm());
		return "user";
	}

	private String formatFirstName(String firstName) {
		String formatted = "";
		String[] names = firstName.split(" ");

		for (int i = 0; i < names.length; i++) {
			String name = names[i];
			if (i > 0)
				formatted += " ";
			formatted += name.substring(0, 1).toUpperCase();
			if (name.length() > 1)
				formatted += name.substring(1, name.length());
		}
		return formatted;
	}

	@PostMapping(path = "/user")
	public String AddUser(@ModelAttribute UserForm userForm) {
		User user = new User();
		if (userForm.getRoleStr().equals("ROLE_SUJET")) {
			user.setSubject(true);
			user.setSleepStart(userForm.getSleepStart().isEmpty() ? "22:00" : userForm.getSleepStart());
			user.setSleepEnd(userForm.getSleepEnd().isEmpty() ? "7:00" : userForm.getSleepEnd());
		} else
			user.setSubject(false);
		user.setFirstName(formatFirstName(userForm.getFirstName()));
		user.setLastName(userForm.getLastName().toUpperCase());
		if (userForm.getEmail() != null && !userForm.getEmail().isEmpty())
			user.setEmail(userForm.getEmail());
		user.setEnabled(true);
		if (userForm.getPassword() != null && !userForm.getPassword().isEmpty()) {
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(11);
			user.setPassword(encoder.encode(userForm.getPassword()));
		}
		Role role = roleRepository.findByName(userForm.getRoleStr());
		user.setRoles(Arrays.asList(role));
		try {
			userRepository.save(user);
		} catch (DataIntegrityViolationException e) {
			return "redirect:/add/user?error=email";
		} catch (Exception e) {
			return "redirect:/add/user?error&message=" + e.getMessage();
		}

		logger.info("User added: " + user.getName());
		return "redirect:/add/user?success";
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
	public String AddUserLink(@ModelAttribute(name = "userLink") UserLink userLink) {
		if (userLink.getCheckedRoles() == null || userLink.getCheckedRoles().isEmpty())
			return "redirect:/add/link?error=role";
		User user = userRepository.findOne(userLink.getUserId());
		User subject = userRepository.findOne(userLink.getSubjectId());
		userLink.setUser(user);
		userLink.setSubject(subject);

		String log = "Lien ajouté : " + user.getName();

		List<String> checkedRoles = userLink.getCheckedRoles();
		for (int i = 0; i < checkedRoles.size(); i++) {
			String roleStr = checkedRoles.get(i);
			Role role = roleRepository.findByName(roleStr);
			UserLink newLink = null;
			if (i > 0) {
				newLink = new UserLink();
				newLink.setUser(user);
				newLink.setSubject(subject);
			} else
				newLink = userLink;
			newLink.setRole(role);
			try {
				userLinkRepository.save(newLink);
			} catch (DataIntegrityViolationException e) {
				return "redirect:/add/link?error=duplicate";
			} catch (Exception e) {
				return "redirect:/add/link?error&message=" + e.getMessage();
			}
			log += " -> " + roleStr;
		}
		log += " -> " + subject.getName();
		logger.info(log);
		return "redirect:/add/link?success";
	}

	@GetMapping(path = "/device")
	public String addDeviceForm(Model model) {
		model.addAttribute("device", new Device());
		List<User> users = userRepository.findBySubject(true);
		model.addAttribute("users", users);
		return "device";
	}

	@PostMapping(path = "/device")
	public String AddDevice(@ModelAttribute(name = "device") Device device) {
		try {
			User user = userRepository.findOne(device.getUserId());
			device.setUser(user);
			device.setSerial(device.getSerialStr());
			deviceRepository.save(device);
			AppiotRef appiotRef = new AppiotRef(device, device.getGateway());
			appiotRefRepository.save(appiotRef);
			logger.info("Device added: " + device.getSerialStr() + " for user: " + device.getUserId());
		} catch (Exception e) {
			return "redirect:/add/device?error&message=" + e.getMessage();
		}
		return "redirect:/add/device?success";
	}

	@GetMapping(path = "/role")
	public String AddRoleForm(Model model) {
		model.addAttribute("role", new Role());
		return "role";
	}

	@PostMapping(path = "/role")
	public String addRole(@ModelAttribute(name = "role") Role role) {
		logger.debug("Received Role: name=" + role.getName());
		try {
			roleRepository.save(role);
		} catch (DataIntegrityViolationException e) {
			return "redirect:/add/role?error=duplicate";
		} catch (Exception e) {
			return "redirect:/add/role?error&message=" + e.getMessage();
		}
		return "redirect:/add/role?success";
	}

	@GetMapping(path = "/raspberry")
	public String addRaspberryForm(Model model) {
		List<User> subjects = userRepository.findBySubject(true);
		List<Raspberry> raspberries = raspberryRepository.findAll();
		RaspberryLink raspberryLink = new RaspberryLink();
		model.addAttribute("subjects", subjects);
		model.addAttribute("raspberries", raspberries);
		model.addAttribute("raspberryLink", raspberryLink);
		return "raspberry";
	}

	@PostMapping(path = "/raspberry")
	public String addRaspberry(@ModelAttribute(name = "link") RaspberryLink link) {
		User user = null;
		try {
			Raspberry raspberry = null;
			if (link.getCreate()) {
				raspberry = new Raspberry();
				raspberry.setId(Raspberry.randomId(raspberryRepository));
			} else {
				raspberry = raspberryRepository.findOne(link.getRaspberryId());
			}
			user = userRepository.findOne(link.getUserId());
			if (!raspberry.addUser(user))
				throw new DataIntegrityViolationException("");
			raspberryRepository.save(raspberry);
		} catch (DataIntegrityViolationException e) {
			return "redirect:/add/raspberry?error=duplicate&name=" + user.getName();
		} catch (Exception e) {
			return "redirect:/add/raspberry?error&message=" + e.getMessage();
		}
		return "redirect:/add/raspberry?success";
	}
}
