package ish.oncourse.willow.editor.service;

import ish.oncourse.willow.editor.model.LoginRequest;
import ish.oncourse.willow.editor.model.User;
import ish.oncourse.willow.editor.model.common.CommonError;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

@Path("/")
public interface AuthApi  {

    @GET
    @Path("/user.get")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    User userGetGet();

    @POST
    @Path("/user.login")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    void userLoginPost(LoginRequest loginRequest);

    @POST
    @Path("/user.logout")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    void userLogoutPost();
}

