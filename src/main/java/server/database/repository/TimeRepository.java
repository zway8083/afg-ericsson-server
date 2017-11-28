package server.database.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import server.database.model.Time;

public interface TimeRepository extends CrudRepository<Time, Integer> {
	List<Time> findAll();
	List<Time> findAllByOrderByChronoAsc();
	Time findOneByName(String name);
}
