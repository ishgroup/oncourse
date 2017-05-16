package ish.oncourse.willow.service;

import ish.oncourse.willow.model.common.CommonError;
import ish.oncourse.willow.model.common.Item;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

@Path("/")
public interface AutocompleteApi  {

    @GET
    @Path("/completion/{key}")
    @Produces({ "application/json" })
    List<Item> autocomplete(@PathParam("key") String key, @QueryParam("text")String text);
}

