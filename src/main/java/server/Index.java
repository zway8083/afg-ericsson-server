package server;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Index {
	@RequestMapping("/")
	public String index() {
		return "Server is running...";
	}
}
