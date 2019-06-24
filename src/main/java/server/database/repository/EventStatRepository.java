package server.database.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import server.database.model.Device;
import server.database.model.EventStat;
import server.database.model.User;

public interface EventStatRepository extends CrudRepository<EventStat, Long> {
	EventStat findFirstByDateBetweenOrderByMvtsDesc(Date date1, Date date2);
	EventStat findFirstByDateBetweenAndMvtsGreaterThanEqualOrderByMvtsAsc(Date date1, Date date2, Integer min);
	Long countByDateAndGradeBetween(Date date, Integer grade1, Integer grade2);
	Long countByDate(Date date);
	List<EventStat> findAllByDeviceOrderByDateAsc(Device device);
	EventStat findByDeviceAndDate(Device device, Date date);

	List<EventStat> findAllByUserOrderByDateAsc(User user);
	EventStat findByUserAndDate(User user, Date date);
}
