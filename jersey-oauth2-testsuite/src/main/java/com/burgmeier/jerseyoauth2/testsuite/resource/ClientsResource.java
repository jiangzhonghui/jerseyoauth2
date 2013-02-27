package com.burgmeier.jerseyoauth2.testsuite.resource;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.burgmeier.jerseyoauth2.api.client.ClientServiceException;
import com.burgmeier.jerseyoauth2.api.client.IClientService;
import com.burgmeier.jerseyoauth2.api.client.IRegisteredClientApp;
import com.google.inject.Inject;

@Path("/clients")
public class ClientsResource {

	private final IClientService clientService;
	
	@Inject
	public ClientsResource(final IClientService clientService) {
		super();
		this.clientService = clientService;
	}

	@POST
	@Produces({MediaType.APPLICATION_JSON})
	public ClientEntity createClient(@QueryParam("appName") String appName, @QueryParam("callbackUrl") String callbackUrl) throws ClientServiceException
	{
		IRegisteredClientApp regClient = clientService.registerClient(appName, callbackUrl);
		return new ClientEntity(regClient);
	}
	
}
