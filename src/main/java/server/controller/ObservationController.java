package server.controller;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import server.database.model.Description;
import server.database.model.Observation;
import server.database.model.ObservationPK;
import server.database.model.Time;
import server.database.model.User;
import server.database.repository.DescriptionRepository;
import server.database.repository.ObservationRepository;
import server.database.repository.TimeRepository;
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

	@GetMapping(path = "/gendata")
	public @ResponseBody String data() {
		List<String> timeStrs = Arrays.asList("Réveil", "Petit déjeuner", "Matinée", "Repas du midi", "Après midi",
				"Goûter", "Soirée", "Repas du soir", "Sommeil / nuit");
		ArrayList<Time> times = new ArrayList<>();
		int i = -10;
		for (String timeStr : timeStrs) {
			times.add(new Time(i += 10, timeStr));
		}
		try {
			timeRepository.save(times);
		} catch (Exception e) {
			System.err.println("Time data already exist");
		}

		ArrayList<Description> descriptions = new ArrayList<Description>();
		ArrayList<Description> descriptions2 = new ArrayList<Description>();

		User observator = userRepository.findOne(18l);

		Description description = new Description(observator, "Activité test", "Comportement test, testtest test.");
		descriptions.add(description);
		descriptionRepository.save(description);

		Description description2 = new Description(observator, "Activité test 2222",
				"Comportement test 2, testtest test. 222");
		descriptions.add(description2);
		descriptionRepository.save(description2);

		Description description3 = new Description(userRepository.findOne(14l), "Activité 3", "Comportement 3");
		descriptions.add(description3);
		descriptionRepository.save(description3);

		Description description4 = new Description(observator, "Activité test", "Comportement test, testtest test.");
		descriptions2.add(description4);
		descriptionRepository.save(description4);

		Observation observation = new Observation();
		observation.setDate(DateConverter.toSQLDate("24/11/2017"));
		observation.setSubject(userRepository.findOne(4l));
		observation.setTime(timeRepository.findOne(0));
		observation.setDescriptions(descriptions);

		Observation observation2 = new Observation();
		observation2.setDate(DateConverter.toSQLDate("24/11/2017"));
		observation2.setSubject(userRepository.findOne(4l));
		observation2.setTime(timeRepository.findOne(10));
		observation2.setDescriptions(descriptions2);

		observationRepository.save(observation);
		observationRepository.save(observation2);

		return "";
	}

	@GetMapping(path = "/observation")
	public String initObservation(Model model) {
		List<User> subjects = userRepository.findBySubject(true);
		model.addAttribute("initForm", true);
		model.addAttribute("subjects", subjects);
		model.addAttribute("form", new InitObservationForm());
		return "observation";
	}

	@PostMapping(path = "/observation")
	public String observation(Model model, @ModelAttribute(name = "form") InitObservationForm initForm) {
		User subject = userRepository.findOne(initForm.getSubjectId());
		ObservationForm form = new ObservationForm(subject.getId(), subject.getName(), initForm.getDate());

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

		Description description = new Description(userRepository.findOne(4l), form.getActivity(), form.getBehaviour());
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
