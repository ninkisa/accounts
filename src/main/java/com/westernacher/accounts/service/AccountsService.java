package com.westernacher.accounts.service;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.westernacher.accounts.persistence.Account;

@Path("/accservice")
@Produces({ "application/json" })
public interface AccountsService {
	@GET
	@Path("/{id}")
	@Produces({ "application/json" })
	public Account get(Long id);

	@GET
	@Path("/")
	@Produces({ "application/json" })
	public Response list();

	@DELETE
	@Path("/")
	@Produces({ "application/json" })
	public Response delete(List<Account> accounts);

	@POST
	@Path("/")
	@Consumes({ "application/json" })
	public Response add(Account acc);

	@PUT
	@Path("/")
	@Produces({ "application/json" })
	public Response edit(Account acc);
}
