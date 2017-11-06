package server.database.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import server.database.model.User;
import server.database.model.UserLink;

public interface UserLinkRepository extends CrudRepository<UserLink, Long>{
	List<UserLink> findBySubject(User subject);
}