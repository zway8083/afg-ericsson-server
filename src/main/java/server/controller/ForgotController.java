package server.controller;

import java.security.SecureRandom;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
	@Autowired
	AuthenticationManager authenticationManager;
	private Hashtable<String, String> hashtable = new Hashtable<>();
	
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
	public String forgot(Model model,@ModelAttribute(name="email") String email) {
		User user = userRepository.findByEmail(email);
		if (user == null)
			return "redirect:/forgot?error";
        
	     
		SecureRandom random = new SecureRandom();
		int result = random.nextInt(1000000);
		String resultStr = result + "";
		if (resultStr.length() != 6) 
		 for (int x = resultStr.length(); x < 6; x++) resultStr = "0" + resultStr;
		
		String e=new String(email);
    	hashtable.put(e, resultStr);
		
		ForgotPasswordRunnable runnable = new ForgotPasswordRunnable(emailId, emailPassword, emailHost, resultStr, email);
		Thread thread = new Thread(runnable);
		thread.start();
		
		
		
		
		logger.info("Sent new code to: " + email);
		
		model.addAttribute("email", email);	
		return "forgotcode";
	}
	
	@PostMapping(path="/forgotcode")
	public String forgotc(Model model,@ModelAttribute(name="code") String code,@ModelAttribute(name="email") String email) {
		
		System.out.println("Merouane: " +hashtable.get(email).toString());
		
		if (!this.hashtable.get(email).equals(code))
		{
			model.addAttribute("error",true);
			return "forgotcode";
		}
		//orienter vers une page de change mot de pass 
	
	return "changepassword";
	}
	
	@PostMapping(path="/changepassword")
	public String changepassword(Model model,@ModelAttribute(name="code") String code,@ModelAttribute(name="email") String email,@ModelAttribute(name="newPassword") String password) {
		
		if (this.hashtable.get(email).equals(code))
		{
			User user = userRepository.findByEmail(email);
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(11);
			user.setPassword(encoder.encode(password));
			userRepository.save(user);	
		}
		
		UsernamePasswordAuthenticationToken authReq
		 = new UsernamePasswordAuthenticationToken(email, password);
		Authentication auth = authenticationManager.authenticate(authReq);
		SecurityContext sc = SecurityContextHolder.getContext();
		sc.setAuthentication(auth);
	
	return "redirect:/login";
	}
	
	
	
}
