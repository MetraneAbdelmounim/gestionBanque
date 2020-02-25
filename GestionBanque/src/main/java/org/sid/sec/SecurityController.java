package org.sid.sec;

import org.hibernate.validator.constraints.br.CNPJ;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SecurityController {
	@RequestMapping(value = "/")
	public String home() {
		return "redirect:/operations";
	}
	/*@RequestMapping(value = "/login")
	public String login() {
		return "login";
	}*/
	@RequestMapping(value = "/403")
	public String accesDenied() {
		return "403";
	}
	
}
