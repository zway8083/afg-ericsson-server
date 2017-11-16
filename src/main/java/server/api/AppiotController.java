package server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import server.database.model.AppiotRef;
import server.database.model.Device;
import server.database.repository.AppiotRefRepository;
import server.database.repository.DeviceRepository;
import server.model.Sensor;
import server.model.SensorCollection;
import server.model.Serial;

@Controller
@RequestMapping(path = "/api/appiot")
public class AppiotController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Value("${appiot.headers.authorization}")
	private String authorization;
	@Value("${appiot.headers.x-devicenetwork}")
	private String xDeviceNetwork;
	@Autowired
	private AppiotRefRepository appiotRefRepository;
	@Autowired
	private DeviceRepository deviceRepository;

	@PostMapping(path = "/keys")
	public ResponseEntity<String> appiotKeys(@RequestParam(value = "token", required = false) String token,
			@RequestBody Serial serial) {

		Device device = deviceRepository.findBySerial(serial.getSerial());
		if (device == null) {
			logger.error("No device found in database for serial: " + serial);
			return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
		}

		AppiotRef appiotRef = appiotRefRepository.findByDevice(device);
		if (appiotRef == null) {
			logger.error("No Appiot reference found for device id = " + device.getId());
			return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
		}
		if (appiotRef.getGateway() != null && appiotRef.getBattery() != null && appiotRef.getLuminescence() != null
				&& appiotRef.getMotion() != null && appiotRef.getTemperature() != null
				&& appiotRef.getTamper() != null) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				String json = mapper.writeValueAsString(appiotRef);
				logger.info("Sending apppiot keys to device id = " + device.getId());
				return new ResponseEntity<String>(json, HttpStatus.OK);
			} catch (JsonProcessingException e) {
				logger.error("Failed to convert Appiot keys to json");
				return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}

		logger.info("Getting Appiot keys for device id = " + device.getId());
		if (appiotRef.getGateway() == null) {
			logger.error("No gateway for Appiot reference id = " + appiotRef.getId());
			return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
		}

		final String url1 = "https://eappiot-api.sensbysigma.com/api/v2/dataCollectors/" + appiotRef.getGateway()
				+ "/sensorCollections";

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", authorization);
		headers.add("X-DeviceNetwork", xDeviceNetwork);
		HttpEntity<String> httpEntity = new HttpEntity<String>(headers);

		ResponseEntity<SensorCollection[]> response = restTemplate.exchange(url1, HttpMethod.GET, httpEntity,
				SensorCollection[].class);
		SensorCollection[] sensorCollections = response.getBody();

		if (sensorCollections == null || sensorCollections.length == 0) {
			logger.info("No SensorCollection found for gateway: " + appiotRef.getGateway());
			return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
		}
		for (SensorCollection sensorCollection : sensorCollections) {
			try {
				Serial appiotSerial = new Serial(sensorCollection.getMacAddress());
				if (appiotSerial.equals(serial)) {
					final String url2 = "https://eappiot-api.sensbysigma.com/clientapi/v1/sensorcollections/"
							+ sensorCollection.getId() + "/sensors";
					ResponseEntity<Sensor[]> response2 = restTemplate.exchange(url2, HttpMethod.GET, httpEntity,
							Sensor[].class);
					appiotRef.setKey(response2.getBody());
					appiotRefRepository.save(appiotRef);
					ObjectMapper mapper = new ObjectMapper();
					try {
						String json = mapper.writeValueAsString(appiotRef);
						logger.info("Returned json: " + json);
						logger.info("Sending apppiot keys to device id = " + device.getId());
						return new ResponseEntity<String>(json, HttpStatus.OK);
					} catch (JsonProcessingException e) {
						logger.error("Failed to convert Appiot keys to json");
					}
					return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return null;
	}

}
