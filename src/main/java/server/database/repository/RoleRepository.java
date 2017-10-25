package server.database.repository;


import java.util.List;

import org.springframework.data.repository.CrudRepository;

import server.database.model.Role;

public interface RoleRepository extends CrudRepository<Role, Long> {
	List<Role> findAll();
	Role findByName(String name);
}
