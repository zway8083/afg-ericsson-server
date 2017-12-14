package server.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import server.database.model.Role;
import server.database.model.Suggestion;
import server.database.model.User;
import server.database.model.UserLink;
import server.database.repository.RoleRepository;
import server.database.repository.SuggestionRepository;
import server.database.repository.UserLinkRepository;
import server.database.repository.UserRepository;
import server.model.NightHoursForm;
import server.utils.DateConverter;

@Controller
public class SettingsController {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserLinkRepository userLinkRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private SuggestionRepository suggestionRepository;

	@GetMapping(path = "/settings")
	public String settings(Principal principal, Model model) {
		Role admin = roleRepository.findByName("ROLE_ADMIN");
		Role parent = roleRepository.findByName("ROLE_PARENT");
		User user = userRepository.findByEmail(principal.getName());
		List<User> subjects;

		if (user.getRoles().contains(admin)) {
			subjects = userRepository.findBySubject(true);
		} else {
			subjects = new ArrayList<>();
			List<UserLink> links = userLinkRepository.findByUserAndRole(user, parent);
			if (!links.isEmpty()) {
				for (UserLink link : links)
					subjects.add(link.getSubject());
			}
		}

		List<NightHoursForm> forms = new ArrayList<>();
		for (User subject : subjects) {
			NightHoursForm form = new NightHoursForm(subject.getId(), subject.getName(), DateConverter.toFormatTime(subject.getSleepStart()),
					DateConverter.toFormatTime(subject.getSleepEnd()));
			forms.add(form);
		}

		model.addAttribute("forms", forms);
		model.addAttribute("nightForm", new NightHoursForm());
		model.addAttribute("subjects", subjects);
		model.addAttribute("suggestionForm", new Suggestion());
		return "settings";
	}
	
	@PostMapping(path="/settings/sleep")
	public String setSleepHours(Principal principal, Model model, @ModelAttribute(name="nightForm") NightHoursForm form) {
		Role admin = roleRepository.findByName("ROLE_ADMIN");
		Role parent = roleRepository.findByName("ROLE_PARENT");
		User user = userRepository.findByEmail(principal.getName());
		User subject = userRepository.findOne(form.getSubjectId());
		if (user.getRoles().contains(admin) || userLinkRepository.countByUserAndSubjectAndRole(user, subject, parent) > 0) {
			subject.setSleepStart(DateConverter.toTime(form.getSleepStart()));
			subject.setSleepEnd(DateConverter.toTime(form.getSleepEnd()));
			userRepository.save(user);
		}
		
		return "redirect:/settings";
	}
	
	@PostMapping(path="/settings/suggestion")
	public String suggestion(Principal principal, @ModelAttribute(name="suggestionForm") Suggestion suggestion) {
		if (suggestion.getSuggestion() == null || suggestion.getSuggestion().isEmpty())
			return "redirect:/settings";
		User user = userRepository.findByEmail(principal.getName());
		suggestion.setUser(user);
		suggestionRepository.save(suggestion);
		return "redirect:/settings?suggestion";
	}
}
