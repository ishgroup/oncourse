package ish.oncourse.willow.editor.service;

import ish.oncourse.willow.editor.model.User;
import ish.oncourse.willow.editor.model.api.LoginRequest;
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
    @Path("/getUser")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    User getUser();

    @POST
    @Path("/login")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    void login(LoginRequest loginRequest);

    @POST
    @Path("/logout")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    void logout();
}

