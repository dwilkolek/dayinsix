package eu.wilkolek.diary.controller;


import org.omg.CORBA.UserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import eu.wilkolek.diary.model.CurrentUser;
import eu.wilkolek.diary.model.Day;
import eu.wilkolek.diary.model.DictionaryWord;
import eu.wilkolek.diary.model.Role;
import eu.wilkolek.diary.model.RoleEnum;
import eu.wilkolek.diary.model.User;
import eu.wilkolek.diary.dto.DayForm;
import eu.wilkolek.diary.dto.UserCreateForm;
import eu.wilkolek.diary.dto.UserCreateFormValidator;
import eu.wilkolek.diary.repository.*;

import javax.validation.Valid;

import java.util.ArrayList;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Optional;

@Controller
public class UserController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(UserController.class);
	private final UserRepository userRepository;
	private final DayRepository dayRepository;
	private final DictionaryWordRepository dictionaryWordRepository;
	
//	private final CurrentUser currentUser;
	
	private final UserCreateFormValidator userCreateFormValidator;

	@Autowired
	public UserController(UserRepository userRepository, DayRepository dayRepository, DictionaryWordRepository dictionaryWordRepository,
			
			UserCreateFormValidator userCreateFormValidator) {
		this.userRepository = userRepository;
		this.dayRepository  = dayRepository;
		this.dictionaryWordRepository = dictionaryWordRepository;
		
//		this.currentUser = currentUser;
		
		this.userCreateFormValidator = userCreateFormValidator;
	}

	@InitBinder("form")
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(userCreateFormValidator);
	}

//	@PreAuthorize("@currentUserServiceImpl.canAccessUser(principal, #id)")
//	@RequestMapping("/user/{id}")
//	public ModelAndView getUserPage(@PathVariable Long id) {
//		LOGGER.debug("Getting user page for user={}", id);
//		return new ModelAndView("user", "user", userService.getUserById(id)
//				.orElseThrow(
//						() -> new NoSuchElementException(String.format(
//								"User=%s not found", id))));
//	}

//	@PreAuthorize("hasAuthority('ADMIN')")
//	// @RequestMapping(value = "/user/create", method = RequestMethod.GET)
//	public ModelAndView getUserCreatePage() {
//		LOGGER.debug("Getting user create form");
//		return new ModelAndView("user_create", "form", new UserCreateForm());
//	}

	@PreAuthorize("hasAuthority('USER')")
	@RequestMapping(value = "/user/details")
	public ModelAndView details() {
		return new ModelAndView("/user/details");
	}

	@PreAuthorize("hasAuthority('USER')")
	@RequestMapping(value = "/user/days")
	public ModelAndView days(CurrentUser currentUser) throws Exception {
		Optional<User> user = userRepository.findById(currentUser.getId());
		if (!user.isPresent()){
			throw new Exception("CurrentUser not found id="+currentUser.getId());
		}
		ArrayList<Day> days = dayRepository.get7DaysFromDate(user.get(), new Date());
		ModelAndView model = new ModelAndView("/day/list");
		model.getModelMap().put("days", days);
		
		return model;
	}
	
	@PreAuthorize("hasAuthority('USER')")
	@RequestMapping(value = "/user/day/add", method = RequestMethod.POST)
	public String saveDay(CurrentUser currentUser, @Valid DayForm dayForm, BindingResult result) {
		if (result.hasErrors()) {
			return "day/add";
		}

		// TODO :view :currentUser :auth
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

		Day day = dayRepository.save(new Day(dayForm, user));

		userRepository.addDayRef(user, day);
		
		return "redirect:/user/days";
	}
	
}
