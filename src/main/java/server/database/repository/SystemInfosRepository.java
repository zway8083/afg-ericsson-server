package server.database.repository;

import org.springframework.data.repository.CrudRepository;

import server.database.model.SystemInfos;

public interface SystemInfosRepository extends CrudRepository<SystemInfos, Long> {

}
