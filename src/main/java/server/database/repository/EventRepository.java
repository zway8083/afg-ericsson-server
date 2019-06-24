package server.database.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import server.database.model.Device;
import server.database.model.Event;
import server.database.model.SensorType;
import server.database.model.User;

public interface EventRepository extends CrudRepository<Event, Integer> {
	List<Event> findByDevice(Device device);

	List<Event> findByDeviceAndType(Device device, SensorType type);

	List<Event> findByDeviceAndTypeAndDateBetweenOrderByDateAsc(Device device, SensorType type, Date d1, Date d2);
	List<Event> findByDeviceAndTypeAndDateBetweenOrderByDateDesc(Device device, SensorType type, Date d1, Date d2);

	List<Event> findByDeviceAndTypeAndBinValueAndDateBetween(Device device, SensorType type, Boolean binValue, Date d1,
			Date d2);

	List<Event> findByDateBetween(Date d1, Date d2);

	Long countByDeviceAndTypeAndBinValueAndDateBetween(Device device, SensorType type, Boolean binValue, Date d1,
			Date d2);

	List<Event> findByUserAndTypeAndDateBetweenOrderByDateAsc(User user, SensorType type, Date d1, Date d2);
	List<Event> findByUserAndTypeAndDateBetweenOrderByDateDesc(User user, SensorType type, Date d1, Date d2);

	List<Event> findByUserAndTypeAndBinValueAndDateBetween(User user, SensorType type, Boolean binValue, Date d1,
															 Date d2);

	Long countByUserAndTypeAndBinValueAndDateBetween(User user, SensorType type, Boolean binValue, Date d1,
													 Date d2);

	@Query(value = "SELECT avg(event.d_value) FROM event WHERE event.device_id = ?1 AND event.type_id = ?2 AND "
			+ "event.date BETWEEN ?3 AND ?4 ORDER BY event.date ASC", nativeQuery = true)
	Double getAverageTypeBetween(Long device_id, Long type_id, Date d1, Date d2);
}
