package ish.oncourse.mbean;

import java.lang.management.ManagementFactory;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.apache.log4j.Logger;

public class MBeanRegisterUtil {
	private static Logger LOGGER = Logger.getLogger(MBeanRegisterUtil.class);
	
	public static void registerMbeanService(final Object mBean, final String objectName) {
		final MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
		try {
			final ObjectName name = new ObjectName(objectName);
			mBeanServer.registerMBean(mBean, name);
		} catch (InstanceAlreadyExistsException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (MBeanRegistrationException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (NotCompliantMBeanException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (MalformedObjectNameException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (NullPointerException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
}
