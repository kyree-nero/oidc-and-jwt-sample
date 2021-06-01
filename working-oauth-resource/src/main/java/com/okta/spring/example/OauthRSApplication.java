package com.okta.spring.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.lang.Assert;
import reactor.core.publisher.Mono;

@SpringBootApplication
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class OauthRSApplication {

    public static void main(String[] args) {
        SpringApplication.run(OauthRSApplication.class, args);
    }


    @Configuration
    static class WebConfig {
    	
    
    	
    	@Bean
        public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) throws Exception {

	          
        	return http
        	.authorizeExchange(exchange -> exchange
                    .pathMatchers("/messages/**").authenticated()
                    .anyExchange().authenticated()
                )
        	.formLogin().disable()
        	.httpBasic().disable()
        	.csrf().disable()
        	.oauth2ResourceServer()
        	.jwt().and()
        	
        	.and()
        	.build();
        	
        }
    	
    	
    }

    
    @RestController 
    @Configuration
    
    public class SampleController {
    	
    	
    	@GetMapping("/sample")
    	public Mono<Something> sample(@AuthenticationPrincipal Jwt authenticationPrincipal){
    		System.out.println(authenticationPrincipal.getSubject());
    		Assert.notNull(authenticationPrincipal);
    		Assert.notNull(authenticationPrincipal.getSubject());
    		Something s = new Something();
    		s.setContent("xyz from the resource");
    		return Mono.just(s);
    	}

		

		
    }

}
