package server.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import server.database.model.User;
import server.database.repository.UserRepository;
import server.model.AccountForm;

@Controller
public class AccountController {
	@Autowired
	private UserRepository userRepository;
	
	@GetMapping(path="/account")
	public String account(Principal principal, Model model) {
		User user = userRepository.findByEmail(principal.getName());
		AccountForm form = new AccountForm();
		model.addAttribute("form", form);
		model.addAttribute("name", user.getName());
		model.addAttribute("email", user.getEmail());
		return "account";
	}
	
	@PostMapping(path="/account")
	public String accountForm(Principal principal, Model model, @ModelAttribute(name="form") AccountForm form) {
		User user = userRepository.findByEmail(principal.getName());
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(11);
		if (!encoder.matches(form.getOldPassword(), user.getPassword())) {
			form.setOldPassword("");
			model.addAttribute("form", form);
			return "redirect:/account?error";
		}
		user.setPassword(encoder.encode(form.getNewPassword()));
		userRepository.save(user);
		return "redirect:/account?success";
	}
}
