package ish.oncourse.willow.service;

import java.util.List;
import ish.oncourse.willow.model.checkout.ConcessionsAndMemberships;
import ish.oncourse.willow.model.checkout.CreateParentChildrenRequest;
import ish.oncourse.willow.model.checkout.concession.Concession;
import ish.oncourse.willow.model.checkout.concession.ConcessionType;
import ish.oncourse.willow.model.field.ContactFields;
import ish.oncourse.willow.model.field.ContactFieldsRequest;
import ish.oncourse.willow.model.web.Contact;
import ish.oncourse.willow.model.web.ContactId;
import ish.oncourse.willow.model.web.CreateContactParams;

import javax.ws.rs.*;

@Path("/")
public interface ContactApi  {

    @PUT
    @Path("/v1/contact")
    @Produces({ "application/json" })
    @XValidate
    @CollegeInfo
    @ContactValidation
    ContactId createOrGetContact(CreateContactParams createContactParams);

    @PUT
    @Path("/v1/createParentChildrenRelation")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @XValidate
    @CollegeInfo
    void createParentChildrenRelation(CreateParentChildrenRequest request);

    @GET
    @Path("/v1/getConcessionTypes")
    @Produces({ "application/json" })
    @CollegeInfo
    List<ConcessionType> getConcessionTypes();

    @GET
    @Path("/v1/contact/{studentUniqueIdentifier}")
    @Produces({ "application/json" })
    @CollegeInfo
    Contact getContact(@PathParam("studentUniqueIdentifier") String studentUniqueIdentifier);

    @POST
    @Path("/v1/getContactConcessionsAndMemberships")
    @Produces({ "application/json" })
    @CollegeInfo
    ConcessionsAndMemberships getContactConcessionsAndMemberships(List<String> contactIds);

    @POST
    @Path("/v1/contactFields")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @CollegeInfo
    ContactFields getContactFields(ContactFieldsRequest contactFieldsRequest);

    @PUT
    @Path("/v1/submitConcession")
    @Produces({ "application/json" })
    @XValidate
    @CollegeInfo
    void submitConcession(Concession concession);

    @POST
    @Path("/v1/submitContactDetails")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @XValidate
    @CollegeInfo
    ContactId submitContactDetails(ContactFields contactFields);
}

