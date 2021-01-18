package ish.oncourse.willow.service;

import ish.oncourse.willow.model.v2.checkout.payment.PaymentRequest;
import ish.oncourse.willow.model.v2.checkout.payment.PaymentResponse;

import javax.ws.rs.*;

@Path("/")
public interface CheckoutV2Api  {

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
    PaymentResponse makePayment(PaymentRequest paymentRequest, @HeaderParam("xValidateOnly") Boolean xValidateOnly, @HeaderParam("payerId") String payerId, @HeaderParam("xOrigin") String referer);
}

