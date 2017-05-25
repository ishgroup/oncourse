package ish.oncourse.willow.service;

import ish.oncourse.willow.model.checkout.CheckoutModel;
import ish.oncourse.willow.model.checkout.PurchaseItems;
import ish.oncourse.willow.model.checkout.payment.PaymentRequest;
import ish.oncourse.willow.model.checkout.payment.PaymentResponse;
import ish.oncourse.willow.model.checkout.request.PurchaseItemsRequest;
import ish.oncourse.willow.model.common.CommonError;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

@Path("/")
public interface CheckoutApi  {

    @POST
    @Path("/calculateAmount")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @CollegeInfo
    CheckoutModel calculateAmount(CheckoutModel checkoutModel);

    @POST
    @Path("/purchaseItems")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @CollegeInfo
    PurchaseItems getPurchaseItems(PurchaseItemsRequest purchaseItemsRequest);

    @POST
    @Path("/makePayment")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @CollegeInfo
    PaymentResponse makePayment(PaymentRequest paymentRequest);
}

