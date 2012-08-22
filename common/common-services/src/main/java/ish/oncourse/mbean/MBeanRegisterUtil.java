package ish.oncourse.mbean;

import java.lang.management.ManagementFactory;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.apache.log4j.Logger;

public class MBeanRegisterUtil {
	private static Logger LOGGER = Logger.getLogger(MBeanRegisterUtil.class);
	
	public static void registerMbeanService(final Object mBean, final ObjectName name) {
		final MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
		try {
			mBeanServer.registerMBean(mBean, name);
		} catch (InstanceAlreadyExistsException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (MBeanRegistrationException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (NotCompliantMBeanException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (NullPointerException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	public static void unregisterMBeanService(final ObjectName name) {
		final MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
		try {
			mBeanServer.unregisterMBean(name);
		} catch (MBeanRegistrationException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (InstanceNotFoundException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
}
