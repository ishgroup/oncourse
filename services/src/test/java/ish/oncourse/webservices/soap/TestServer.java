package ish.oncourse.webservices.soap;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.webapp.WebAppContext;

public class TestServer {
    private static Logger LOGGER = Logger.getLogger(TestServer.class);
    private transient Server server;
    private int port = 9091;
    private int stopPort = 9092;
    private String contextPath = "/services";
    private String webPath = "src/test/webapp/WEB-INF";
    private String host = "127.0.0.1";

    public static final String SERVER_URL = "http://127.0.0.1:9091";

    private transient Exception exception;


    public void start() throws Exception {
        server = new Server();
        SocketConnector connector = new SocketConnector();
        connector.setPort(port);
        server.setConnectors(new Connector[]{connector});

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
        } catch (Exception e) {
            server.stop();
            server.destroy();
            throw new RuntimeException(e);
        }
        LOGGER.info("[start] Started Server @ " + host + ":" + port);
        LOGGER.info("[start] Server Ready & Running - " + server.isRunning());

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

        while (!server.isStarted())
        {
           Thread.sleep(1000);
        }
    }

    public void stop() throws Exception {
        if (server != null && server.isStarted()) {
            server.stop();
        }
    }

    public static void main(String[] args) throws Exception {

        TestServer server = new TestServer();
        server.start();
    }
}
