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
public interface AuthorizationApi  {

    @POST
    @Path("/getUser")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    User getUser(LoginRequest loginRequest);

    @POST
    @Path("/logout")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    void logout();
}

