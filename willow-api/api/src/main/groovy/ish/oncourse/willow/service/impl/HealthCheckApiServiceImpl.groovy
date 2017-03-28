package ish.oncourse.willow.service.impl

import com.google.inject.Inject
import ish.oncourse.willow.Configuration
import ish.oncourse.willow.service.HealthCheckApi

import javax.ws.rs.BadRequestException

class HealthCheckApiServiceImpl implements HealthCheckApi {

    
    ShotDownService downService

    @Inject 
    HealthCheckApiServiceImpl(ShotDownService downService) {
        this.downService = downService
    }
    
    @Override
    String healthCheck() {
        if (downService.killSignalReceived) {
            throw new BadRequestException()
        }
        System.getProperty(Configuration.API_VERSION)
    }
}
