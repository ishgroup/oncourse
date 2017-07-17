package ish.oncourse.willow.service;

import java.util.List;
import ish.oncourse.willow.model.checkout.CreateParentChildrenRequest;
import ish.oncourse.willow.model.checkout.concession.Concession;
import ish.oncourse.willow.model.checkout.concession.ConcessionType;
import ish.oncourse.willow.model.common.CommonError;
import ish.oncourse.willow.model.common.ValidationError;
import ish.oncourse.willow.model.field.ContactFields;
import ish.oncourse.willow.model.field.ContactFieldsRequest;
import ish.oncourse.willow.model.field.SubmitFieldsRequest;
import ish.oncourse.willow.model.web.Contact;
import ish.oncourse.willow.model.web.ContactId;
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
    ContactId createOrGetContact(CreateContactParams createContactParams);

    @PUT
    @Path("/createParentChildrenRelation")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @CollegeInfo
    void createParentChildrenRelation(CreateParentChildrenRequest contactFields);

    @GET
    @Path("/getConcessionTypes")
    @Produces({ "application/json" })
    @CollegeInfo
    List<ConcessionType> getConcessionTypes();

    @GET
    @Path("/contact/{studentUniqueIdentifier}")
    @Produces({ "application/json" })
    @CollegeInfo
    Contact getContact(@PathParam("studentUniqueIdentifier") String studentUniqueIdentifier);

    @POST
    @Path("/getContactConcessions")
    @Produces({ "application/json" })
    @CollegeInfo
    List<Concession> getContactConcessions(List<String> contactIds);

    @POST
    @Path("/contactFields")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @CollegeInfo
    ContactFields getContactFields(ContactFieldsRequest contactFieldsRequest);

    @PUT
    @Path("/submitConcession")
    @Produces({ "application/json" })
    @CollegeInfo
    void submitConcession(Concession concession);

    @POST
    @Path("/submitContactDetails")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @CollegeInfo
    ContactId submitContactDetails(SubmitFieldsRequest contactFields);
}

