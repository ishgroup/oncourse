package ish.oncourse.listeners;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.ResourceBundle;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.LogFactory;

public class TheLeakPreventionListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		// TODO Auto-generated method stub
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		org.apache.log4j.LogManager.shutdown();
		LogFactory.release(classLoader);

		for (Enumeration e = DriverManager.getDrivers(); e.hasMoreElements();) {
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
