package server.database.repository;

import org.springframework.data.repository.CrudRepository;

import server.database.model.InputHistory;

public interface InputHistoryRepository extends CrudRepository<InputHistory, Long> {
	InputHistory findOneByToken(String token);
}
