package com.springapp.mvc.config;

import com.springapp.mvc.service.Impl.SimpleSocialUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.social.security.SpringSocialConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	@Qualifier("userDetailsService")
	UserDetailsService userDetailsService;

	@Bean
	public SocialUserDetailsService socialUserDetailsService() {
		return new SimpleSocialUserDetailsService(userDetailsService());
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.authorizeRequests()
				.antMatchers("/registration*").permitAll()
				.antMatchers("/login").permitAll()
				.antMatchers("/issecret**").permitAll()
				.antMatchers("/facebook").permitAll()
				.antMatchers("/signup").permitAll()
				.antMatchers("/callback").permitAll()
				.antMatchers("/callbackvk").permitAll()
				.antMatchers("/*").authenticated()
				.and().formLogin().loginPage("/login").failureUrl("/login?error")
				.defaultSuccessUrl("/")
				.usernameParameter("username").passwordParameter("password")
				.and()
				.logout().logoutSuccessUrl("/login?logout")
				.and()
				.apply(new SpringSocialConfigurer()
						.postLoginUrl("/")
						.alwaysUsePostLoginUrl(true))
				.and()
				.csrf().disable();

	}

	@Bean
	public PasswordEncoder passwordEncoder(){
		PasswordEncoder encoder = new BCryptPasswordEncoder();
		return encoder;
	}
}
