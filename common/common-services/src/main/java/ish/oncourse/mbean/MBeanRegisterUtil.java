package ish.oncourse.mbean;

import org.apache.log4j.Logger;

import javax.management.*;
import java.lang.management.ManagementFactory;

public class MBeanRegisterUtil {
	private static Logger LOGGER = Logger.getLogger(MBeanRegisterUtil.class);
	
	public static void registerMbeanService(final Object mBean, final ObjectName name) {
		final MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
		try {
			LOGGER.info(String.format("Register MBean with name %s started.", name.getCanonicalName()));
			if (!isRegisteredMBean(name)) {
				mBeanServer.registerMBean(mBean, name);
				LOGGER.info(String.format("Register MBean with name %s done.", name.getCanonicalName()));
			} else {
				throw new Exception(String.format("Instance with the same name %s already registered!" , name.getCanonicalName()));
			}
		} catch (InstanceAlreadyExistsException | NotCompliantMBeanException | MBeanRegistrationException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	public static boolean isRegisteredMBean(final ObjectName name) {
		return ManagementFactory.getPlatformMBeanServer().isRegistered(name);
	}
	
	public static void unregisterMBeanService(final ObjectName name) {
		final MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
		try {
			if (isRegisteredMBean(name)) {
				mBeanServer.unregisterMBean(name);
			} else {
				throw new Exception(String.format("Instance with the same name %s already unregistered!" , name.getCanonicalName()));
			}
		} catch (MBeanRegistrationException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
}
