package ish.oncourse.willow.service

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces

@Path("/")
interface HealthCheckApi {
    
    @GET
    @Path("/ISHHealthCheck")
    @Produces(['text/plain'])
    String healthCheck()
}