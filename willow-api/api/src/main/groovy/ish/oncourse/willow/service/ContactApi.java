package ish.oncourse.willow.service;

import ish.oncourse.willow.model.common.CommonError;
import ish.oncourse.willow.model.common.ValidationError;
import ish.oncourse.willow.model.field.ContactFields;
import ish.oncourse.willow.model.field.ContactFieldsRequest;
import ish.oncourse.willow.model.web.Contact;
import ish.oncourse.willow.model.web.CreateContactParams;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

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

    @POST
    @Path("/contactFields")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @CollegeInfo
    ContactFields getContactFields(ContactFieldsRequest contactFieldsRequest);
}

