package server.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path="/api")
public class AliveController {
	@GetMapping(path = "/alive")
	public ResponseEntity<?> alive() {
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
