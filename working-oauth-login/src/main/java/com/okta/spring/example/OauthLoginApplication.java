package com.okta.spring.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@SpringBootApplication
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class OauthLoginApplication {

    public static void main(String[] args) {
        SpringApplication.run(OauthLoginApplication.class, args);
    }

   
    @Configuration
    static class WebSecurityConfiguration {
    	
    
    	
    	@Bean
        public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) throws Exception {

	          
        	return http
        	.authorizeExchange(exchange -> exchange
                    .pathMatchers("/", "/index.html").permitAll()
                    .pathMatchers("/messages/**").authenticated()
                    .anyExchange().authenticated()
                )
        	.formLogin().disable()
        	.httpBasic().disable()
        	.csrf().disable()
        	.oauth2Login().and()
        	.oauth2ResourceServer()
        	.jwt().and()
        	
        	.and()
        	.build();
        	
        }
    	
    	
    }

    @Configuration
    @Profile("!test")
    static class OauthClientConfiguration {
    	@Bean
    	WebClient webClient(
    			ReactiveClientRegistrationRepository clientRegistrations, 
    			ServerOAuth2AuthorizedClientRepository authenticatedPrincipalServerOAuth2AuthorizedClientRepository
    			) {
    	    ServerOAuth2AuthorizedClientExchangeFilterFunction oauth =
    	      new ServerOAuth2AuthorizedClientExchangeFilterFunction(
    	        clientRegistrations,
    	        authenticatedPrincipalServerOAuth2AuthorizedClientRepository
    	      );
    	    
    	    oauth.setDefaultClientRegistrationId("okta");
    	    return WebClient.builder()
    	      .filter(oauth)
    	      
    	      .build();
    	}
    	
    	
    	
    	
    }
    
    @RestController 
    @Configuration
    
    public class SampleController {
    	@Autowired WebClient webClient;
//    	@Autowired RestTemplate restTemplate;
    	@Value("${secondaryapp.host}") String secondaryAppHost;
    	@Value("${secondaryapp.port}") String secondaryAppPort;
    	@Value("${tertiaryapp.host}") String tertiaryAppHost;
    	@Value("${tertiaryapp.port}") String tertiaryAppPort;
    	
    	@GetMapping("/sample")
    	public Mono<Something> sample(@AuthenticationPrincipal OAuth2User authenticationPrincipal){
    		Assert.notNull(authenticationPrincipal, "The authentication principal can't be null");
    		System.out.println(authenticationPrincipal);
    		Something s = new Something();
    		s.setContent("xyz");
    		return Mono.just(s);
    	}

		
    	@GetMapping("/sample2")
    	public Mono<Something> sample2(@AuthenticationPrincipal OAuth2User authenticationPrincipal){
    		String target = secondaryAppHost+":"+ secondaryAppPort+"/sample";
    		System.out.println("...calling  " + target);
    		Assert.notNull(authenticationPrincipal, "The authentication principal can't be null");

    		  Mono<Something> result =  webClient
    				  .get()
    				  .uri(target)
    				  .attributes(
      					    ServerOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId("okta")
    				  )
    				  .retrieve()
    				  .bodyToMono(Something.class);
    				  
    		 return result;
    	}
    	
    	
    	@GetMapping("/sample3")
    	public Mono<Something> sample3(@AuthenticationPrincipal OAuth2User authenticationPrincipal){
    		String target = tertiaryAppHost+":"+ tertiaryAppPort+"/sample";
    		System.out.println("...calling  " + target);
    		Assert.notNull(authenticationPrincipal, "The authentication principal can't be null");

    		  Mono<Something> result =  webClient
    				  .get()
    				  .uri(target)
    				  .attributes(
      					    ServerOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId("okta")
    				  )
    				  .retrieve()
    				  .bodyToMono(Something.class);
    				  
    		 return result;
    		
    		
    	}
		
    }

}
