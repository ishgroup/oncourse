package ish.oncourse.services.jmx;

import java.sql.SQLException;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import ish.oncourse.mbean.ApplicationData;
import ish.oncourse.mbean.MBeanRegisterUtil;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.services.RegistryShutdownListener;
import org.apache.tapestry5.services.ApplicationGlobals;

public class JMXInitService implements IJMXInitService, RegistryShutdownListener {
	private static final String ONCOURSE_DATASOURCE = "/oncourse";
	private static final String JAVA_COMP_ENV_JDBC_CONTEXT = "java:comp/env/jdbc";
	private static final Logger LOGGER = Logger.getLogger(JMXInitService.class);
	private ObjectName applicationDataInstance;
		
	public JMXInitService(final ApplicationGlobals applicationGlobals, final String appName, final String objectName) {
		DataSource datasource = null;
		try {
			datasource = getDataSource();
		} catch (NamingException e) {
			datasource = null;
			LOGGER.error(e.getMessage(), e);
		} catch (SQLException e) {
			datasource = null;
			LOGGER.error(e.getMessage(), e);
		}
		ObjectName newApplicationDataInstance = null;
		try {
			newApplicationDataInstance = new ObjectName(objectName);
		} catch (MalformedObjectNameException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (NullPointerException e) {
			LOGGER.error(e.getMessage(), e);
		}
		if (applicationDataInstance == null) {
			applicationDataInstance = newApplicationDataInstance;
		} else if (applicationDataInstance != null && newApplicationDataInstance != null) {
			registryDidShutdown();
			applicationDataInstance = newApplicationDataInstance;
		}
		MBeanRegisterUtil.registerMbeanService(new ApplicationData(appName, applicationGlobals, datasource), applicationDataInstance);
	}
	
	private DataSource getDataSource() throws NamingException, SQLException {
		Context context = (Context) new InitialContext().lookup(JAVA_COMP_ENV_JDBC_CONTEXT);
		DataSource dataSource = (DataSource) context.lookup(ONCOURSE_DATASOURCE);
		if (dataSource != null && !dataSource.getConnection().isClosed()) {
			LOGGER.info("Datasource available");
			return dataSource;
		}
		return null;
	}

	@Override
	public void registryDidShutdown() {
		LOGGER.info("Shutdown requested");
		if (applicationDataInstance != null) {
			LOGGER.info("unregister" + applicationDataInstance.getCanonicalName());
			MBeanRegisterUtil.unregisterMBeanService(applicationDataInstance);
			LOGGER.info("unregister" + applicationDataInstance.getCanonicalName() + "complete");
		}
	}
	
}
