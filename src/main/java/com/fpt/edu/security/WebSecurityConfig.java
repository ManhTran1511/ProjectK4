package com.fpt.edu.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
	@Autowired
	UserDetailsService userDetailsService;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//		http.cors().and().csrf().disable()
//				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
//				.authorizeRequests().antMatchers("/api/auth/**").permitAll()
//				.antMatchers("/**").permitAll()
//				.antMatchers("/api/test/**").permitAll()
//				.antMatchers("/user/**").permitAll()
//				.antMatchers("/account/**").permitAll()
//				.antMatchers("/admin/**").permitAll()
//				.antMatchers("/","/home","/menu","/reservation","/gallery","/about","/blog","/contact","/admin").permitAll()
//				.antMatchers("/admin/**").hasRole("ADMIN")
//				.anyRequest().authenticated()
//				.and()

		http.csrf().disable()
				.authorizeHttpRequests((authorize) ->
						authorize.antMatchers("/api/auth/**").permitAll()
								.antMatchers("/index").permitAll()
								.antMatchers("/admin/**").hasRole("ADMIN"))
				.formLogin(
						form -> form
								.loginPage("/api/auth/login")
								.loginProcessingUrl("/api/auth/login")
								.defaultSuccessUrl("/default?success").permitAll()
								.failureUrl("/api/auth/login?error")
				).logout(
						logout -> logout
								.logoutRequestMatcher(new AntPathRequestMatcher("/api/auth/logout"))
								.logoutSuccessUrl("/api/auth/login?logout")
								.permitAll()
				);

		return http.build();
	}

//	@Autowired
//	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//		auth
//				.userDetailsService(userDetailsService)
//				.passwordEncoder(passwordEncoder());
//	}
}
