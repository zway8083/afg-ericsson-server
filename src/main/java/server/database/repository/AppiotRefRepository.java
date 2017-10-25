package server.database.repository;

import org.springframework.data.repository.CrudRepository;

import server.database.model.AppiotRef;
import server.database.model.Device;

public interface AppiotRefRepository extends CrudRepository<AppiotRef, Long> {
	AppiotRef findByDevice(Device device);
}
