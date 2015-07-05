package eu.wilkolek.diary.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.wilkolek.diary.dto.DayForm;
import eu.wilkolek.diary.model.CurrentUser;
import eu.wilkolek.diary.model.Day;
import eu.wilkolek.diary.model.DictionaryWord;
import eu.wilkolek.diary.model.Role;
import eu.wilkolek.diary.model.RoleEnum;
import eu.wilkolek.diary.model.User;
import eu.wilkolek.diary.repository.DayRepository;
import eu.wilkolek.diary.repository.DictionaryWordRepository;
import eu.wilkolek.diary.repository.UserRepository;
import eu.wilkolek.diary.repository.WordRepository;

@Controller
public class DayController {

	private CurrentUser currentUser;
	private DayRepository dayRepository;
	private WordRepository wordRepository;
	private DictionaryWordRepository dictionaryWordRepository;
	private UserRepository userRepository;

	@Autowired
	DayController(DayRepository dayRepository, WordRepository wordRepository,
			DictionaryWordRepository dictionaryWordRepository,
			UserRepository userRepository) {
		this.dayRepository = dayRepository;
		this.wordRepository = wordRepository;
		this.dictionaryWordRepository = dictionaryWordRepository;
		this.userRepository = userRepository;
	}

	@PreAuthorize("hasAuthority('USER')")
	@RequestMapping(value = "/user/day/add", method = RequestMethod.GET)
	public String add(Model model) {
		model.addAttribute("form", new DayForm());
		return "day/add";
	}

	

	@PreAuthorize("hasAuthority('USER')")
	@RequestMapping(value = "/user/day/list")
	public String listDays(Model model, CurrentUser user) throws Exception {
		Date now = new Date();
		Optional<User> u = userRepository.findById(currentUser.getId());
		if (u.isPresent()) {
			ArrayList<Day> days = dayRepository.get7DaysFromDate(u.get(), now);
			model.addAttribute("days", days);
			return "day/list";
		} else {
			throw new Exception("no user!");
		}
	}

}
