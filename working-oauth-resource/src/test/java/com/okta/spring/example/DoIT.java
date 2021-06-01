package com.okta.spring.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;


@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@TestPropertySource(properties="debug=true")
@AutoConfigureWebTestClient
@SpringBootTest

public class DoIT {
	@Autowired WebTestClient webTestClient;
	
	@Test public void test() {
		Something expected = new Something();
		expected.setContent("xyz from the resource");
		
		webTestClient
			.mutateWith(
					SecurityMockServerConfigurers.mockJwt()
					.jwt(x -> x.subject("someuser"))
			)
			.get()
			.uri("/sample")
			.exchange()
			.expectStatus()
			.isOk()
			.expectBody(Something.class)
			.isEqualTo(expected);
	}
	
}
