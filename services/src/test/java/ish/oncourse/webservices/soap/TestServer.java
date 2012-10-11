package ish.oncourse.webservices.soap;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.webapp.WebAppContext;

public class TestServer {
	private static Logger LOGGER = Logger.getLogger(TestServer.class);
	private Server server;
	private int port = 9091;
	private int stopPort = 9092;
	private String contextPath = "/services";
	private String webPath = "src/test/webapp/WEB-INF";
	private String host = "127.0.0.1";

	public static final String SERVER_URL = "http://127.0.0.1:9091";


	public void start() throws Exception {
		Thread t = new Thread() {
			public void run() {
				server = new Server();
				SocketConnector connector = new SocketConnector();
				connector.setPort(port);
				server.setConnectors(new Connector[] { connector });

				WebAppContext context = new WebAppContext();
				context.setServer(server);
				context.setContextPath(contextPath);
				//context.setWar(warFilePath);

				//Note: Set WAR assumes all resources etc in place like genuine WAR,
				//in our case resources scattered across so use the following instead:
				context.setResourceBase("src/test");
				context.setDescriptor(webPath + "/web.xml");
				server.setHandler(context);

				try {
					server.start();
					server.join();
				}
				catch(Exception e) {
					throw new RuntimeException(e);
				}
			}
		};

		t.setDaemon(true);
		t.start();

		while(server==null || !server.isStarted()) {
			Thread.sleep(1000);
		}

		LOGGER.info("[start] Started Server @ " + host + ":" + port );
		LOGGER.info("[start] Server Ready & Running - " + server.isRunning());
	}

	public void stop() throws Exception {
		if (server!=null && server.isStarted()) {
			server.stop();
		}
	}

	public static void main(String[] args) throws Exception {

		TestServer server = new TestServer();
		server.start();
	}
}
