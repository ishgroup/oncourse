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
public interface SearchApi  {

    @GET
    @Path("/country/{text}")
    @Produces({ "application/json" })
    List<Item> getCountries(@PathParam("text") String text);

    @GET
    @Path("/language/{text}")
    @Produces({ "application/json" })
    List<Item> getLanguages(@PathParam("text") String text);

    @GET
    @Path("/postcode/{text}")
    @Produces({ "application/json" })
    List<Item> getPostcodes(@PathParam("text") String text);

    @GET
    @Path("/suburb/{text}")
    @Produces({ "application/json" })
    List<Item> getSuburbs(@PathParam("text") String text);
}

