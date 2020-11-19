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
    @Path("/v1/getCorporatePass")
    @Produces({ "application/json" })
    @CollegeInfo
    CorporatePass getCorporatePass(GetCorporatePassRequest request);

    @GET
    @Path("/v1/isCorporatePassEnabled")
    @Produces({ "application/json" })
    @CollegeInfo
    Boolean isCorporatePassEnabled();

    @POST
    @Path("/v1/isCorporatePassEnabledFor")
    @Produces({ "application/json" })
    @CollegeInfo
    Boolean isCorporatePassEnabledFor(CheckoutModelRequest checkoutModelRequest);

    @POST
    @Path("/v1/makeCorporatePass")
    @Produces({ "application/json" })
    @XValidate
    @CollegeInfo
    void makeCorporatePass(MakeCorporatePassRequest request);
}

