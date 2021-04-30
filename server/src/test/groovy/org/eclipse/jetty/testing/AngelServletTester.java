/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package org.eclipse.jetty.testing;

import com.google.inject.Provider;
import ish.oncourse.server.AngelServerFactory;
import ish.oncourse.server.PreferenceController;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.LocalConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.Servlet;
import java.nio.ByteBuffer;
import java.util.EnumSet;

public class AngelServletTester extends ContainerLifeCycle {

	private static PreferenceController prefController;
	private static Provider<Server> serverProvider;
	private static AngelServerFactory serverFactory;


	public AngelServletTester(final Provider<Server> serverProvider,
							  final PreferenceController prefController,
							  final AngelServerFactory serverFactory) {
		this("/",ServletContextHandler.SECURITY|ServletContextHandler.SESSIONS);
		AngelServletTester.prefController = prefController;
		AngelServletTester.serverProvider = serverProvider;
		AngelServletTester.serverFactory = serverFactory;
	}

	public AngelServletTester(String contextPath,int options)
	{
		_context=new ServletContextHandler(_server,contextPath,options);
		_server.setConnectors(new Connector[]{_connector});
		addBean(_server);
	}

	public void addServlet(ServletHolder holder, String pathSpec) {
		this._context.addServlet(holder, pathSpec);
	}

//	public void addSecurityFilter(String pathSpec, EnumSet<DispatcherType> dispatches) {
//		SecurityFilter filter = new SecurityFilter();
//
//		_context.addFilter(new FilterHolder(filter), pathSpec, dispatches);
//	}

	private static final Logger LOG = Log.getLogger(AngelServletTester.class);

	private final Server _server=new Server();
	private final LocalConnector _connector=new LocalConnector(_server);
	private final ServletContextHandler _context;

	public Server getServer()
	{
		return _server;
	}

	public ServletHolder addServlet(Class<? extends Servlet> servlet, String pathSpec)
	{
		return _context.addServlet(servlet,pathSpec);
	}

	public FilterHolder addFilter(Class<? extends Filter> filterClass, String pathSpec, EnumSet<DispatcherType> dispatches)
	{
		return _context.addFilter(filterClass,pathSpec,dispatches);
	}

	public void setContextPath(String contextPath)
	{
		_context.setContextPath(contextPath);
	}

	public ServletContextHandler getContext()
	{
		return _context;
	}

	public ByteBuffer getResponses(ByteBuffer request) throws Exception
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Request (Buffer): {}", BufferUtil.toUTF8String(request));
		}
		return _connector.getResponses(request);
	}
}
