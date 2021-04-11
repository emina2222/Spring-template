package com.emina.config;

import java.beans.PropertyVetoException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.mchange.v2.c3p0.ComboPooledDataSource;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages="com.emina")
@PropertySource("classpath:persistence-mssql.properties") //registrovanje property fajla i dodavanje u environment
public class AppConfig implements WebMvcConfigurer{
	
	@Autowired
	private Environment env; //cuva podatke procitane iz properties fajlova
	
	@Bean
	public DataSource securityDataSource() {
ComboPooledDataSource cpd=new ComboPooledDataSource();
		
		try {
			cpd.setDriverClass(env.getProperty("jdbc.driver")); //postavlja driver klasu za mssql iz properties fajla
		}catch(PropertyVetoException ex) {
			throw new RuntimeException(ex);
		}
				
		cpd.setJdbcUrl(env.getProperty("jdbc.url")); //postavlja jdbc url iz property fajla
		
		//postavlja connection pool props
		
		cpd.setInitialPoolSize(getIntProperty("connection.pool.initialPoolSize"));
		cpd.setMinPoolSize(getIntProperty("connection.pool.minPoolSize"));
		cpd.setMaxPoolSize(getIntProperty("connection.pool.maxPoolSize"));
		cpd.setMaxIdleTime(getIntProperty("connection.pool.maxIdleTime"));

		return cpd;
	}

	//parsiranje iz stringa u integer za potrebe setovanja connection pool properija
	private int getIntProperty(String name) {
		String val=env.getProperty(name);
		int broj=Integer.parseInt(val);
		return broj;
	}
	
	@Bean
	public ViewResolver viewResolver() {
		InternalResourceViewResolver viewResolver=new InternalResourceViewResolver();
		
		viewResolver.setPrefix("/WEB-INF/views/"); //trazi jsp fajlove u ovom folderu
		viewResolver.setSuffix(".jsp");
		
		return viewResolver;
	}

	//Za welcome fajl nije potrebna konfiguracija, jer java odmah trazi index.html/jsp u webapp folderu
	//Njegov url mapping je "/"
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		
		//registruje css folder
		registry.addResourceHandler("/resources/**").addResourceLocations("classpath:/statics/");
		//registruje folder sa slikama
		registry.addResourceHandler("/images/**").addResourceLocations("/WEB-INF/images/");
		//registruje bootstrap i jquery jars
		registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
	}
}
