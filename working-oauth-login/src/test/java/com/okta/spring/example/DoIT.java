package com.okta.spring.example;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;


@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@TestPropertySource(properties="debug=true")
@AutoConfigureWebTestClient
@SpringBootTest

public class DoIT {
	static WireMockServer wireMockServer = null;
	
	@BeforeAll 
	public static void beforeAll(){
		wireMockServer = new WireMockServer(new WireMockConfiguration().dynamicPort());
		
	    wireMockServer.start();
	}
	
	@DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("secondaryapp.port", () -> String.valueOf(wireMockServer.port()));
        registry.add("secondaryapp.host", () -> "http://localhost");
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri", () -> "http://localhost:"+String.valueOf(wireMockServer.port()));
        registry.add("spring.security.oauth2.client.provider.okta.issuer-uri", () -> "http://localhost:"+String.valueOf(wireMockServer.port()));
        
    }
	
	@AfterAll
	public static void afterAll() {
		wireMockServer.stop();
	}
	
	@BeforeEach
	public void before() {
		wireMockServer.resetAll();
	}
	
	
	
	@MockBean ReactiveClientRegistrationRepository reactiveClientRegistrationRepository;
	@MockBean ReactiveJwtDecoder reactiveJwtDecoder;

	
	
	
	@Autowired WebTestClient webTestClient;
	
	
	
	@Test public void testLocal() throws Exception{
		Something expected = new Something();
		expected.setContent("xyz");
		
		webTestClient
			.mutateWith(
					SecurityMockServerConfigurers.mockOidcLogin()
			)
			.get()
			
			.uri("/sample")
			
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody(Something.class)
			.isEqualTo(expected);
		
	}
	
	@Test public void testResource() throws Exception{
		Something expected = new Something();
		expected.setContent("xyz from the resource");
		
		
		
		wireMockServer.stubFor(
			      WireMock.get(WireMock.urlMatching("/sample"))
			        .willReturn(
			          WireMock.aResponse()
			          .withStatus(200)
			          .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
			          .withBody("{\"content\":\"xyz from the resource\"}")
			         )
			    );
		
	
		webTestClient
			.mutateWith(
					SecurityMockServerConfigurers.mockOidcLogin()
			)
			.get()
			.uri("/sample2")
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody(Something.class)
			.isEqualTo(expected);
	
	}
	
}
