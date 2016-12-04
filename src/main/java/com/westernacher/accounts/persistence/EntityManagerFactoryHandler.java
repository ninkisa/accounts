package com.westernacher.accounts.persistence;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class EntityManagerFactoryHandler implements InvocationHandler {

	private static final Method createEntityManager;
	private static final Method createScopedEntityManager;
	private static final Logger logger;

	static {
		try {
			createEntityManager = EntityManagerFactory.class.getMethod("createEntityManager");
			createScopedEntityManager = ScopedEntityManagerFactory.class.getMethod("createScopedEntityManager");
			logger = Logger.getLogger(ScopedEntityManagerFactory.class.getCanonicalName());
		} catch (NoSuchMethodException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	private final EntityManagerFactory emf;

	public EntityManagerFactoryHandler(EntityManagerFactory emf) {
		this.emf = emf;
	}

	public Object invoke(Object o, Method method, Object[] os) throws Throwable {
		logger.entering(ScopedEntityManagerFactory.class.getCanonicalName(), method.getName(), os);
		
		Object result;
		try {
			if (method.equals(createScopedEntityManager)) {
				assert (os.length == 0);
				EntityManager target = (EntityManager) createEntityManager.invoke(emf, os);
				result = Proxy.newProxyInstance(ScopedEntityManagerFactory.class.getClassLoader(), new Class[]{ScopedEntityManager.class}, new EntityManagerHandler(target));
			} else {
				result = method.invoke(emf, os);
			}
		} catch (Throwable th) {
			logger.throwing(ScopedEntityManagerFactory.class.getCanonicalName(), method.getName(), th);
			throw th;
		}
		
		if (method.getReturnType().getClass().equals(Void.class.getClass())){
			logger.exiting(ScopedEntityManagerFactory.class.getCanonicalName(), method.getName());
		} else {
			logger.exiting(ScopedEntityManagerFactory.class.getCanonicalName(), method.getName(), result);
		}
		return result;
	}
}
