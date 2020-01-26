package com.test.client.hapi.fhirclient.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.test.client.hapi.fhirclient.auth.AccessTokenProvider;
import com.test.client.hapi.fhirclient.auth.BearerTokenAuthInterceptor;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;

@Configuration
public class FhirConfig {

	@Value("${fhir.clientId}")
	private String clientId;
	@Value("${fhir.clientSecret}")
	private String clientSecret;
	@Value("${fhir.serverBase}")
	private String fhirServerBase;
	@Value("${fhir.authTokenProvider}")
	private String accessTokenUrl;

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	@Bean
	public AccessTokenProvider accessTokenProvider(RestTemplate restTemplate) {
		return new AccessTokenProvider(restTemplate, this.clientId, this.clientSecret, this.accessTokenUrl);
	}

	@Bean
	public FhirContext fhirContext() {
		return FhirContext.forR4();
	}

	@Bean 
	public IGenericClient fhirClient(FhirContext ctx, AccessTokenProvider tokenProvider) {
		IGenericClient client = ctx.newRestfulGenericClient(fhirServerBase);
		client.registerInterceptor(new BearerTokenAuthInterceptor(tokenProvider));
		client.registerInterceptor(new LoggingInterceptor(true));
		return client;
	}
}
