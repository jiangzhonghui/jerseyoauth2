package com.burgmeier.jerseyoauth2.test.client;

import java.io.IOException;

import org.apache.amber.oauth2.common.message.types.GrantType;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import com.burgmeier.jerseyoauth2.client.scribe.IOAuth2Service;
import com.burgmeier.jerseyoauth2.client.scribe.OAuth2Token;
import com.burgmeier.jerseyoauth2.testsuite.resource.ClientEntity;
import com.burgmeier.jerseyoauth2.testsuite.resource.SampleEntity;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestClient {
	
	private String clientId;
	private String clientSecret;
	
	public TestClient(ClientEntity clientEntity) {
		super();
		this.clientId = clientEntity.getClientId();
		this.clientSecret = clientEntity.getClientSecret();
	}
	
	public TestClient(String clientId, String clientSecret)
	{
		super();
		this.clientId = clientId;
		this.clientSecret = clientSecret;
	}

	protected OAuthService getOAuthService(GrantType grantType)
	{
		OAuthService service = new ServiceBuilder()
		.provider(new LocalTestAPI(grantType.toString()))
		.apiKey(clientId)
		.apiSecret(clientSecret)
	    .scope("test1 test2")
		.build();
		return service;
	}
	
	public String getAuthUrl()
	{
		OAuthService service = getOAuthService(GrantType.AUTHORIZATION_CODE);
		return service.getAuthorizationUrl(null);
	}

	public Token getAccessToken(String code) {
		OAuthService service = getOAuthService(GrantType.AUTHORIZATION_CODE);
		return service.getAccessToken(null, new Verifier(code));
	}
	
	public Token refreshToken(OAuth2Token token) {
		OAuthService service = getOAuthService(GrantType.REFRESH_TOKEN);
		return ((IOAuth2Service)service).refreshToken(token);
	}		
	
	public String sendTestRequest(Token accessToken) throws ClientException
	{
		OAuthService service = getOAuthService(GrantType.AUTHORIZATION_CODE);
		
		OAuthRequest request = new OAuthRequest(Verb.GET,
				"http://localhost:9998/testsuite/rest/sample/1");
		service.signRequest(accessToken, request);
		Response response = request.send();
		if (response.getCode()!=200)
			throw new ClientException(response.getBody());
		return response.getBody();
	}
	
	public SampleEntity retrieveEntity(Token accessToken) throws ClientException
	{
		OAuthService service = getOAuthService(GrantType.AUTHORIZATION_CODE);
		
		OAuthRequest request = new OAuthRequest(Verb.GET,
				"http://localhost:9998/testsuite/rest/sample/1");
		service.signRequest(accessToken, request);
		Response response = request.send();
		if (response.getCode()!=200)
			throw new ClientException(response.getBody());
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			SampleEntity entity = mapper.readValue(response.getBody(), SampleEntity.class);
			return entity;
		} catch (IOException e) {
			throw new ClientException(response.getBody());
		}
	}	


	
}