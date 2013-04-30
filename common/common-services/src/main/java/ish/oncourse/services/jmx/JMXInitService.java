package ish.oncourse.services.jmx;

import ish.oncourse.mbean.ApplicationData;
import ish.oncourse.mbean.MBeanRegisterUtil;
import org.apache.log4j.Logger;
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
	private static final Logger LOGGER = Logger.getLogger(JMXInitService.class);
	private ObjectName applicationDataInstance;
		
	public JMXInitService(final ApplicationGlobals applicationGlobals, final String appName, final String objectName) {
		LOGGER.info("JMX service init started.");
		DataSource datasource = null;
		try {
			datasource = getDataSource();
		} catch (NamingException e) {
			datasource = null;
			LOGGER.error(e.getMessage(), e);
		}
		ObjectName newApplicationDataInstance = null;
		try {
			newApplicationDataInstance = new ObjectName(objectName);
		} catch (MalformedObjectNameException | NullPointerException e) {
			LOGGER.error(e.getMessage(), e);
		}
		if (newApplicationDataInstance != null) {
			applicationDataInstance = newApplicationDataInstance;
			if (isObjectNameRegistered(newApplicationDataInstance)) {
				unregisterObjectName(newApplicationDataInstance);
			}
		}
		MBeanRegisterUtil.registerMbeanService(new ApplicationData(appName, applicationGlobals, datasource), applicationDataInstance);
		LOGGER.info("JMX service init finished.");
	}
	
	private DataSource getDataSource() throws NamingException {
		Context context = (Context) new InitialContext().lookup(JAVA_COMP_ENV_JDBC_CONTEXT);
		DataSource dataSource = (DataSource) context.lookup(ONCOURSE_DATASOURCE);
		if (dataSource != null) {
			LOGGER.info("Datasource available");
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
		LOGGER.info("JMX service shutdown requested.");
		//comment mbean unregister because on init we unregister old instance
		/*if (applicationDataInstance != null && isObjectNameRegistered(applicationDataInstance)) {
			LOGGER.info("Unregister " + applicationDataInstance.getCanonicalName());
			unregisterObjectName(applicationDataInstance);
			LOGGER.info("Unregister " + applicationDataInstance.getCanonicalName() + " complete.");
		}*/
		LOGGER.info("JMX service shutdown complete.");
	}
	
}
