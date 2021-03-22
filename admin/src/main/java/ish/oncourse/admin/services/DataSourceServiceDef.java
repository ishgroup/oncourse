/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.admin.services;

import org.apache.tapestry5.commons.ObjectCreator;
import org.apache.tapestry5.ioc.ScopeConstants;
import org.apache.tapestry5.ioc.ServiceBuilderResources;
import org.apache.tapestry5.ioc.def.ServiceDef;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.Set;

/**
 * User: akoiro
 * Date: 1/12/17
 */
public class DataSourceServiceDef implements ServiceDef {

    public static final String ID = "WillowDataSource";

    private DataSource dataSource;

    public DataSourceServiceDef(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public ObjectCreator createServiceCreator(ServiceBuilderResources resources) {
        return () -> dataSource;
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
        return DataSource.class;
    }

    @Override
    public String getServiceScope() {
        return ScopeConstants.DEFAULT;
    }

    @Override
    public boolean isEagerLoad() {
        return true;
    }
}
