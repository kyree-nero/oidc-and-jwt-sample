package com.okta.spring.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.lang.Assert;

@SpringBootApplication
@EnableWebSecurity
public class OauthRSApplication {

    public static void main(String[] args) {
        SpringApplication.run(OauthRSApplication.class, args);
    }


    @Configuration
    static class WebConfig extends WebSecurityConfigurerAdapter {
    	
    
    
    	
    	@Override protected void configure(HttpSecurity http) throws Exception {
    		http.csrf().and()
    			.authorizeRequests()
    			.antMatchers("/**").permitAll()
    			.mvcMatchers(HttpMethod.OPTIONS, "/**").permitAll()
    			.anyRequest()
    			.authenticated()
    			.and()
    			.oauth2ResourceServer()
    			.jwt();
    			
    	}
    	
    	
    }

    
    @RestController 
    @Configuration
    
    public class SampleController {
    	
    	
    	@GetMapping("/sample")
    	public Something sample(@AuthenticationPrincipal Jwt authenticationPrincipal){
    		System.out.println(authenticationPrincipal.getSubject());
    		Assert.notNull(authenticationPrincipal);
    		Assert.notNull(authenticationPrincipal.getSubject());
    		Something s = new Something();
    		s.setContent("abc from the resource");
    		return s;
    	}

		

		
    }

}
