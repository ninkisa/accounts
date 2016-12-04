package com.westernacher.accounts.persistence;
import javax.persistence.EntityManager;

public interface ScopedEntityManager extends EntityManager, AutoCloseable {

	@FunctionalInterface
	public interface Transaction {
		public void execute();
	}
	
	@FunctionalInterface
	public interface TransactionFunction<T> {
		public T execute();
	}
	
	public void executeTransaction(Transaction t);
	public <T> T executeTransaction(TransactionFunction<T> t);
}
