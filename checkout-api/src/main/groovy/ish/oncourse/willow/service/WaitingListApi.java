package ish.oncourse.willow.service;

import ish.oncourse.willow.model.checkout.waitinglist.WaitingListRequest;
import ish.oncourse.willow.model.common.CommonError;
import ish.oncourse.willow.model.common.ValidationError;
import ish.oncourse.willow.model.field.FieldHeading;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

@Path("/")
public interface WaitingListApi  {

    @POST
    @Path("/submitWaitingList")
    @Produces({ "application/json" })
    @CollegeInfo
    void submitWaitingList(WaitingListRequest request);

    @POST
    @Path("/waitingListFields/{classId}")
    @Produces({ "application/json" })
    @CollegeInfo
    List<FieldHeading> waitingListFields(@PathParam("classId") String classId);
}

