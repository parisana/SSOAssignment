package com.demo.spring_security;

import com.demo.spring_security.domain.Message;
import com.demo.spring_security.domain.MessageForm;
import com.demo.spring_security.domain.User;
import com.demo.spring_security.repositories.MessageRepo;
import com.demo.spring_security.repositories.UserRepo;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.query.Param;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@SpringBootApplication
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SpringSecurityApplication{
	public static void main(String[] args) {
		SpringApplication.run(SpringSecurityApplication.class, args);
	}
}

@Component
class DataLoader{
	private final UserRepo userRepo;
	private final MessageRepo messageRepo;

	public DataLoader(UserRepo userRepo, MessageRepo messageRepo) {
		this.userRepo = userRepo;
		this.messageRepo = messageRepo;
	}

	@Bean
	CommandLineRunner initData(){
		return (String... args) -> {
			Stream.of("pari, sana, ngpari.earth@gmail.com, password",
					"udit, gogoi, udit.earth@gmail.com, password",
					"A, Lastname, a@b.com, password",
					"rwinch, sana, rwinch.earth@gmail.com, password")
					.map(s -> s.split(", "))
					.forEach(strings -> userRepo.save(new User(strings[0], strings[1], strings[2], strings[3])));
			Stream.of("100, Hello Pari, This message is for Pari, 1",
					"101, Hello Rob, This message is for Rob, 3")
					.map(s -> s.split(", "))
					.forEach(strings -> {
						Long id= Long.parseLong(strings[0]);
						Message message= new Message(id, strings[1], strings[2]);
						message.setTo(userRepo.findById(Long.parseLong(strings[3])).orElseGet(User::new));
						messageRepo.save(message);
					});
		};
	}
}
//@Configuration
//class CustomFilter extends OncePerRequestFilter{
//
//	@Override
//	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//
//		logger.debug("****Request: "+request.toString());
//		logger.debug("****Response: "+response.toString());
//		logger.debug("*****Request URL: "+request.getRequestURL());
//
//		filterChain.doFilter(request, response);
//	}
//}