package com.westernacher.accounts.persistence;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.spi.PersistenceUnitTransactionType;

import com.westernacher.accounts.app.AppConfiguration;

import static org.eclipse.persistence.config.PersistenceUnitProperties.JDBC_DRIVER;
import static org.eclipse.persistence.config.PersistenceUnitProperties.JDBC_PASSWORD;
import static org.eclipse.persistence.config.PersistenceUnitProperties.JDBC_URL;
import static org.eclipse.persistence.config.PersistenceUnitProperties.JDBC_USER;
import static org.eclipse.persistence.config.PersistenceUnitProperties.TRANSACTION_TYPE;

public class PersistenceManager {

	private static final PersistenceManager instance = new PersistenceManager();
	protected ScopedEntityManagerFactory semf;
	
	public PersistenceManager() {
	}
	
	public static PersistenceManager getInstance() {
		return instance;
	}

	public ScopedEntityManagerFactory getScopedEntityManagerFactory() {
		if (semf == null) {
			createScopedEntityManagerFactory();
		}
		return semf;
	}

	public void closeEntityManagerFactory() {
		if (semf != null) {
			semf.close();
			semf = null;
		}
	}

	private void createScopedEntityManagerFactory() {
		
		Map properties = new HashMap();
		
		// Configure the internal EclipseLink connection pool
	    properties.put(JDBC_URL, AppConfiguration.getInstance().getJdbcUrl());
	    properties.put(JDBC_USER, AppConfiguration.getInstance().getJdbcUser());
	    properties.put(JDBC_PASSWORD, AppConfiguration.getInstance().getJdbcPassword());
		
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("accounts", properties);
		
		
		semf = (ScopedEntityManagerFactory) Proxy.newProxyInstance(ScopedEntityManagerFactory.class.getClassLoader(), 
				new Class[]{ScopedEntityManagerFactory.class}, 
				new EntityManagerFactoryHandler(emf));
	}
}
