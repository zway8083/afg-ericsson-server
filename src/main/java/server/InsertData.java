package server;

import java.util.Arrays;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import server.database.model.AppiotRef;
import server.database.model.Device;
import server.database.model.Role;
import server.database.model.SensorType;
import server.database.model.User;
import server.database.repository.AppiotRefRepository;
import server.database.repository.DeviceRepository;
import server.database.repository.RoleRepository;
import server.database.repository.SensorTypeRepository;
import server.database.repository.UserRepository;

@RestController
public class InsertData {
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private AppiotRefRepository appiotRefRepository;
	@Autowired
	private DeviceRepository deviceRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private SensorTypeRepository sensorTypeRepository;

	@GetMapping(path = "/data")
	public @ResponseBody String data() {
		try {
			SensorType motion = new SensorType("motion");
			SensorType temperature = new SensorType("temperature");
			SensorType luminescence = new SensorType("luminescence");
			SensorType battery = new SensorType("battery");
			SensorType tamper = new SensorType("tamper");
			sensorTypeRepository.save(Arrays.asList(motion, temperature, luminescence, battery, tamper));
			Role instructor = new Role("Instructeur");
			Role parent = new Role("Parent");
			Role child = new Role("Enfant");
			roleRepository.save(instructor);
			roleRepository.save(parent);
			roleRepository.save(child);

			User user = new User("Ada", "Test", new Date(787273200000l));
			user.addRole(instructor);
			User user2 = new User("Adi", "Toto", new Date(787473200000l));
			user2.addRole(child);
			User user3 = new User("Didi", "Lala", new Date(787293200000l));
			user3.addRole(parent);
			userRepository.save(user);
			userRepository.save(user2);
			userRepository.save(user3);

			Device device = new Device(new int[] { 0, 0, 0, 0, 0, 5, 210, 197 }, user);
			deviceRepository.save(device);

			AppiotRef appiotRef = new AppiotRef(device, "ed7dc500-16ca-4158-b5e1-90b53ef7ba79");
			appiotRefRepository.save(appiotRef);

			return "OK";
		} catch (Exception e) {
			return "KO";
		}
	}

}
