package ish.oncourse.willow.service;

import ish.oncourse.willow.model.checkout.CheckoutModelRequest;
import ish.oncourse.willow.model.checkout.corporatepass.CorporatePass;
import ish.oncourse.willow.model.checkout.corporatepass.GetCorporatePassRequest;
import ish.oncourse.willow.model.checkout.corporatepass.MakeCorporatePassRequest;
import ish.oncourse.willow.model.common.CommonError;
import ish.oncourse.willow.model.common.ValidationError;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

@Path("/")
public interface CorporatePassApi  {

    @POST
    @Path("/getCorporatePass")
    @Produces({ "application/json" })
    @CollegeInfo
    CorporatePass getCorporatePass(GetCorporatePassRequest request);

    @GET
    @Path("/isCorporatePassEnabled")
    @Produces({ "application/json" })
    @CollegeInfo
    Boolean isCorporatePassEnabled();

    @POST
    @Path("/isCorporatePassEnabledFor")
    @Produces({ "application/json" })
    @CollegeInfo
    Boolean isCorporatePassEnabledFor(CheckoutModelRequest checkoutModelRequest);

    @POST
    @Path("/makeCorporatePass")
    @Produces({ "application/json" })
    @CollegeInfo
    void makeCorporatePass(MakeCorporatePassRequest request);
}

