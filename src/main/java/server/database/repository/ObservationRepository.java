package server.database.repository;

import org.springframework.data.repository.CrudRepository;

import server.database.model.Observation;
import server.database.model.ObservationPK;

public interface ObservationRepository extends CrudRepository<Observation, ObservationPK> {
}
