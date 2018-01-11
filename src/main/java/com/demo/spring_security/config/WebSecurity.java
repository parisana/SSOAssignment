package com.demo.spring_security.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

	@Bean
	public PasswordEncoder passwordEncoder(){
		return NoOpPasswordEncoder.getInstance();
	}

    @Override
	protected void configure(HttpSecurity http) throws Exception {
		log.debug("**Configuring HttpSecurity**");
		http
				.csrf().disable()
				.headers().disable()
				.authorizeRequests()
					.antMatchers("/h2-console/**").permitAll()
//					.antMatchers("/**").permitAll()
                    .antMatchers("/resources/**").permitAll()
                    .antMatchers("/signup").permitAll()
                    .antMatchers("/login**").permitAll()
				.anyRequest().hasRole("USER")
				.anyRequest().authenticated()
                    .and()
				.oauth2Login().defaultSuccessUrl("/oauth2/index")
					.loginPage("/login")
					.and()
				.formLogin()
                    .loginPage("/login") //add custom login page
					.successForwardUrl("/messages")
                        .and()
                .logout()
                    .permitAll();
	}
	@Autowired
	protected void configureGlobal(UserDetailsService userDetailsService, AuthenticationManagerBuilder auth) throws Exception {
		log.debug("**configuring AuthenticationManagerBuilder");
		auth
				.userDetailsService(userDetailsService);
//				.jdbcAuthentication()
//                .dataSource(dataSource)
//				.withDefaultSchema()
//				.usersByUsernameQuery("select email, password, true from user where email=?")
//				.authoritiesByUsernameQuery("select email, password, 'ROLE_USER' from user where email=?");
	}
}
