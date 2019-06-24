package server.controller;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import server.database.model.Device;
import server.database.model.EventStat;
import server.database.model.User;
import server.database.repository.DeviceRepository;
import server.database.repository.EventRepository;
import server.database.repository.EventStatRepository;
import server.database.repository.SensorTypeRepository;
import server.database.repository.UserRepository;
import server.exception.MissingSleepTimesException;
import server.exception.NoMotionException;
import server.model.UserIdForm;
import server.task.EventTask;

@Controller
public class Generate {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private DeviceRepository deviceRepository;
	@Autowired
	private SensorTypeRepository sensorTypeRepository;
	@Autowired
	private EventRepository eventRepository;
	@Autowired
	private EventStatRepository eventStatRepository;

	@GetMapping(path = "/generate/eventstat")
	public String generateEventStatForm(Model model) {
		List<User> subjects = userRepository.findBySubject(true);
		model.addAttribute("subjects", subjects);
		model.addAttribute("form", new UserIdForm());
		return "generate-eventstat";
	}

	@PostMapping(path = "/generate/eventstat")
	public String generateEventStat(@ModelAttribute(name = "form") UserIdForm form) {
		User user = userRepository.findOne(form.getId());
		Device device = deviceRepository.findOneByUser(user);

		List<EventStat> stats = eventStatRepository.findAllByUserOrderByDateAsc(user);
		//List<EventStat> stats = eventStatRepository.findAllByDeviceOrderByDateAsc(device);
		DateTime date;
		DateTime last;
		if (stats == null || stats.isEmpty() || stats.size() < 2) {
			date = new DateTime(2017, 10, 1, 0, 0);
			last = new DateTime();
		} else {
			date = new DateTime(stats.get(0).getDate());
			last = new DateTime(stats.get(stats.size() - 1).getDate());
		}
		for (EventStat eventStat : stats)
			eventStatRepository.delete(eventStat);
		
		while (date.getMillis() <= last.getMillis()) {
			try {
				@SuppressWarnings("unused")
				EventTask eventTask = new EventTask(device, date, sensorTypeRepository, eventRepository, eventStatRepository, null, "");
			} catch (MissingSleepTimesException e) {
				//System.out.println(e.getMessage());
			} catch (NoMotionException e) {
				//System.out.println(e.getMessage());
			}
			date = date.plusDays(1);
		}
		
		return "redirect:/generate/eventstat?success";
	}
}
