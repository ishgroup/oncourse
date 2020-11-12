package ish.oncourse.server.security.api.test

import ish.oncourse.server.api.security.Permission
import ish.oncourse.server.api.v1.model.TagDTO

import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces

@Path("/")
interface TestApi {

    @POST
    @Path("/v1/test")
    @Consumes(["application/json"])
    @Produces([ "application/json" ])
    @Permission(mask = "create", keyCode = "TAG", errorMessage = "Can't execute CREATE method")
    void create();
    @GET
    @Path("/v1/test")
    @Consumes([ "application/json" ])
    @Produces([ "application/json" ])
    @Permission(mask = "view", keyCode = "TAG", errorMessage = "Can't execute GET method")
    List<TagDTO> get();
    @GET
    @Path("/v1/test/list")
    @Consumes([ "application/json" ])
    @Produces([ "application/json" ])
    @Permission(mask = "view", keyCode = "LAZY/entity", errorMessage = "Can't execute list GET method")
    List<TagDTO> getList();
}
