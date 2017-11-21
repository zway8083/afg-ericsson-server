package server.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Alive {
	@GetMapping(path="/alive")
	public ResponseEntity<String> alive() {
		return new ResponseEntity<String>(HttpStatus.OK);
	}
}
