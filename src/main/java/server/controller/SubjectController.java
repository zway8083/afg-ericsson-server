package server.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import server.database.model.Role;
import server.database.model.User;
import server.database.model.UserLink;
import server.database.repository.RoleRepository;
import server.database.repository.UserLinkRepository;
import server.database.repository.UserRepository;
import server.model.SubjectForm;

@Controller
public class SubjectController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private UserLinkRepository userLinkRepository;


	@GetMapping(path = "/mysubjects")
	public String mysubjects(Authentication authentication, Model model) {
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

		
		model.addAttribute("subjects", subjects);
		model.addAttribute("form", new SubjectForm());
		return "mysubjects";
	}

	@PostMapping(path = "/mysubjects")
	public String mysubjectsForm(Authentication authentication, Model model,
			@ModelAttribute(name = "form") SubjectForm form) {
		
		@SuppressWarnings("unchecked")
		Collection<SimpleGrantedAuthority> authorities = (Collection<SimpleGrantedAuthority>) authentication
				.getAuthorities();
		
		User user = userRepository.findByEmail(authentication.getName());
		User subject = new User();
		 
		subject.setSubject(true);
		subject.setSleepStart("22:00");
		subject.setSleepEnd("7:00");
		subject.setEmailON("check");
		subject.setFirstName(formatFirstName(form.getFirstName()));
		subject.setLastName(form.getLastName().toUpperCase());
		subject.setEnabled(false);
		Role role = roleRepository.findByName("ROLE_SUJET");
		subject.setRoles(Arrays.asList(role));
		userRepository.save(subject);
		
		UserLink link = new UserLink(user, subject, roleRepository.findByName("ROLE_PARENT"));
	    userLinkRepository.save(link);


		return mysubjects(authentication, model);
	}
	
	
	    @PostMapping(path = "/mysubjects/{idSubject}/delete")
		public String deleteSubjectLink(Authentication authentication,Model model,
				@PathVariable("idSubject") long idSubject,final RedirectAttributes redirectAttributes) {
                
	    	@SuppressWarnings("unchecked")
			Collection<SimpleGrantedAuthority> authorities = (Collection<SimpleGrantedAuthority>) authentication
					.getAuthorities();
	    	
				User subject= userRepository.findOne(idSubject);
				
				userLinkRepository.delete(userLinkRepository.findBySubject(subject));
				
				
		    
	    	
	    	
	    	return "redirect:/mysubjects";

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
