package server.database.repository;

import org.springframework.data.repository.CrudRepository;

import server.database.model.Suggestion;

public interface SuggestionRepository extends CrudRepository<Suggestion, Long> {
}
