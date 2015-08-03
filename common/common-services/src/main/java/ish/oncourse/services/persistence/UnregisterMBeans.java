package ish.oncourse.services.persistence;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.hibernate.management.impl.EhcacheHibernateMbeanNames;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.util.Set;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class UnregisterMBeans {

    private static final Logger logger = LogManager.getLogger();

    private CacheManager cacheManager;
    private MBeanServer mBeanServer;

    public void unregister() {
        Set<ObjectName> registeredObjectNames = null;

        try {
            registeredObjectNames = mBeanServer.queryNames(createObjectName(), null);
            registeredObjectNames.addAll(mBeanServer.queryNames(new ObjectName("net.sf.ehcache:*,CacheManager="
                    + EhcacheHibernateMbeanNames.mbeanSafe(cacheManager.toString())), null));
        } catch (Exception e) {
            // this should not happen
            logger.error("Error querying MBeanServer. Error was {}", e.getMessage(), e);
        }

        if (registeredObjectNames != null) {
            for (ObjectName objectName : registeredObjectNames) {
                try {
                    mBeanServer.unregisterMBean(objectName);
                } catch (Exception e) {
                    logger.error("Error unregistering object instance {}. Error was {}", objectName, e.getMessage(), e);
                }
            }
        }
    }

    private ObjectName createObjectName() {
        ObjectName objectName;
        try {
            objectName = new ObjectName("net.sf.ehcache:type=CacheManager,name="
                    + EhcacheHibernateMbeanNames.mbeanSafe(cacheManager.getName()));
        } catch (MalformedObjectNameException e) {
            throw new CacheException(e);
        }
        return objectName;
    }

    public static UnregisterMBeans valueOf(CacheManager cacheManager, MBeanServer mBeanServer) {
        UnregisterMBeans result = new UnregisterMBeans();
        result.cacheManager = cacheManager;
        result.mBeanServer = mBeanServer;
        return result;
    }
}
