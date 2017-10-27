package server.database.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import server.database.model.User;

public interface UserRepository extends CrudRepository<User, Long> {
	List<User> findAll();
	List<User> findByRoles_Name(String name);
}
