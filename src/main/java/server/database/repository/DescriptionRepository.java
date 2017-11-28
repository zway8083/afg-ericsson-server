package server.database.repository;

import org.springframework.data.repository.CrudRepository;

import server.database.model.Description;

public interface DescriptionRepository extends CrudRepository<Description, Long> {

}
