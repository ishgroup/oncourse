package ish.oncourse.services.jmx;

import ish.oncourse.mbean.ApplicationData;
import ish.oncourse.mbean.MBeanRegisterUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.ioc.services.RegistryShutdownListener;
import org.apache.tapestry5.services.ApplicationGlobals;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class JMXInitService implements IJMXInitService, RegistryShutdownListener {
	private static final String ONCOURSE_DATASOURCE = "/oncourse";
	private static final String JAVA_COMP_ENV_JDBC_CONTEXT = "java:comp/env/jdbc";
	private static final Logger logger = LogManager.getLogger();
	private ObjectName applicationDataInstance;
		
	public JMXInitService(final ApplicationGlobals applicationGlobals, final String appName, final String objectName) {
		logger.info("JMX service init started.");
		DataSource datasource = null;
		try {
			datasource = getDataSource();
		} catch (NamingException e) {
			datasource = null;
			logger.error(e.getMessage(), e);
		}
		ObjectName newApplicationDataInstance = null;
		try {
			newApplicationDataInstance = new ObjectName(objectName);
		} catch (MalformedObjectNameException | NullPointerException e) {
			logger.error(e.getMessage(), e);
		}
		if (newApplicationDataInstance != null) {
			applicationDataInstance = newApplicationDataInstance;
			if (isObjectNameRegistered(newApplicationDataInstance)) {
				unregisterObjectName(newApplicationDataInstance);
			}
		}
		MBeanRegisterUtil.registerMbeanService(new ApplicationData(appName, applicationGlobals, datasource), applicationDataInstance);
		logger.info("JMX service init finished.");
	}
	
	private DataSource getDataSource() throws NamingException {
		Context context = (Context) new InitialContext().lookup(JAVA_COMP_ENV_JDBC_CONTEXT);
		DataSource dataSource = (DataSource) context.lookup(ONCOURSE_DATASOURCE);
		if (dataSource != null) {
			logger.info("Datasource available");
			return dataSource;
		}
		return null;
	}
	
	private void unregisterObjectName(final ObjectName name) {
		MBeanRegisterUtil.unregisterMBeanService(name);
	}
	
	private boolean isObjectNameRegistered(final ObjectName name) {
		return MBeanRegisterUtil.isRegisteredMBean(name);
	}

	@Override
	public void registryDidShutdown() {
		logger.info("JMX service shutdown requested.");
		logger.info("JMX service shutdown complete.");
	}
	
}
