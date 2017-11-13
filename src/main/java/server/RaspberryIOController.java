package server;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import server.database.model.InputHistory;
import server.database.model.Raspberry;
import server.database.repository.InputHistoryRepository;
import server.database.repository.RaspberryRepository;
import server.utils.RandomStringGenerator;

@Controller
@RequestMapping(path="/raspberry")
public class RaspberryIOController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private RaspberryRepository raspberryRepository;
	@Autowired
	private InputHistoryRepository inputHistoryRepository;

	@GetMapping(path = "/input")
	public ResponseEntity<String> rasp(@RequestHeader(required = true, value = "Id") String id) {
		Raspberry raspberry = raspberryRepository.findOne(id);
		if (raspberry == null)
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		
		HttpHeaders headers = new HttpHeaders();
		boolean connected = raspberry.isConnected();
		headers.set("Connected", connected ? "True" : "False");
		String input = raspberry.getInput();
		if (connected && (input == null || input.isEmpty()) || !connected) {
			return new ResponseEntity<String>("", headers, HttpStatus.OK);
		}
		
		InputHistory history = new InputHistory(raspberry, input, new Date(), randomToken());
		inputHistoryRepository.save(history);
		
		raspberry.setInput(null);
		raspberryRepository.save(raspberry);

		logger.info("Sending input to Raspberry id=" + raspberry.getId() + ", history id=" + history.getId());
		headers.set("Token", history.getToken());
		return new ResponseEntity<String>(input, headers, HttpStatus.OK);
	}

	@PostMapping(path = "/output")
	public ResponseEntity<String> raspResponse(@RequestHeader(required = true, value = "Id") String id,
			@RequestHeader(required = true, value = "Token") String token,
			@RequestBody(required = false) String output) {
		Raspberry raspberry = raspberryRepository.findOne(id);
		InputHistory history = inputHistoryRepository.findOneByToken(token);
		if (raspberry == null || history == null || history.getOutputReceived() != null)
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		logger.info("Received output from Raspberry id=" + raspberry.getId() + ", history id=" + history.getId());
		history.setOutput(output);
		history.setOutputReceived(new Date());
		inputHistoryRepository.save(history);
		raspberryRepository.save(raspberry);
		return new ResponseEntity<String>(HttpStatus.OK);
	}

	private String randomToken() {
		String token = RandomStringGenerator.randomString(10);
		InputHistory history = inputHistoryRepository.findOneByToken(token);
		return history != null ? randomToken() : token;
	}
}
