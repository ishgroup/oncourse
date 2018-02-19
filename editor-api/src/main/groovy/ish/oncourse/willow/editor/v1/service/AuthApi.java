package ish.oncourse.willow.editor.v1.service;

import ish.oncourse.willow.editor.v1.model.LoginRequest;
import ish.oncourse.willow.editor.v1.model.User;

import javax.ws.rs.*;

import ish.oncourse.willow.editor.annotation.AuthFilter;

@Path("/")
public interface AuthApi  {

    @GET
    @Path("/v1/user.get")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    User userGetGet();

    @POST
    @Path("/v1/user.login")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    void userLoginPost(LoginRequest loginRequest);

    @POST
    @Path("/v1/user.logout")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    void userLogoutPost();
}

