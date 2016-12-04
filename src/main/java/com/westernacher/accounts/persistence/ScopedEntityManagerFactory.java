package com.westernacher.accounts.persistence;

import javax.persistence.EntityManagerFactory;

public interface ScopedEntityManagerFactory extends EntityManagerFactory {
	public ScopedEntityManager createScopedEntityManager();
}