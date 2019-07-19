package ish.oncourse.willow.service;

import ish.oncourse.willow.model.checkout.CodeResponse;
import ish.oncourse.willow.model.common.CommonError;
import ish.oncourse.willow.model.web.Promotion;
import ish.oncourse.willow.model.web.PromotionNotFound;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

@Path("/")
public interface PromotionApi  {

    @GET
    @Path("/v1/promotion/{code}")
    @Produces({ "application/json" })
    @CollegeInfo
    Promotion getPromotion(@PathParam("code") String code);

    @GET
    @Path("/v1/submitCode/{code}")
    @Produces({ "application/json" })
    @CollegeInfo
    CodeResponse submitCode(@PathParam("code") String code);
}

