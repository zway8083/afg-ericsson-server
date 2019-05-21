package server.database.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import server.database.model.User;

public interface UserRepository extends CrudRepository<User, Long> {
	List<User> findAll();
	User findByEmail(String email);
	List<User> findBySubject(boolean isSubject);
	//User findSingleByEmailON(boolean isEmailON);

	List<User> findByEmailON(boolean isEmailON);
}
