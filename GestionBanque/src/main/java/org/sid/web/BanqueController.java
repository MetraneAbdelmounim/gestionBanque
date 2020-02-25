package org.sid.web;

import java.util.List;

import org.sid.entities.Client;
import org.sid.entities.Compte;
import org.sid.entities.Operation;
import org.sid.metier.IBanqueMetier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
@Controller
public class BanqueController {
	@Autowired
	private IBanqueMetier banqueMetier;
	@RequestMapping("/operations")
	public String index() {
		return "comptes";
	}
	@RequestMapping("/consulterCompte")
	public String consulter(Model model,String codeCompte) {
		model.addAttribute("codeCompte", codeCompte);
		try {
			Compte cp=banqueMetier.consulterunCompte(codeCompte);
			Page<Operation> pageOperations=banqueMetier.listeOperation(codeCompte, 0, 100);
			model.addAttribute("listOperations",pageOperations.getContent());
			model.addAttribute("compte",cp);
			model.addAttribute("total", pageOperations.toList().size());
			
		} catch (Exception e) {
			model.addAttribute("exception", e);
		
		}
		return "comptes";
	}
	@RequestMapping(value = "/saveOperation",method = RequestMethod.POST)
	public String saveOperation(Model model,String typeOperation,String codeCompte,String codeCompte2,double montant) {
		try {
			if(typeOperation.equals("VERS")) {
				banqueMetier.verser(codeCompte, montant);
			}
			else if(typeOperation.equals("RETR")) {
				banqueMetier.retirer(codeCompte, montant);
			}
			else {
				banqueMetier.virement(codeCompte, codeCompte2, montant);
			}
		}
		catch (Exception e) {
			model.addAttribute("error", e);
			return "redirect:/consulterCompte?codeCompte="+codeCompte+"&error="+e.getMessage();
		}
		return "redirect:/consulterCompte?codeCompte="+codeCompte;
	}
	@RequestMapping("/clients")
	public String afficherClient(Model model) {
		List<Client> listeClients=banqueMetier.listeClients();
		model.addAttribute("listeClients", listeClients);
		
		return "clients";
	}
	@RequestMapping("/compt")
	public String afficherComptes(Model model) {
		List<Compte> listeComptes=banqueMetier.listeCompte();
		model.addAttribute("listeComptes", listeComptes);
		
		return "compt";
	}
	@RequestMapping("/addCompte")
	public String compte() {
		return "ajouterCompte";
	}
	@RequestMapping(value = "/saveCompte",method = RequestMethod.POST)
	public String saveCompte(Model model, String codeCompte,double solde,String typeCompte,String taux,String decouvert,String nomClient ) {
		if(codeCompte.equals("")) {
			String e="veuillez remplisser tous les champs!";
			model.addAttribute("erreur",e );
			return "redirect:/addCompte?codeCompte="+codeCompte+"&erreur="+e;
		}
		try {
			Client client=banqueMetier.findClient(nomClient);
			if(typeCompte.equals("COUR")) banqueMetier.creeCompteCourant(codeCompte, solde,Double.parseDouble(decouvert), client);
			else banqueMetier.creeCompteEpargne(codeCompte, solde, Double.parseDouble(taux), client);
		
			
			
		} catch (Exception e) {
			model.addAttribute("erreur",e );
			return "redirect:/addCompte?codeCompte="+codeCompte+"&erreur="+e.getMessage();
		}
		
		
		return "redirect:/compt";
	}
	@RequestMapping("/addClient")
	public String client() {
		return "client";
	}
	@RequestMapping(value = "/saveClient",method = RequestMethod.POST)
	public String saveCompte(Model model,String nomClient,String emailClient) {
		if(nomClient.equals("")) {
			String e="veuillez remplisser tous les champs!";
			model.addAttribute("erreur",e );
			return "redirect:/addClient?nom="+nomClient+"&erreur="+e;
		}
		try {
			banqueMetier.creeClient(nomClient, emailClient);
		} catch (Exception e) {
			model.addAttribute("erreur",e );
			return "redirect:/addClient?nom="+nomClient+"&erreur="+e.getMessage();
		}
		
		
		return "redirect:/clients";
	}
	@RequestMapping(value = "/deleteCompte/{codeCompte}", method = RequestMethod.GET)
	public String delete(@PathVariable("codeCompte") String codeCompte,Model model) {
		banqueMetier.delete(codeCompte);
		
		
		return "redirect:/compt";
	}
}
