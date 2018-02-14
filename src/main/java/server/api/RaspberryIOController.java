package server.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import server.database.model.InputHistory;
import server.database.model.Raspberry;
import server.database.repository.InputHistoryRepository;
import server.database.repository.RaspberryRepository;

import java.util.Date;

@Controller
@RequestMapping(path="/api/raspberry")
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

		InputHistory history = inputHistoryRepository.findFirstByRaspberryAndOutputOrderByInputSentAsc(raspberry, null);
		if (history == null)
			return new ResponseEntity<String>("", headers, HttpStatus.OK);

		headers.set("Token", history.getToken());
		logger.info("Sending input to Raspberry id=" + raspberry.getId() + ", history id=" + history.getId());
		return new ResponseEntity<String>(history.getInput(), headers, HttpStatus.OK);
	}

	@PostMapping(path = "/output")
	public ResponseEntity<String> raspResponse(@RequestHeader(required = true, value = "Id") String id,
			@RequestHeader(required = true, value = "Token") String token,
			@RequestBody(required = false) String output) {
		Raspberry raspberry = raspberryRepository.findOne(id);
		InputHistory history = inputHistoryRepository.findOneByToken(token);
		if (raspberry == null || history == null || (history.getOutputReceived() != null && history.getOutput() != null))
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		logger.info("Received output from Raspberry id=" + raspberry.getId() + ", history id=" + history.getId());
		history.setOutput(output == null ? "" : output);
		history.setOutputReceived(new Date());
		inputHistoryRepository.save(history);
		return new ResponseEntity<String>(HttpStatus.OK);
	}

}
