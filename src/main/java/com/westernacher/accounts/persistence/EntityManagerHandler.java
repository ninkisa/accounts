package com.westernacher.accounts.persistence;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

public class EntityManagerHandler implements InvocationHandler {

	private static final Method executeTransaction;
	private static final Method executeVoidTransaction;
	private static final Logger logger;

	static {
		try {
			executeTransaction = ScopedEntityManager.class.getMethod("executeTransaction",
					ScopedEntityManager.TransactionFunction.class);
			executeVoidTransaction = ScopedEntityManager.class.getMethod("executeTransaction",
					ScopedEntityManager.Transaction.class);
			logger = Logger.getLogger(ScopedEntityManager.class.getCanonicalName());
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	private final EntityManager em;

	protected Object proxyExecuteTransaction(Object fn) {
		EntityTransaction t = em.getTransaction();
		Object result = null;
		try {
			t.begin();
			if (fn instanceof ScopedEntityManager.Transaction) {
				((ScopedEntityManager.Transaction) fn).execute();
			} else if (fn instanceof ScopedEntityManager.TransactionFunction) {
				((ScopedEntityManager.TransactionFunction) fn).execute();
			} else {
				assert(false);
			}
			t.commit();
		} finally {
			if (t.isActive()) {
				t.rollback();
			}
		}
		return result;
	}
	
	public EntityManagerHandler(EntityManager em) {
		this.em = em;
	}
	
	@Override
	protected void finalize() throws Throwable {
		try {
			if (em.isOpen()) {
				logger.warning("Closing enity manager by finalizer!");
				em.close();
			}
		} finally {
			super.finalize();
		}
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		logger.entering(EntityManagerHandler.class.getCanonicalName(), method.getName(), args);
		Object result;
		try {
			if (method.equals(executeVoidTransaction) || method.equals(executeTransaction)) {
				assert (args.length == 1);
				result = proxyExecuteTransaction(args[0]);
			} else {
				result = method.invoke(em, args);
			}
		} catch (Throwable e) {
			logger.throwing(ScopedEntityManager.class.getCanonicalName(), method.getName(), e);
			throw e;
		}
		if (method.getReturnType().getClass().equals(Void.class.getClass())){
			logger.exiting(ScopedEntityManager.class.getCanonicalName(), method.getName());
		} else {
			logger.exiting(ScopedEntityManager.class.getCanonicalName(), method.getName(), result);
		}
		return result;
	}

}
