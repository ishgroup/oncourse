package ish.oncourse.willow.editor.v1.service;

import ish.oncourse.willow.editor.v1.model.Block;

import java.util.List;
import javax.ws.rs.*;

import ish.oncourse.willow.editor.annotation.AuthFilter;

@Path("/")
public interface BlockApi  {

    @POST
    @Path("/v1/block.create")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    Block blockCreatePost();

    @POST
    @Path("/v1/block.delete/{id}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    void blockDeleteIdPost(@PathParam("id") String id);

    @GET
    @Path("/v1/block.list")
    @Produces({ "application/json" })
    @AuthFilter
    List<Block> blockListGet();

    @POST
    @Path("/v1/block.update")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    Block blockUpdatePost(Block saveBlockRequest);
}

