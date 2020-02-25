package org.sid.dao;

import org.sid.entities.Client;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface ClientRepository extends JpaRepository<Client,Long>{

	Client findByNom(String nom);

}
