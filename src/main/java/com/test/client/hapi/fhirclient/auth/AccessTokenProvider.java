package com.test.client.hapi.fhirclient.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class AccessTokenProvider implements TokenProvider {

	private static final Logger LOGGER = LoggerFactory.getLogger(AccessTokenProvider.class);

	private String clientId;
	private String clientSecret;
	private String accessTokenUrl;
	private RestTemplate restTemplate;

	private Token token;

	public AccessTokenProvider(RestTemplate restTemplate, String clientId, String clientSecret, String accessTokenUrl) {
		this.restTemplate = restTemplate;
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.accessTokenUrl = accessTokenUrl;

	    this.token = getToken();
	}

	private Token getToken() {
		HttpHeaders headers = newHeaders();      
	 
	    HttpEntity<String> request = new HttpEntity<>(body("client_credentials"), headers);

	    ResponseEntity<Token> result = restTemplate.postForEntity(accessTokenUrl, request, Token.class);
		return result.getBody();
	}

	private String body(String grantType) {
		return "grant_type=" + grantType+ "&client_id=" + clientId + "&client_secret=" + clientSecret;
	}

	private HttpHeaders newHeaders() {
		HttpHeaders headers = new HttpHeaders();
	    
		headers.set("Content-Type", "application/x-www-form-urlencoded");    
	    headers.set("Accept", "application/json");
		return headers;
	}
	
	@Override
	public String getAccessToken() {
		if (this.token.isExpired()) {
			refreshToken();
		}

		return token.getAccessToken();
	}

	private void refreshToken() {
		LOGGER.debug("Token Experied, Refreshing token");

		HttpHeaders headers = newHeaders();      
		 
	    HttpEntity<String> request = new HttpEntity<>(refreshTokenBody(), headers);

	    ResponseEntity<Token> result = this.restTemplate.postForEntity(accessTokenUrl, request, Token.class);
		this.token = result.getBody();
		
	}

	private String refreshTokenBody() {
		String body = body("refresh_token");
		return body + "&refresh_token="+ token.getRefreshToken();
	}
}
