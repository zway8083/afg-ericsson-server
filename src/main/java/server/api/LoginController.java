package server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import server.database.model.User;
import server.database.repository.UserRepository;
import server.model.LoginForm;

@Controller
public class LoginController {
	@Autowired
	private UserRepository userRepository;

	@PostMapping(path = "/api/login")
	public ResponseEntity<?> login(@RequestBody LoginForm loginForm) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		User user = userRepository.findByEmail(loginForm.getUsername());
		if (user == null || !encoder.matches(loginForm.getPassword(), user.getPassword()))
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
