package ish.oncourse.willow.service

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces

@Path("/ISHHealthCheck")
interface HealthCheckApi {
    
    @GET
    @Path("/")
    @Produces(['text/plain'])
    String healthCheck()
}