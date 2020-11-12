/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */
package ish.oncourse.server.jmx;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import ish.oncourse.server.AngelModule;
import ish.oncourse.server.ICayenneService;
import ish.oncourse.server.PreferenceController;
import ish.oncourse.server.api.servlet.ISessionManager;
import ish.oncourse.server.monitoring.ApplicationInfo;
import ish.oncourse.server.monitoring.ApplicationInfoMBean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

/**
 * User: akoiro
 * Date: 22/07/2016
 */
@Singleton
public class RegisterMBean {
	private static final Logger LOGGER = LogManager.getLogger(RegisterMBean.class);
	private ObjectName objectName;
	private MBeanServer mBeanServer;
	private ApplicationInfoMBean applicationInfo;

	@Inject
	public RegisterMBean(ICayenneService cayenneService, ISessionManager sessionManager, @Named(AngelModule.ANGEL_VERSION) String angelVersion, PreferenceController preferenceController) throws MalformedObjectNameException, NotCompliantMBeanException {
		objectName = new ObjectName("ish.oncourse.server.monitoring:type=ApplicationInfo");
		mBeanServer = ManagementFactory.getPlatformMBeanServer();
		applicationInfo = new ApplicationInfo(cayenneService, sessionManager, angelVersion, preferenceController);
	}

	public void register() {
		try {
			mBeanServer.registerMBean(applicationInfo, objectName);
		} catch (Exception e) {
			LOGGER.catching(e);
		}
	}

	public void unregister() {
		try {
			if (mBeanServer.isRegistered(objectName)) {
				mBeanServer.unregisterMBean(objectName);
			}
		} catch (Exception e) {
			LOGGER.catching(e);
		}
	}
}
