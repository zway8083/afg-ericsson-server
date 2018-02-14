package server.database.repository;

import java.util.Date;

import org.springframework.data.repository.CrudRepository;

import server.database.model.InputHistory;
import server.database.model.Raspberry;

public interface InputHistoryRepository extends CrudRepository<InputHistory, Long> {
	InputHistory findOneByToken(String token);
	InputHistory findFirstByRaspberryAndOutputOrderByInputSentAsc(Raspberry raspberry, String Output);
}
