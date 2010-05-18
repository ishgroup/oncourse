package ish.jetty;

import java.io.File;
import java.net.MalformedURLException;

import org.eclipse.jetty.server.Server;

public class JettyLauncher {

	public static void main(String[] args) throws Exception {

		JettyBuilder jettyBuilder = new JettyBuilder();
		jettyBuilder.setDataSourceFactory(LaunchProperties.ISH_DS_FACTORY
				.getValue());
		jettyBuilder.setPort(LaunchProperties.ISH_WEB_PORT.getValue());
		jettyBuilder.setContext(LaunchProperties.ISH_WEB_CONTEXT.getValue());
		jettyBuilder.setDataSourceName(LaunchProperties.ISH_DS_NAME.getValue());
		jettyBuilder.setWar(getWar());

		Server server = jettyBuilder.toServer();
		server.start();
	}

	private static String getWar() {

		String userDir = System.getProperty("user.dir");

		// following maven project structure...
		File root = new File(userDir);
		File src = new File(root, "src");
		File main = new File(src, "main");
		File webapp = new File(main, "webapp");

		try {
			return webapp.toURL().toExternalForm();
		} catch (MalformedURLException e) {
			throw new RuntimeException("Error building project URL", e);
		}
	}
}
