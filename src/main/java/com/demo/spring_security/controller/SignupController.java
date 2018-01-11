package com.demo.spring_security.controller;

import com.demo.spring_security.domain.User;
import com.demo.spring_security.repositories.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/signup")
public class SignupController{
	private final UserRepo userRepo;

	SignupController(UserRepo userRepo) {
		this.userRepo = userRepo;
	}

	@GetMapping()
	public String signupForm(@ModelAttribute User user){
		log.debug("***Getting signup Form");
		return "user/signup";
	}
	@RequestMapping(method = RequestMethod.POST)
	public String signup(@Valid User user, BindingResult result,
                         RedirectAttributes redirect) {
		if (result.hasErrors()) {
			return "user/signup";
		}
		user = userRepo.save(user);
		redirect.addFlashAttribute("globalMessage", "Successfully signed up");

		List<GrantedAuthority> authorities =
				AuthorityUtils.createAuthorityList("ROLE_USER");
		Authentication auth =
				new UsernamePasswordAuthenticationToken(user, user.getPassword(), authorities);
		SecurityContextHolder.getContext().setAuthentication(auth);
		return "redirect:/";
	}
}
