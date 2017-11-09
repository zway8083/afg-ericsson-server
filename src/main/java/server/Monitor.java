package server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import server.database.model.Raspberry;
import server.database.model.SystemInfos;
import server.database.repository.RaspberryRepository;
import server.database.repository.SystemInfosRepository;

@Controller
public class Monitor {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private RaspberryRepository raspberryRepository;
	@Autowired
	private SystemInfosRepository systemInfosRepository;

	@PostMapping(value = "/monitor/{id}")
	public ResponseEntity<String> save(@PathVariable("id") String id,
			@RequestBody(required = true) SystemInfos systemInfos) {
		try {
			System.out.println(systemInfos.toString());
			Raspberry raspberry = raspberryRepository.findOne(id);
			if (raspberry == null)
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			systemInfos.setRaspberry(raspberry);
			systemInfosRepository.save(systemInfos);
			logger.info("Saved SystemInfos=" + systemInfos.getId() + "from raspberry=" + id);
			return new ResponseEntity<>(HttpStatus.CREATED);
		} catch (Exception e) {
			logger.error("Error saving " + systemInfos.toString() + " from raspberry=" + id + ": " + e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}