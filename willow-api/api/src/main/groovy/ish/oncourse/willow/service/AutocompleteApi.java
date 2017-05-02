package ish.oncourse.willow.service;

import ish.oncourse.willow.model.autocomplete.Item;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.List;

@Path("/")
public interface AutocompleteApi  {

    @GET
    @Path("/completion/{key}")
    @Produces({ "application/json" })
    List<Item> autocomplete(@PathParam("key") String key, @QueryParam("text")String text);
}

