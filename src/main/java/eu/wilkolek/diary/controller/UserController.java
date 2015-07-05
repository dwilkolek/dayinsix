package eu.wilkolek.diary.controller;


import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import eu.wilkolek.diary.dto.DayForm;
import eu.wilkolek.diary.dto.DayFormValidator;
import eu.wilkolek.diary.dto.UserCreateFormValidator;
import eu.wilkolek.diary.model.CurrentUser;
import eu.wilkolek.diary.model.Day;
import eu.wilkolek.diary.model.DictionaryWord;
import eu.wilkolek.diary.model.User;
import eu.wilkolek.diary.repository.DayRepository;
import eu.wilkolek.diary.repository.DictionaryWordRepository;
import eu.wilkolek.diary.repository.UserRepository;

@Controller
public class UserController {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
	
	private final UserRepository userRepository;
	private final DayRepository dayRepository;
	private final DictionaryWordRepository dictionaryWordRepository;
	
//	private final CurrentUser currentUser;
	
//	private final UserCreateFormValidator userCreateFormValidator;
//
//	private DayFormValidator dayFormValidator;

	@Autowired
	public UserController(UserRepository userRepository, DayRepository dayRepository, DictionaryWordRepository dictionaryWordRepository) {
		this.userRepository = userRepository;
		this.dayRepository  = dayRepository;
		this.dictionaryWordRepository = dictionaryWordRepository;
		
//		this.currentUser = currentUser;
//		this.dayFormValidator = dayFormValidator;
//		this.userCreateFormValidator = userCreateFormValidator;
	}

//	@InitBinder("userForm")
//	public void initBinder(WebDataBinder binder) {
//		binder.addValidators(userCreateFormValidator);	
//	}
//
//	
//	
	
	@PreAuthorize("hasAuthority('USER')")
	@RequestMapping(value = "/user/details")
	public ModelAndView details() {
		return new ModelAndView("/user/details");
	}

	@PreAuthorize("hasAuthority('USER')")
	@RequestMapping(value = "/user/day/list")
	public ModelAndView days(CurrentUser currentUser) throws Exception {
		Optional<User> user = userRepository.findById(currentUser.getId());
		if (!user.isPresent()){
			throw new Exception("CurrentUser not found id="+currentUser.getId());
		}
		ArrayList<Day> days = dayRepository.get7DaysFromDate(user.get(), new Date());
		ModelAndView model = new ModelAndView("user/day/list");
		model.getModelMap().put("days", days);
		
		return model;
	}
	
	@PreAuthorize("hasAuthority('USER')")
	@RequestMapping(value = "/user/day/add", method = RequestMethod.POST)
	public String saveDay(CurrentUser currentUser, @Valid DayForm dayForm, BindingResult result) {
		if (result.hasErrors()) {
			return "user/day/add";
		}

		User user = currentUser.getUser();
		System.out.println("User id:" + user.getId());
		if (dayForm.getWords().size() > 0) {
			ArrayList<DictionaryWord> resultList = new ArrayList<DictionaryWord>();
			for (int i = 0; i < dayForm.getWords().size(); i++) {
				String value = dayForm.getWords().get(i);

				Optional<DictionaryWord> dictWordO = dictionaryWordRepository
						.findByValue(value);
				if (dictWordO.isPresent()) {
					resultList.add(dictWordO.get());
				} else {
					DictionaryWord dw = new DictionaryWord();
					dw.setValue(value);
					DictionaryWord dwSaved = dictionaryWordRepository.save(dw);
					resultList.add(dwSaved);
				}
			}
			dayForm.setDictionaryWords(resultList);
		}

		dayRepository.save(new Day(dayForm, user));
		LOGGER.debug("User "+user.getEmail()+" added new day");
	//	userRepository.addDayRef(user, day);
		
		return "redirect:/user/day/list";
	}
	
	@PreAuthorize("hasAuthority('USER')")
	@RequestMapping(value = "/user/day/add", method = RequestMethod.GET)
	public String add(Model model) {
		model.addAttribute("form", new DayForm());
		return "user/day/add";
	}
	
}
