/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.admin.services;

import ish.oncourse.mbean.MBeanRegisterUtil;
import ish.oncourse.services.jmx.IJMXInitService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.http.services.ApplicationGlobals;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.sql.DataSource;

/**
 * User: akoiro
 * Date: 23/8/17
 */
public class JMXInitService implements IJMXInitService, Runnable {
    private static final Logger logger = LogManager.getLogger();

    private ObjectName applicationDataInstance;

    public JMXInitService(ApplicationGlobals applicationGlobals,
                          DataSource dataSource, String name) {
        logger.info("JMX service init started.");
        ObjectName newApplicationDataInstance = null;
        try {
            newApplicationDataInstance = new ObjectName("ish.oncourse:type=" + name + "ApplicationData");
        } catch (MalformedObjectNameException | NullPointerException e) {
            logger.error(e.getMessage(), e);
        }
        if (newApplicationDataInstance != null) {
            applicationDataInstance = newApplicationDataInstance;
            if (isObjectNameRegistered(newApplicationDataInstance)) {
                unregisterObjectName(newApplicationDataInstance);
            }
        }
        MBeanRegisterUtil.registerMbeanService(new ApplicationData(name, applicationGlobals, dataSource), applicationDataInstance);
        logger.info("JMX service init finished.");
    }

    private void unregisterObjectName(final ObjectName name) {
        MBeanRegisterUtil.unregisterMBeanService(name);
    }

    private boolean isObjectNameRegistered(final ObjectName name) {
        return MBeanRegisterUtil.isRegisteredMBean(name);
    }

    @Override
    public void run() {
        logger.info("JMX service shutdown requested.");
        logger.info("JMX service shutdown complete.");
    }

}
