package server.controller;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import server.database.model.User;
import server.database.repository.RoleRepository;
import server.database.repository.UserLinkRepository;
import server.database.repository.UserRepository;
import server.model.AccountForm;

@Controller
public class AccountController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private UserRepository userRepository;

	@GetMapping(path = "/account")
	public String account(Principal principal, Model model) {
		User user = userRepository.findByEmail(principal.getName());
		AccountForm form = new AccountForm();
		model.addAttribute("form", form);
		model.addAttribute("name", user.getName());
		model.addAttribute("email", user.getEmail());
		model.addAttribute("Id", user.getId());
		return "account";
	}

	@PostMapping(path = "/account")
	public String accountForm(Principal principal, Model model, @ModelAttribute(name = "form") AccountForm form) {
		if (form.getNewPassword().length() < 6)
			return "redirect:/acount?error=length";
		User user = userRepository.findByEmail(principal.getName());
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(11);
		if (!encoder.matches(form.getOldPassword(), user.getPassword()))
			return "redirect:/account?error=mismatch";
		user.setPassword(encoder.encode(form.getNewPassword()));
		userRepository.save(user);
		return "redirect:/account?success";
	}
}