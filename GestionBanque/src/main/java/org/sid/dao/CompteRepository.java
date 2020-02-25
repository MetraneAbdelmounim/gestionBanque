package org.sid.dao;

import org.sid.entities.Compte;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface CompteRepository extends JpaRepository<Compte,String>{


	Compte findByCodeCompte(String codeCpte);
	
	

}
