package com.westernacher.accounts.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ContextListener implements ServletContextListener {

	private final static Logger logger = Logger.getLogger(ContextListener.class.getName());
	public final static String CFG_FILE_NAME = "application.cfg";

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext servletContext = sce.getServletContext();

		Properties props = loadConfiguration(servletContext);

		AppConfiguration cfg = AppConfiguration.getInstance();
		
		cfg.setJdbcUrl(props.getProperty("jdbc.url")); 
		cfg.setJdbcUser(props.getProperty("jdbc.user"));
		cfg.setJdbcPassword(props.getProperty("jdbc.password"));
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub

	}

	public synchronized Properties loadConfiguration(ServletContext servletContext) {
		File applRoot = new File(servletContext.getRealPath("/WEB-INF/"));
		File cfgFolder = new File(applRoot.getParentFile().getParentFile().getParentFile().getAbsoluteFile(), "config");
		File cfgFile = new File(cfgFolder, CFG_FILE_NAME);
		
		System.out.println(cfgFile);
		
		FileInputStream fIn = null;
		Properties properties = new Properties();
		try {
			fIn = new FileInputStream(cfgFile);
			properties.load(fIn);
			
		} catch (IOException e) {
			logger.log(Level.ALL, "Error loading application configuration file", e);
		} finally {
			if (fIn != null) {
				try {
					fIn.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return properties;
	}
}
