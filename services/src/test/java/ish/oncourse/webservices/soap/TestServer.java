package ish.oncourse.webservices.soap;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

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

				Thread monitor = new MonitorThread(host, stopPort, server);
				monitor.start();

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
		Socket s = new Socket(InetAddress.getByName(host), stopPort);
		OutputStream out = s.getOutputStream();
		LOGGER.info("[stop] sending jetty stop request @ " + host + ":" + stopPort );
		out.write(("\r\n").getBytes());
		out.flush();
		s.close();

		if (server!=null && server.isStarted()) {
			server.stop();
		}
	}

	private static final class MonitorThread extends Thread {

		private ServerSocket socket;
		private Server server;
		private int stopPort;
		private String host;

		public MonitorThread(String host, int stopPort, Server server) {
			this.server = server;
			this.stopPort = stopPort;
			this.host = host;

			setDaemon(true);
			setName("StopMonitor");
			try {
				socket = new ServerSocket(stopPort, 1, InetAddress.getByName(host));
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public void run() {
			LOGGER.info("[run] Running Jetty Stop Thread");
			Socket accept;
			try {
				accept = socket.accept();
				BufferedReader reader = new BufferedReader(new InputStreamReader(accept.getInputStream()));
				reader.readLine();
				LOGGER.info("[run] Stopping embedded Jetty Server");
				server.stop();
				accept.close();
				socket.close();
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
	}


	public static void main(String[] args) throws Exception {

		TestServer server = new TestServer();
		server.start();
	}
}
