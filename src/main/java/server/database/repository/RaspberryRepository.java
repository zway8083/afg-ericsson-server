package server.database.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import server.database.model.Raspberry;

public interface RaspberryRepository extends CrudRepository<Raspberry, String> {
	List<Raspberry> findAll();
	Raspberry findOneByUsers_id(Long id);
}
