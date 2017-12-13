package server.database.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import server.database.model.Observation;
import server.database.model.ObservationPK;

public interface ObservationRepository extends CrudRepository<Observation, ObservationPK> {
	@Query(value = "SELECT observation.* FROM afg_ericsson.observation, observation_descriptions "
			+ "WHERE afg_ericsson.observation_descriptions.descriptions_id = ?1 "
			+ "AND afg_ericsson.observation_descriptions.observation_date = observation.date "
			+ "AND afg_ericsson.observation_descriptions.observation_subject_id = observation.subject_id "
			+ "AND afg_ericsson.observation_descriptions.observation_time_chrono = observation.time_chrono", nativeQuery = true)
	Observation findByDescription(Long descriptionId);
}
