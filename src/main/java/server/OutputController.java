package server;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import server.database.model.InputHistory;
import server.database.model.Raspberry;
import server.database.model.User;
import server.database.repository.InputHistoryRepository;
import server.database.repository.RaspberryRepository;
import server.database.repository.UserRepository;
import server.model.Message;

@Controller
public class OutputController {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RaspberryRepository raspberryRepository;
	@Autowired
	private InputHistoryRepository inputHistoryRepository;

	@GetMapping("raspberry")
	public String snd(Model model) {
		List<User> subjects = userRepository.findBySubject(true);
		model.addAttribute("subjects", subjects);
		return "send";
	}

	@MessageMapping("/input")
	@SendTo("/topic/output")
	public Message output(Message message) throws Exception {

		User user = userRepository.findOne(message.getId());
		Raspberry raspberry = raspberryRepository.findOneByUsers_id(user.getId());
		String input = message.getMessage();
		raspberry.setInput(input);
		Date now = new Date();
		raspberryRepository.save(raspberry);

		InputHistory history = inputHistoryRepository
				.findFirstByRaspberryAndInputAndInputSentGreaterThanEqualOrderByInputSentDesc(raspberry, input, now);

		while (history == null || history.getOutput() == null) {
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (history != null) {
				history = inputHistoryRepository.findOne(history.getId());
			} else {
				history = inputHistoryRepository
						.findFirstByRaspberryAndInputAndInputSentGreaterThanEqualOrderByInputSentDesc(raspberry, input,
								now);
			}
		}

		return new Message(history.getOutput());
	}
}
