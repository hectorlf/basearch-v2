package basearch.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SecuredController {

	private static final Logger logger = LoggerFactory.getLogger(SecuredController.class);

	@RequestMapping(value="/secured.page")
	public String welcome(ModelMap model) {
		logger.debug("Going into SecuredController.welcome()");
		return "/secured.html";
	}

}
