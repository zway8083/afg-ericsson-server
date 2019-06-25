package server.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import server.config.Security;
import server.database.model.Role;
import server.database.model.User;
import server.database.model.UserLink;
import server.database.repository.RoleRepository;
import server.database.repository.UserLinkRepository;
import server.database.repository.UserRepository;
import server.model.AccompanistForm;
import server.task.NewAccompanistRunnable;
import server.utils.RandomStringGenerator;

@Controller
public class AccompanistController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private UserLinkRepository userLinkRepository;

	@Value("${report.email.id}")
	private String emailId;
	@Value("${report.email.password}")
	private String emailPassword;
	@Value("${report.email.host}")
	private String emailHost;

	@GetMapping(path = "/accompanist")
	public String accompanist(Authentication authentication, Model model) {
		@SuppressWarnings("unchecked")
		Collection<SimpleGrantedAuthority> authorities = (Collection<SimpleGrantedAuthority>) authentication
				.getAuthorities();
		
		List<User> subjects;
		
		if (authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
			subjects = userRepository.findBySubject(true);
		} else {
			subjects = new ArrayList<>();
			String email = authentication.getName();
			User curUser = userRepository.findByEmail(email);

			Role parent = roleRepository.findByName("ROLE_PARENT");
			if (curUser.getRoles().contains(parent)) {
				List<UserLink> userLinks = userLinkRepository.findByUserAndRole(curUser, parent);
				for (UserLink userLink : userLinks)
					subjects.add(userLink.getSubject());
			}
		}

		Hashtable<Long, List<User>> hashtable = new Hashtable<>();
		for (User subject : subjects) {
			List<UserLink> subjectLinks = userLinkRepository.findBySubject(subject);
			String email = authentication.getName();
			User curUser = userRepository.findByEmail(email);
			List<User> accompanists = new ArrayList<>();
			for (UserLink userLink : subjectLinks)
				if (!accompanists.contains(userLink.getUser()) && !(userLink.getUser().equals(curUser)))
				accompanists.add(userLink.getUser());
			hashtable.put(subject.getId(), accompanists);
		}

		List<Role> roles = roleRepository.findAll();
		roles.remove(roleRepository.findByName("ROLE_ADMIN"));
		roles.remove(roleRepository.findByName("ROLE_SUJET"));
		
		model.addAttribute("roles", roles);
		
		model.addAttribute("subjects", subjects);
		model.addAttribute("hashtable", hashtable);
		model.addAttribute("form", new AccompanistForm());
		return "accompanist";
	}

	@PostMapping(path = "/accompanist")
	public String accompanistForm(Authentication authentication, Model model,
			@ModelAttribute(name = "form") AccompanistForm form) {

		@SuppressWarnings("unchecked")
		Collection<SimpleGrantedAuthority> authorities = (Collection<SimpleGrantedAuthority>) authentication
				.getAuthorities();

		String name;
		if (authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")))
			name = "Un administrateur";
		else {
			name = userRepository.findByEmail(authentication.getName()).getName();
		}
		Security security= new Security(userRepository,userLinkRepository);

		User subject = userRepository.findOne(form.getSubjectId());
		if(!security.checkAutority(authentication,subject)){
			logger.info("redirected");
			return "redirect:/logout";
		}
		logger.info("not redirected");
		String accmpEmail = form.getEmail();
		String rawPassword = null;
		User accompanist = userRepository.findByEmail(accmpEmail);
		if (accompanist == null) {
			
			Role role = roleRepository.findByName("ROLE_PARENT");
			logger.info("role du nouveau nommé : " + form.getRoleStr());
			List<Role> roles = Arrays.asList(role);

			rawPassword = RandomStringGenerator.randomString(10);
			logger.debug("password du nouvel accompagnateur " + rawPassword);
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(11);
			String password = encoder.encode(rawPassword);

			User newAccompanist = new User();
			newAccompanist.setFirstName(form.getFirstName());
			newAccompanist.setLastName(form.getLastName());
			newAccompanist.setEmail(form.getEmail());
			newAccompanist.setPassword(password);
			newAccompanist.setEnabled(true);
			newAccompanist.setSubject(false);
			newAccompanist.setRoles(roles);
			userRepository.save(newAccompanist);

			Role role2 = roleRepository.findByName(form.getRoleStr());
			logger.info("role du nouveau nommé : " + form.getRoleStr());
			List<Role> roles2 = Arrays.asList(role2);

			UserLink link = new UserLink(newAccompanist, subject, role2);
			userLinkRepository.save(link);
			
			
			NewAccompanistRunnable runnable = new NewAccompanistRunnable(emailId, emailPassword, emailHost, accmpEmail,
					rawPassword, name, subject.getName(), logger);
			Thread thread = new Thread(runnable);
			thread.start();
		}
		else {

			List<User> accompanists = new ArrayList<>();
			List<UserLink> subjectLinks = userLinkRepository.findBySubject(subject);
				
			for (UserLink userLink : subjectLinks)
				accompanists.add(userLink.getUser());
			
		    if (!accompanists.contains(accompanist)){
		    	
		    	Role role = roleRepository.findByName(form.getRoleStr());
			    UserLink link = new UserLink(accompanist, subject, role);
			    userLinkRepository.save(link);
			
		    
			    NewAccompanistRunnable runnable = new NewAccompanistRunnable(emailId, emailPassword, emailHost, accmpEmail,
						rawPassword, name, subject.getName(), logger);
				Thread thread = new Thread(runnable);
				thread.start();
		    }
		    
		}

		

		return accompanist(authentication, model);
	}
	
	
	    @PostMapping(path = "/accompanist/{idAccompanist}/{idSubject}/delete")
		public String deleteAccompanistLink(Authentication authentication,Model model,
				@PathVariable("idAccompanist") long idAccompanist,@PathVariable("idSubject") long idSubject, final RedirectAttributes redirectAttributes) {
                
	    	@SuppressWarnings("unchecked")
			Collection<SimpleGrantedAuthority> authorities = (Collection<SimpleGrantedAuthority>) authentication
					.getAuthorities();
	    	
				logger.debug("delete accompanist : {}", idAccompanist);
				User subject= userRepository.findOne(idSubject);
				User accompanist= userRepository.findOne(idAccompanist);
				Security security=new Security(userRepository,userLinkRepository);
				if(security.checkAutority(authentication,subject)){
					userLinkRepository.delete(userLinkRepository.findBySubjectAndUser(subject, accompanist));
					redirectAttributes.addFlashAttribute("css", "success");
					redirectAttributes.addFlashAttribute("msg", "User is deleted!");
				}


	    	return "redirect:/accompanist";

			}
	
	
	
	
}
