package com.westernacher.accounts.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.westernacher.accounts.persistence.Account;
import com.westernacher.accounts.persistence.PersistenceManager;
import com.westernacher.accounts.persistence.ScopedEntityManager;
import com.westernacher.accounts.utils.EmailValidator;
import com.westernacher.accounts.utils.ValidationException;

public class AccountsServiceDBImpl implements AccountsService {
	public void init() {

	}

	public Account get(@PathParam("id") Long id) {

		try (ScopedEntityManager em = getScopedEntityManager()) {
			em.find(Account.class, id);
		}
		return null;
	}

	public Response list() {
		try (ScopedEntityManager em = getScopedEntityManager()) {
			TypedQuery<Account> q = em.createNamedQuery("Account.getAll", Account.class);
			List<Account> result = q.getResultList();
			return Response.ok(result).build();
		}
	}

	public Response delete(List<Account> accounts) {
		try (ScopedEntityManager em = getScopedEntityManager()) {
			accounts.forEach(account -> {
				Account acc = em.find(Account.class, account.getId());
				if (acc != null)
					em.executeTransaction(() -> em.remove(acc));
			});

			return Response.ok().build();
		}
	}

	public Response add(Account acc) {
		
		try {
			validate(acc);
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getLocalizedMessage()).build();
		}
		
		try (ScopedEntityManager em = getScopedEntityManager()) {
			em.executeTransaction(() -> em.persist(acc));
			return Response.ok().build();
		}
	}

	public Response edit(Account acc) {
		try {
			validate(acc);
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getLocalizedMessage()).build();
		}
		
		try (ScopedEntityManager em = getScopedEntityManager()) {
			em.executeTransaction(() -> em.merge(acc));
			return Response.ok().build();
		}
	}

	private ScopedEntityManager getScopedEntityManager() {
		return PersistenceManager.getInstance().getScopedEntityManagerFactory().createScopedEntityManager();
	}
	
	private void validate(Account acc) throws ValidationException {
		if (!new EmailValidator().validateEmail(acc.getEmail())) {
			throw new ValidationException("EMail is not valid");
		}
		
		if (acc.getBirthDate() != null) {
			LocalDate now = LocalDate.now();
			LocalDate date = acc.getBirthDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			if (date.isAfter(now)) {
				throw new ValidationException("Birth date should be in the past");
			}
		}
	}

}
