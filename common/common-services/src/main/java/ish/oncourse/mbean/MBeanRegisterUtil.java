package ish.oncourse.mbean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.management.*;
import java.lang.management.ManagementFactory;

public class MBeanRegisterUtil {
	private static Logger logger = LogManager.getLogger();
	
	public static void registerMbeanService(final Object mBean, final ObjectName name) {
		final MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
		try {
			logger.info("Register MBean with name {} started.", name.getCanonicalName());
			if (!isRegisteredMBean(name)) {
				mBeanServer.registerMBean(mBean, name);
				logger.info("Register MBean with name {} done.", name.getCanonicalName());
			} else {
				throw new Exception(String.format("Instance with the same name %s already registered!" , name.getCanonicalName()));
			}
		} catch (InstanceAlreadyExistsException | NotCompliantMBeanException | MBeanRegistrationException e) {
			logger.error(e.getMessage(), e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
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
			logger.error(e.getMessage(), e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
}
