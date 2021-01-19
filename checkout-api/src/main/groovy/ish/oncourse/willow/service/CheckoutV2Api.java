package ish.oncourse.willow.service;

import ish.oncourse.willow.model.checkout.ContactNode;
import ish.oncourse.willow.model.checkout.request.ContactNodeRequest;
import ish.oncourse.willow.model.v2.checkout.payment.PaymentRequest;
import ish.oncourse.willow.model.v2.checkout.payment.PaymentResponse;

import javax.ws.rs.*;

@Path("/")
public interface CheckoutV2Api  {

    @POST
    @Path("/v2/getContactNode")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @CollegeInfo
    ContactNode getContactNodeV2(ContactNodeRequest contactNodeRequest);

    @GET
    @Path("/v2/getPaymentStatus/{sessionId}")
    @Produces({ "application/json" })
    @CollegeInfo
    PaymentResponse getStatus(@PathParam("sessionId") String sessionId, @HeaderParam("payerId") String payerId);

    @POST
    @Path("/v2/makePayment")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @CollegeInfo
    @XValidate
    PaymentResponse makePayment(PaymentRequest paymentRequest, @HeaderParam("xValidateOnly") Boolean xValidateOnly, @HeaderParam("payerId") String payerId, @HeaderParam("xOrigin") String xOrigin);
}

