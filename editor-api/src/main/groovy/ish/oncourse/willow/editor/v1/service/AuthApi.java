package ish.oncourse.willow.editor.v1.service;

import ish.oncourse.willow.editor.v1.model.CommonError;
import ish.oncourse.willow.editor.v1.model.LoginRequest;
import ish.oncourse.willow.editor.v1.model.User;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;
import ish.oncourse.willow.editor.annotation.AuthFilter;

@Path("/")
public interface AuthApi  {

    @POST
    @Path("/v1/session")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    void createSession(LoginRequest loginRequest);

    @DELETE
    @Path("/v1/session")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    void destroySession();

    @GET
    @Path("/v1/user")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    User getUser();
}

