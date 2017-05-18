package ish.oncourse.willow.service;

import java.util.List;
import ish.oncourse.willow.model.checkout.PurchaseItems;
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
    @Path("/purchaseItems")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @CollegeInfo
    PurchaseItems getPurchaseItems(String contactId, List<String> classesIds, List<String> productIds, List<String> promotionIds);
}

