package server.controller;

import java.security.Principal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import server.database.model.Description;
import server.database.model.EventStat;
import server.database.model.Observation;
import server.database.model.ObservationPK;
import server.database.model.Time;
import server.database.model.User;
import server.database.model.UserLink;
import server.database.repository.DescriptionRepository;
import server.database.repository.DeviceRepository;
import server.database.repository.EventStatRepository;
import server.database.repository.ObservationRepository;
import server.database.repository.TimeRepository;
import server.database.repository.UserLinkRepository;
import server.database.repository.UserRepository;
import server.model.DeleteObservationForm;
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
	@Autowired
	private EventStatRepository eventStatRepository;
	@Autowired
	private DeviceRepository deviceRepository;

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
			{
				if(!subjects.contains(userLink.getSubject()))
				subjects.add(userLink.getSubject());
			}	
		}

		model.addAttribute("init", true);
		model.addAttribute("subjects", subjects);
		model.addAttribute("initForm", new InitObservationForm());
		return "observation";
	}

	@PostMapping(path = "/observation")
	public String observation(Principal principal, Model model, @ModelAttribute InitObservationForm initForm) {
		User user = userRepository.findByEmail(principal.getName());

		User subject = userRepository.findOne(initForm.getSubjectId());
		ObservationForm form = new ObservationForm(subject.getId(), subject.getName(), initForm.getDate());

		DateTime curDate = DateConverter.toDateTime(initForm.getDate());
		String prevDate = DateConverter.toFormatString(curDate.minusDays(1));
		model.addAttribute("initForm", new InitObservationForm(subject.getId(), prevDate));
		String nextDate = DateConverter.toFormatString(curDate.plusDays(1));
		model.addAttribute("initForm2", new InitObservationForm(subject.getId(), nextDate));
		Date date = DateConverter.toSQLDate(initForm.getDate());

		Hashtable<Integer, List<Description>> hashtable = new Hashtable<>();
		List<Time> allTimes = timeRepository.findAllByOrderByChronoAsc();
		List<Time> times = new ArrayList<>();
		for (Time time : allTimes) {
			List<Description> descriptions = new ArrayList<>();
			if (time.getChrono() == -10) {
				EventStat eventStat = eventStatRepository.findByDeviceAndDate(deviceRepository.findOneByUser(subject),
						curDate.toDate());
				if (eventStat == null || eventStat.getGrade() == null)
					continue;
				Integer grade;
				if (eventStat.getGrade() < 60)
					grade = 2;
				else if (eventStat.getGrade() < 90)
					grade = 1;
				else
					grade = 0;
				Description description = new Description(null, "", "Score : " + eventStat.getGrade() + "%", grade);
				descriptions.add(description);
			} else {
				Observation observation = observationRepository.findOne(new ObservationPK(subject, date, time));
				if (observation == null)
					continue;
				descriptions = observation.getDescriptions();
			}
			if (descriptions == null || descriptions.size() == 0)
				continue;
			hashtable.put(time.getChrono(), descriptions);
			times.add(time);
		}

		model.addAttribute("times", times);
		model.addAttribute("allTimes", allTimes);
		model.addAttribute("hashtable", hashtable);
		model.addAttribute("obsForm", true);
		model.addAttribute("form", form);
		model.addAttribute("userId", user.getId());
		
		return "observation";
	}

	
	
	@PostMapping(value = "/observation/submit")
	public String  submitObservation(Principal principal, Model model,
			@ModelAttribute(name = "form") ObservationForm form) {
		User observator = userRepository.findByEmail(principal.getName());
		User subject = userRepository.findOne(form.getSubjectId());
		Time time = timeRepository.findOne(form.getTimeId());
		Date date = DateConverter.toSQLDate(form.getDate());
		Description description = new Description(observator, form.getActivity(), form.getBehaviour(), form.getGrade());
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
		
		
		observation(principal, model, new InitObservationForm(subject.getId(), form.getDate()));
		
		return "observation"; 
			
	}

	@PostMapping(path = "/observation/{idDescription}/delete")
	public String deleteAccompanistLink(Authentication authentication,Principal principal,Model model,
			 @PathVariable("idDescription") long idDescription,@ModelAttribute(name = "form") ObservationForm form) {
      
		@SuppressWarnings("unchecked")
		Collection<SimpleGrantedAuthority> authorities = (Collection<SimpleGrantedAuthority>) authentication
				.getAuthorities();
		
		User curUser = userRepository.findByEmail(authentication.getName());
		Description description = descriptionRepository.findOne(idDescription);
		
		if (description.getObservator().getId() == curUser.getId() ||authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
			Observation observation = observationRepository.findByDescription(description.getId());
			List<Description> descriptions = observation.getDescriptions();
			if (descriptions.size() == 1)
				observationRepository.delete(observation);
			else {
				descriptions.remove(description);
				observation.setDescriptions(descriptions);
				observationRepository.save(observation);
			     }
			descriptionRepository.delete(description);
		}
	
		
		
		
		 return observation(principal,model, new InitObservationForm(form.getSubjectId(), form.getDate()));
	}
}
