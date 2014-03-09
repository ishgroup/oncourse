package ish.oncourse.listeners;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.ResourceBundle;

import javax.servlet.ServletContextEvent;

import org.apache.commons.logging.LogFactory;

public class TheLeakPreventionListener extends OverrideLog4JListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		super.contextInitialized(sce);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		org.apache.log4j.LogManager.shutdown();
		LogFactory.release(classLoader);

		for (@SuppressWarnings("unchecked")
		Enumeration e = DriverManager.getDrivers(); e.hasMoreElements();) {
			Driver driver = (Driver) e.nextElement();
			if (driver.getClass().getClassLoader() == getClass().getClassLoader()) {
				try {
					DriverManager.deregisterDriver(driver);
				} catch (SQLException e1) {
				}
			}
		}

		ResourceBundle.clearCache(classLoader);
		java.beans.Introspector.flushCaches();
	}
	
	
}
