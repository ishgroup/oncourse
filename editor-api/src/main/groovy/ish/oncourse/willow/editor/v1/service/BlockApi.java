package ish.oncourse.willow.editor.v1.service;

import ish.oncourse.willow.editor.v1.model.Block;
import ish.oncourse.willow.editor.v1.model.CommonError;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;
import ish.oncourse.willow.editor.annotation.AuthFilter;

@Path("/")
public interface BlockApi  {

    @POST
    @Path("/v1/block")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    Block createBlock();

    @DELETE
    @Path("/v1/block/{id}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    void deleteBlock(@PathParam("id") String id);

    @GET
    @Path("/v1/block")
    @Produces({ "application/json" })
    @AuthFilter
    List<Block> getBlocks();

    @PUT
    @Path("/v1/block")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    Block updateBlock(Block saveBlockRequest);
}

