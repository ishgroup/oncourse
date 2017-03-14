package ish.oncourse.willow.service

import ish.oncourse.willow.model.CourseClassPrice

import javax.validation.constraints.NotNull
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

class CourseClassService {
    
    
    @GET()
    @Path("/classes/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    CourseClassPrice getCourseClassPrice(@NotNull @PathParam("id") String id) {
        return null
    }
}
