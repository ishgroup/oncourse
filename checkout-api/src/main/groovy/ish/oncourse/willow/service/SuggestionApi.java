package ish.oncourse.willow.service;

import ish.oncourse.willow.model.common.CommonError;
import ish.oncourse.willow.model.v2.suggestion.SuggestionRequest;
import ish.oncourse.willow.model.v2.suggestion.SuggestionResponse;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

@Path("/")
public interface SuggestionApi  {

    @POST
    @Path("/v1/suggestion")
    @Produces({ "application/json" })
    @CollegeInfo
    SuggestionResponse getSuggestion(SuggestionRequest suggestionRequest);
}

