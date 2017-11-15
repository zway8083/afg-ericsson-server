package server.database.repository;

import java.util.Date;

import org.springframework.data.repository.CrudRepository;

import server.database.model.Device;
import server.database.model.EventStat;

public interface EventStatRepository extends CrudRepository<EventStat, Long> {
	EventStat findByDeviceAndDate(Device device, Date date);
	EventStat findFirstByOrderByMvtsDesc();
	EventStat findFirstByMvtsGreaterThanEqualOrderByMvtsAsc(Integer min);
}
