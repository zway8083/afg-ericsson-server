package server.controller;


import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import server.database.model.Role;
import server.database.model.User;
import server.database.model.UserLink;
import server.database.repository.RoleRepository;
import server.database.repository.UserLinkRepository;
import server.database.repository.UserRepository;
import server.model.SignUpForm;
import server.task.ForgotPasswordRunnable;

@Controller
public class SignUpController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private UserLinkRepository userLinkRepository;
	@Autowired
	AuthenticationManager authenticationManager;
	private Hashtable<String, String> hashtable = new Hashtable<>();
	@Value("${report.email.id}")
	private String emailId;
	@Value("${report.email.password}")
	private String emailPassword;
	@Value("${report.email.host}")
	private String emailHost;
	

	
	@GetMapping(path="/signup")
	public String signUpForm( Model model) {
		
	model.addAttribute("form", new SignUpForm());
		return "signup";
	}
	
	@PostMapping(path = "/signup")
	public String signUp(Model model, @ModelAttribute(name = "form") SignUpForm form) {
		User user = userRepository.findByEmail(form.getEmail());
		if (user != null)
			return "redirect:/signup?error";
        
		SecureRandom random = new SecureRandom();
		int result = random.nextInt(1000000);
		String resultStr = result + "";
		if (resultStr.length() != 6) 
		 for (int x = resultStr.length(); x < 6; x++) resultStr = "0" + resultStr;
		
		String e=new String(form.getEmail());
    	hashtable.put(e, resultStr);
		
		ForgotPasswordRunnable runnable = new ForgotPasswordRunnable(emailId, emailPassword, emailHost, resultStr, form.getEmail());
		Thread thread = new Thread(runnable);
		thread.start();
		
		logger.info("Sent new code: "+resultStr+ " to: " + form.getEmail());
		
		return "validatecode";
	}
	
	@PostMapping(path="/validatecode")
	public String forgotc(Model model,@ModelAttribute(name="code") String code, @ModelAttribute(name = "form") SignUpForm form) {
		
		
		System.out.println("Merouane: " +hashtable.get(form.getEmail()).toString());
		if (!this.hashtable.get(form.getEmail()).equals(code))
		{
			model.addAttribute("error",true);
			return "validatecode";
		}
		
		User user = new User();
		user.setSubject(false);
		user.setFirstName(formatFirstName(form.getName()));
		user.setLastName(form.getSurname().toUpperCase());
		if (form.getEmail() != null && !form.getEmail().isEmpty())
			user.setEmail(form.getEmail());
		user.setEnabled(true);
		if (form.getPassword() != null && !form.getPassword().isEmpty()) {
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(11);
			user.setPassword(encoder.encode(form.getPassword()));
		}
		Role role = roleRepository.findByName("ROLE_PARENT");
		user.setRoles(Arrays.asList(role));
	    userRepository.save(user);
		
	    UsernamePasswordAuthenticationToken authReq
		 = new UsernamePasswordAuthenticationToken(form.getEmail(), form.getPassword());
		Authentication auth = authenticationManager.authenticate(authReq);
		SecurityContext sc = SecurityContextHolder.getContext();
		sc.setAuthentication(auth);
	
      
	return "addsubject";
	
	}
	
	@PostMapping(path="/addsubject")
	public String addsub(Authentication authentication,Model model,@ModelAttribute(name="name") String name, @ModelAttribute(name = "surname") String surname) {

		User user = userRepository.findByEmail(authentication.getName());
		User subject = new User();
		 
		subject.setSubject(true);
		subject.setSleepStart("22:00");
		subject.setSleepEnd("7:00");
		subject.setFirstName(formatFirstName(name));
		subject.setLastName(surname.toUpperCase());
		subject.setEnabled(false);
		Role role = roleRepository.findByName("ROLE_SUJET");
		subject.setRoles(Arrays.asList(role));
		userRepository.save(subject);
		
		UserLink link = new UserLink(user, subject, roleRepository.findByName("ROLE_PARENT"));
	    userLinkRepository.save(link);
 
	return "redirect:/login";
	
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
	
}
