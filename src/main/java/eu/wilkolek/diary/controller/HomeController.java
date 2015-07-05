package eu.wilkolek.diary.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);

    @RequestMapping("/")
    public ModelAndView getHomePage() {
    	ModelAndView model = new ModelAndView("home");
        LOGGER.debug("Getting home page");
        return model;
    }

}
