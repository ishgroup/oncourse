package ish.oncourse.willow.service;

import ish.oncourse.willow.model.Contact;
import ish.oncourse.willow.model.Error;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

@Path("/contact")
public interface ContactApi  {

    @GET
    @Path("{studentUniqueIdentifier}")
    @Produces({ "application/json" })
    Contact getContact(@PathParam("studentUniqueIdentifier") String studentUniqueIdentifier);
}

