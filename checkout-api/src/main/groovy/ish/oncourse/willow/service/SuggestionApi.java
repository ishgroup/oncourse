package ish.oncourse.willow.service;

import ish.oncourse.willow.model.v2.suggestion.SuggestionRequest;
import ish.oncourse.willow.model.v2.suggestion.SuggestionResponse;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/")
public interface SuggestionApi  {

    @POST
    @Path("/v1/suggestion")
    @Produces({ "application/json" })
    @CollegeInfo
    SuggestionResponse getSuggestion(SuggestionRequest suggestionRequest);
}

