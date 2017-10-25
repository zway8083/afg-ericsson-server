package server.database.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import server.database.model.Device;
import server.database.model.Event;
import server.database.model.SensorType;

public interface EventRepository extends CrudRepository<Event, Integer> {
	List<Event> findByDevice(Device device); 
	List<Event> findByDeviceAndType(Device device, SensorType type);
	List<Event> findByDeviceAndTypeAndDateBetween(Device device, SensorType type, Date d1, Date d2);
	List<Event> findByDateBetween(Date d1, Date d2);
}
