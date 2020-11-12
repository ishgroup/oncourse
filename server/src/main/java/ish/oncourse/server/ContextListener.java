/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Context change listener.
 */
public class ContextListener implements ServletContextListener {

	private static final Logger logger = LogManager.getLogger();

	/**
	 * Default constructor.
	 */
	public ContextListener() {}

	/**
	 * Called when context is initialized.
	 */
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		logger.debug("Context is initialized...");
	}

	/**
	 * Called when context is destroyed.
	 */
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		logger.debug("Context is destroyed...");
	}

}
