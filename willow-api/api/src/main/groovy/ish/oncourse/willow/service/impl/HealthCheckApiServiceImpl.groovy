package ish.oncourse.willow.service.impl

import ish.oncourse.willow.Configuration
import ish.oncourse.willow.service.HealthCheckApi
import org.apache.cxf.interceptor.OutInterceptors

@OutInterceptors(classes = [HealthCheckInterceptor.class])
class HealthCheckApiServiceImpl implements HealthCheckApi {
    
    @Override
    String healthCheck() {
        System.getProperty(Configuration.API_VERSION)
    }
}
