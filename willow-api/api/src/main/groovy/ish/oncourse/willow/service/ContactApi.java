package ish.oncourse.willow.service;

import ish.oncourse.willow.model.web.Contact;
import ish.oncourse.willow.model.web.CreateContactParams;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("/")
public interface ContactApi  {

    @PUT
    @Path("/contact")
    @Produces({ "application/json" })
    @CollegeInfo
    @ContactValidation
    String createOrGetContact(CreateContactParams createContactParams);

    @GET
    @Path("/contact/{studentUniqueIdentifier}")
    @Produces({ "application/json" })
    @CollegeInfo
    Contact getContact(@PathParam("studentUniqueIdentifier") String studentUniqueIdentifier);
}

