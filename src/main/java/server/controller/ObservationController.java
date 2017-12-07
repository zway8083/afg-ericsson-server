package server.controller;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import server.database.model.Description;
import server.database.model.Observation;
import server.database.model.ObservationPK;
import server.database.model.Time;
import server.database.model.User;
import server.database.model.UserLink;
import server.database.repository.DescriptionRepository;
import server.database.repository.ObservationRepository;
import server.database.repository.TimeRepository;
import server.database.repository.UserLinkRepository;
import server.database.repository.UserRepository;
import server.model.InitObservationForm;
import server.model.ObservationForm;
import server.utils.DateConverter;

@Controller
public class ObservationController {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private TimeRepository timeRepository;
	@Autowired
	private ObservationRepository observationRepository;
	@Autowired
	private DescriptionRepository descriptionRepository;
	@Autowired
	private UserLinkRepository userLinkRepository;

	@GetMapping(path = "/observation")
	public String initObservation(Authentication authentication, Model model) {
		@SuppressWarnings("unchecked")
		Collection<SimpleGrantedAuthority> authorities = (Collection<SimpleGrantedAuthority>) authentication
				.getAuthorities();
		List<User> subjects = null;
		if (authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
			subjects = userRepository.findBySubject(true);
		} else {
			subjects = new ArrayList<>();
			User curUser = userRepository.findByEmail(authentication.getName());
			List<UserLink> links = userLinkRepository.findByUser(curUser);
			for (UserLink userLink : links)
				subjects.add(userLink.getSubject());
		}

		model.addAttribute("init", true);
		model.addAttribute("subjects", subjects);
		model.addAttribute("initForm", new InitObservationForm());
		return "observation";
	}

	@PostMapping(path = "/observation")
	public String observation(Model model, @ModelAttribute InitObservationForm initForm) {
		User subject = userRepository.findOne(initForm.getSubjectId());
		ObservationForm form = new ObservationForm(subject.getId(), subject.getName(), initForm.getDate());

		DateTime curDate = DateConverter.toDateTime(initForm.getDate());
		String prevDate = DateConverter.toFormatString(curDate.minusDays(1));
		model.addAttribute("initForm", new InitObservationForm(subject.getId(), prevDate));
		String nextDate = DateConverter.toFormatString(curDate.plusDays(1));
		model.addAttribute("initForm2", new InitObservationForm(subject.getId(), nextDate));
		Date date = DateConverter.toSQLDate(initForm.getDate());

		Hashtable<Integer, List<Description>> hashtable = new Hashtable<>();
		List<Time> times = timeRepository.findAllByOrderByChronoAsc();

		for (Time time : times) {
			Observation observation = observationRepository.findOne(new ObservationPK(subject, date, time));
			if (observation == null)
				continue;
			List<Description> descriptions = observation.getDescriptions();
			if (descriptions == null || descriptions.size() == 0)
				continue;
			hashtable.put(time.getChrono(), descriptions);
		}

		model.addAttribute("times", times);
		model.addAttribute("hashtable", hashtable);
		model.addAttribute("obsForm", true);
		model.addAttribute("form", form);
		return "observation";
	}

	@PostMapping(path = "/observation/submit")
	public String submitObservation(Model model, @ModelAttribute(name = "form") ObservationForm form) {
		User subject = userRepository.findOne(form.getSubjectId());
		Time time = timeRepository.findOne(form.getTimeId());
		Date date = DateConverter.toSQLDate(form.getDate());

		Description description = new Description(userRepository.findOne(4l), form.getActivity(), form.getBehaviour(),
				form.getGrade());
		descriptionRepository.save(description);
		Observation observation = observationRepository.findOne(new ObservationPK(subject, date, time));
		if (observation == null) {
			observation = new Observation();
			observation.setSubject(subject);
			observation.setDate(date);
			observation.setTime(time);
		}
		observation.addDescription(description);
		observationRepository.save(observation);
		return observation(model, new InitObservationForm(subject.getId(), form.getDate()));
	}
}
