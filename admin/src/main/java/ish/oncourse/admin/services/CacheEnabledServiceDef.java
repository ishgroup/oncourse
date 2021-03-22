/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.admin.services;

import ish.oncourse.cayenne.cache.ICacheEnabledService;
import org.apache.tapestry5.commons.ObjectCreator;
import org.apache.tapestry5.ioc.ScopeConstants;
import org.apache.tapestry5.ioc.ServiceBuilderResources;
import org.apache.tapestry5.ioc.def.ServiceDef;

import java.util.Collections;
import java.util.Set;

public class CacheEnabledServiceDef implements ServiceDef {


    public static final String ID = "CacheEnabledService";

    private ICacheEnabledService enabledService;

    public CacheEnabledServiceDef(ICacheEnabledService enabledService) {
        this.enabledService = enabledService;
    }

    @Override
    public ObjectCreator createServiceCreator(ServiceBuilderResources resources) {
        return () -> enabledService;
    }

    @Override
    public String getServiceId() {
        return ID;
    }

    @Override
    public Set<Class> getMarkers() {
        return Collections.emptySet();
    }

    @Override
    public Class getServiceInterface() {
        return ICacheEnabledService.class;
    }

    @Override
    public String getServiceScope() {
        return ScopeConstants.DEFAULT;
    }

    @Override
    public boolean isEagerLoad() {
        return false;
    }
}
