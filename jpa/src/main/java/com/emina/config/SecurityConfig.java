package com.emina.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{

	@Autowired
	private DataSource ds;
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {		
		//povezano je sa tabelama u sql serveru
		auth.jdbcAuthentication().dataSource(ds)
		.usersByUsernameQuery("SELECT KORISNICKO_IME,LOZINKA,ENABLED FROM AUTENTIFIKACIJA WHERE KORISNICKO_IME=?")
		.authoritiesByUsernameQuery("SELECT KORISNICKO_IME,ULOGA FROM AUTENTIFIKACIJA WHERE KORISNICKO_IME=?");
		//uzima usera(kor ime, password i enabled) sa datim korisnickim imenom 
		//enabled = da je user aktivan
		//authorities - uzima ulogu za datog usera
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.authorizeRequests()
				/*
				 * .anyRequest().authenticated()
				 */			
			.antMatchers("/").permitAll()
			.antMatchers("/admin/**").hasRole("ADMIN") //na url koji pocinje sa /admin, moci ce da udje samo user sa admin ulogom
			.antMatchers("/usluge/**").hasRole("KLIJENT") //na url koji pocinje sa /usluge, moci ce da udje samo user sa klijent ulogom
			.and()
			.formLogin()
				.loginPage("/login") //ovde se nalazi login stranica
				.loginProcessingUrl("/autentifikacija") //na ovom url se obavlja autentifikacija (security se pobrinuo za to)
				.usernameParameter("korisnicko_ime") //menjanje parametra sa username na 'korisnicko_ime' (koristi se u formama)
				.passwordParameter("lozinka") //menjanje parametra sa password na 'lozinka' (koristi se u formama)
				.permitAll()
				.and()
				.logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
				.logoutSuccessUrl("/").deleteCookies("JSESSIONID") //nakon logout ide na / homepage
				.invalidateHttpSession(true); //brise sesiju nakon logout
				
				
		}
	
	
}

