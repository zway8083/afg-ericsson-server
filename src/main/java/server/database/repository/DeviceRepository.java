package server.database.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import server.database.model.Device;
import server.database.model.User;

public interface DeviceRepository extends CrudRepository<Device, Long> {
	List<Device> findByUser(User user);

	Device findOneByUser(User user);

	Device findBySerial(int[] serial);

	List<Device> findAll();
}
