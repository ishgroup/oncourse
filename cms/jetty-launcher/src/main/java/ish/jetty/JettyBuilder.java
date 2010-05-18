package ish.jetty;

import javax.naming.NamingException;

import org.eclipse.jetty.plus.jndi.Resource;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * A class that creates and configures a Jetty server object suitable for
 * running the apps from Eclipse.
 */
public class JettyBuilder {

	protected String war;
	protected int port;
	protected String context;

	protected String dataSourceName;
	protected DataSourceFactory dataSourceFactory;

	public Server toServer() {
		Server server = new Server();

		SelectChannelConnector connector = new SelectChannelConnector();
		connector.setPort(port);

		server.addConnector(connector);
		Resource connectionPool;
		try {
			connectionPool = new Resource(dataSourceName, dataSourceFactory
					.getDataSource());
		} catch (NamingException e) {
			throw new RuntimeException("Error creating DataSource", e);
		}

		server.addBean(connectionPool);

		WebAppContext webappContext = new WebAppContext();
		webappContext.setContextPath(context);
		webappContext.setWar(war);

		server.setHandler(webappContext);

		server.setStopAtShutdown(true);
		server.setSendServerVersion(true);

		return server;
	}

	public void setWar(String war) {
		this.war = war;
	}

	public void setPort(String port) {
		this.port = Integer.parseInt(port);
	}

	public void setContext(String context) {
		this.context = context;
	}

	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}

	public void setDataSourceFactory(String dataSourceFactory) throws Exception {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		this.dataSourceFactory = (DataSourceFactory) Class.forName(
				dataSourceFactory, true, loader).newInstance();
	}
}
