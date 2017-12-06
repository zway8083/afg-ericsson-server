package server.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import server.database.model.User;
import server.database.repository.UserRepository;
import server.task.ForgotPasswordRunnable;
import server.utils.RandomStringGenerator;

@Controller
public class ForgotController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private UserRepository userRepository;
	
	@Value("${report.email.id}")
	private String emailId;
	@Value("${report.email.password}")
	private String emailPassword;
	@Value("${report.email.host}")
	private String emailHost;
	
	@GetMapping(path="/forgot")
	public String forgotForm() {
		return "forgot";
	}
	
	@PostMapping(path="/forgot")
	public String forgot(@ModelAttribute(name="email") String email) {
		User user = userRepository.findByEmail(email);
		if (user == null)
			return "redirect:/forgot?error";

		String rawPassword = RandomStringGenerator.randomString(10);
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(11);
		String password = encoder.encode(rawPassword);
		
		user.setPassword(password);
		userRepository.save(user);
		
		ForgotPasswordRunnable runnable = new ForgotPasswordRunnable(emailId, emailPassword, emailHost, rawPassword, email, logger);
		Thread thread = new Thread(runnable);
		thread.start();
		
		return "redirect:/login?forgot";
	}
}
