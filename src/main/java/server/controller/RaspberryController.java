package server.controller;

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
import server.utils.RandomStringGenerator;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Controller
public class RaspberryController {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RaspberryRepository raspberryRepository;
	@Autowired
	private InputHistoryRepository inputHistoryRepository;

	@GetMapping("/raspberry")
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
		if (!message.isConnected())
			raspberry.setConnected(false);
		else
			raspberry.setConnected(true);
		if (message.getMessage() == null) {
			raspberryRepository.save(raspberry);
			return null;
		}

		String input = message.getMessage();
		String token = randomToken();
		InputHistory history = new InputHistory(raspberry, input, new Date(), token);
		inputHistoryRepository.save(history);

		while (history.getOutput() == null) {
			TimeUnit.SECONDS.sleep(5);
			history = inputHistoryRepository.findOneByToken(token);
		}
		return new Message(history.getOutput());
	}

	private String randomToken() {
		String token = RandomStringGenerator.randomString(10);
		InputHistory history = inputHistoryRepository.findOneByToken(token);
		return history != null ? randomToken() : token;
	}
}
