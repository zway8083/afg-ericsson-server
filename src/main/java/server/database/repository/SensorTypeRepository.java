package server.database.repository;

import org.springframework.data.repository.CrudRepository;

import server.database.model.SensorType;

public interface SensorTypeRepository extends CrudRepository<SensorType, Long> {
	SensorType findByName(String name);
}
