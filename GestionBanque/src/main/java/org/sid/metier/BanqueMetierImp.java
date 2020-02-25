package org.sid.metier;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.sid.dao.ClientRepository;
import org.sid.dao.CompteRepository;
import org.sid.dao.OperationRepository;
import org.sid.entities.Client;
import org.sid.entities.Compte;
import org.sid.entities.CompteCourant;
import org.sid.entities.CompteEpargne;
import org.sid.entities.Operation;
import org.sid.entities.Retrait;
import org.sid.entities.Versement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
@Service
@Transactional
public class BanqueMetierImp implements IBanqueMetier {
	@Autowired
	private ClientRepository clientRepository;
	@Autowired
	private CompteRepository compteRepository;
	@Autowired
	private OperationRepository operationRepository;
	@Override
	public Compte consulterunCompte(String codeCpte) {
		Compte cpt =compteRepository.findByCodeCompte(codeCpte);
		if (cpt==null ) throw new RuntimeException("Compte introuvable");
		return cpt;
	}

	@Override
	public void verser(String codeCpte, double montant) {
		Compte cpt=consulterunCompte(codeCpte);
		Versement v= new Versement(new Date(),montant,cpt);
		cpt.setSolde(cpt.getSolde()+montant);
		operationRepository.save(v);
		
		
	}

	@Override
	public void retirer(String codeCpte, double montant) {
		Compte cpt=consulterunCompte(codeCpte);
		double facilitesCaisse=0;
		if(cpt instanceof CompteCourant )
			facilitesCaisse=((CompteCourant) cpt).getDecouvert();
		if(cpt.getSolde()+facilitesCaisse<montant)
			throw new RuntimeException("Solde insuffisant !! ");
		Retrait v= new Retrait(new Date(),montant,cpt);
		operationRepository.save(v);
		cpt.setSolde(cpt.getSolde()-montant);
		
	}

	@Override
	public void virement(String codeCpte1, String codeCpte2, double montant) {
		if(codeCpte1.equals(codeCpte2)) throw new RuntimeException("Operation refusée : virement d'un compte vers lui même.");
		retirer(codeCpte1,montant);
		verser(codeCpte2,montant);
		
	}

	@Override
	public Page<Operation> listeOperation(String codeCpte, int page, int size) {
		Compte compte=this.consulterunCompte(codeCpte);
		return operationRepository.findByCompte(compte,PageRequest.of(page, size));
	}

	@Override
	public List<Client> listeClients() {
		
		return clientRepository.findAll();
	}

	@Override
	public List<Compte> listeCompte() {
		
		return compteRepository.findAll();
	}
	@Override
	public Client findClient(String nom) {
		Client client=clientRepository.findByNom(nom);
		if (client==null) throw new RuntimeException("Client Introuvable!! ");
		return client ;
	}

	@Override
	public void creeCompteCourant(String codeCompte, double solde, double decouvert, Client client) {
		if(!this.existeCompte(codeCompte)) compteRepository.save(new CompteCourant(codeCompte, new Date(), solde, client, decouvert));
		else throw new RuntimeException("Code compte déja existe,essayez avec un autre compte ! ");
		
	}

	@Override
	public void creeCompteEpargne(String codeCompte, double solde, double taux, Client client) {
		
		if(!this.existeCompte(codeCompte)) compteRepository.save(new CompteEpargne(codeCompte, new Date(), solde, client, taux));
		else throw new RuntimeException("Code compte déja existe,essayez avec un autre compte ! ");
	}

	@Override
	public boolean existeCompte(String codeCompte) {
		Compte compte=compteRepository.findByCodeCompte(codeCompte);
		if (compte!=null) return true;
		return false;
	}

	@Override
	public void creeClient(String nom,String email) {
		Client client=clientRepository.findByNom(nom);
		if(client==null) clientRepository.save(new Client(nom,email));
		else if (client!=null) throw new RuntimeException("nom client déja existe,essayez avec un autre nom ! ");
		
	}

	@Override
	public void delete(String codeCompte) {
		compteRepository.deleteById(codeCompte);
		
	}

	

}
