package server.database.repository;

import org.springframework.data.repository.CrudRepository;

import server.database.model.Raspberry;

public interface RaspberryRepository extends CrudRepository<Raspberry, String> {

}
