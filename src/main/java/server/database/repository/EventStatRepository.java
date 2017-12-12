package server.database.repository;

import java.util.Date;

import org.springframework.data.repository.CrudRepository;

import server.database.model.Device;
import server.database.model.EventStat;

public interface EventStatRepository extends CrudRepository<EventStat, Long> {
	EventStat findByDeviceAndDate(Device device, Date date);
	EventStat findFirstByDateBeforeOrderByMvtsDesc(Date date);
	EventStat findFirstByDateBeforeAndMvtsGreaterThanEqualOrderByMvtsAsc(Date date, Integer min);
	Long countByDateAndGradeBetween(Date date, Integer grade1, Integer grade2);
	Long countByDate(Date date);
}
