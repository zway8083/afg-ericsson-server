package server.database.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import server.database.model.User;

public interface UserRepository extends CrudRepository<User, Long> {
	List<User> findAll();
	User findByEmail(String email);
	//List<User> findByRoles_Name(String name);
	List<User> findBySubject(boolean isSubject);
}
