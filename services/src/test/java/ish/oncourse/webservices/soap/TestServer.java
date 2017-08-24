package ish.oncourse.webservices.soap;

import ish.oncourse.test.InitialContextFactoryMock;
import ish.oncourse.util.ContextUtil;
import ish.oncourse.webservices.usi.TestUSIServiceEndpoint;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class TestServer {
	public static final String DEFAULT_WEB_XML_FILE_PATH = "/web.xml";
	public static final String DEFAULT_RESOURSE_BASE = "src/test";
	public static final String DEFAULT_HOST = "127.0.0.1";
	public static final String DEFAULT_WEB_PATH = "src/test/webapp/WEB-INF";
	public static final String DEFAULT_CONTEXT_PATH = "/services";
	static final int DEFAULT_SERVER_PORT = 9091;
	private static Logger logger = LogManager.getLogger();
	private transient Server server;
	private int port;
	//private int stopPort = 9092;
	private String contextPath;
	private String webPath;
	private String host;
	private String resourseBase;
	private String webXmlFilePath;
	public static final String DEFAULT_SERVER_URL = "http://127.0.0.1:9091";

	private transient Exception exception;

	public TestServer() {
		this(DEFAULT_SERVER_PORT, DEFAULT_CONTEXT_PATH, DEFAULT_WEB_PATH, DEFAULT_HOST, DEFAULT_RESOURSE_BASE, DEFAULT_WEB_XML_FILE_PATH);
	}

	public TestServer(int port, String contextPath, String webPath, String host, String resourseBase, String webXmlFilePath) {
		System.setProperty(TestUSIServiceEndpoint.USI_TEST_MODE, "true");
		this.port = port;
		this.contextPath = contextPath;
		this.webPath = webPath;
		this.host = host;
		this.resourseBase = resourseBase;
		this.webXmlFilePath = webXmlFilePath;
	}

	public String getServerUrl() {
		return "http://" + host + ":" + port;
	}

	public TestServer start() {

		server = new Server();

		ServerConnector connector = new ServerConnector(server);
		connector.setPort(port);
		connector.setHost(host);
		ServletContextHandler handler = new ServletContextHandler();
		handler.setContextPath(contextPath);
		handler.setResourceBase(resourseBase);
		ServletHolder servletHolder = new ServletHolder(new CXFServlet());
		servletHolder.setInitParameter("config-location",
				"classpath:ish/oncourse/webservices/soap/cxf-servlet.xml");
		handler.addServlet(servletHolder, "/*");
		server.setHandler(handler);

		server.setConnectors(new Connector[]{connector});


//		WebAppContext context = new WebAppContext();
//		context.setServer(server);
//		context.setContextPath(contextPath);
		//context.setWar(warFilePath);

		//Note: Set WAR assumes all resources etc in place like genuine WAR,
		//in our case resources scattered across so use the following instead:
//		context.setResourceBase(resourseBase);
//		context.setDescriptor(webPath + webXmlFilePath);
//		server.setHandler(context);

		try {
			//this is local workaround to pass the tests with this settings
			//System.setProperty("https.proxyHost", "fish.ish.com.au");
			//System.setProperty("https.proxyPort", "8080");

			InitialContextFactoryMock.bind(ContextUtil.CACHE_ENABLED_PROPERTY_KEY, Boolean.FALSE);
			server.start();
		} catch (Exception e) {
			try {
				server.stop();
				server.destroy();
			} catch (Exception e1) {
				throw new RuntimeException(e);
			}
			throw new RuntimeException(e);
		}
		logger.info("[start] Started Server @ {}:{}", host, port);
		logger.info("[start] Server Ready & Running - {}", server.isRunning());

		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					server.join();
				} catch (InterruptedException e) {
				}
			}
		};

		Thread t = new Thread(runnable);
		t.setDaemon(true);
		t.start();

		while (!server.isStarted()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				break;
			}
		}

		return this;
	}

	public void stop() {
		try {
			if (server != null && server.isStarted()) {
				server.stop();
			}
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

	public static void main(String[] args) throws Exception {
		new TestServer().start();
	}
}
