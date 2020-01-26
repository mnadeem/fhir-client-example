package com.test.client.hapi.fhirclient.auth;

import org.apache.commons.lang3.Validate;

import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IHttpRequest;
import ca.uhn.fhir.rest.client.api.IHttpResponse;

public class BearerTokenAuthInterceptor implements IClientInterceptor {
	
	private final TokenProvider tokenProvider;

	public BearerTokenAuthInterceptor(TokenProvider tokenProvider) {
		Validate.notNull(tokenProvider, "tokenProvider must not be null");
		this.tokenProvider = tokenProvider;
	}

	@Override
	public void interceptRequest(IHttpRequest theRequest) {
		theRequest.addHeader(Constants.HEADER_AUTHORIZATION, (Constants.HEADER_AUTHORIZATION_VALPREFIX_BEARER + tokenProvider.getAccessToken()));
	}

	@Override
	public void interceptResponse(IHttpResponse theResponse) {
		// nothing
	}

}
