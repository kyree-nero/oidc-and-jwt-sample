package com.okta.spring.example;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@TestPropertySource(properties="debug=true")
@AutoConfigureMockMvc
@SpringBootTest

public class DoIT {
	@Autowired MockMvc mockMvc;
	@Autowired WebApplicationContext webApplicationContext;
	
	@BeforeEach public void before() {
		 mockMvc = MockMvcBuilders
				 .webAppContextSetup(webApplicationContext)
				 .apply(SecurityMockMvcConfigurers.springSecurity())
				 .build();
	}
	
	@Test public void test() throws Exception{
		Something expected = new Something();
		expected.setContent("xyz from the resource");
		
//		webTestClient
//			.mutateWith(
//					SecurityMockServerConfigurers.mockJwt()
//					.jwt(x -> x.subject("someuser"))
//			)
//			.get()
//			.uri("/sample")
//			.exchange()
//			.expectStatus()
//			.isOk()
//			.expectBody(Something.class)
//			.isEqualTo(expected);
		
		mockMvc.perform(
				MockMvcRequestBuilders.get("/sample")
					.with(
							SecurityMockMvcRequestPostProcessors.jwt()
							.jwt(token -> token.subject("someuser")))
				.accept(MediaType.APPLICATION_JSON))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.content", Matchers.notNullValue()))
				//.andExpect(MockMvcResultMatchers.jsonPath("$.content", "xyz from the resource")))
				.andReturn()
		;
	}
	
}
