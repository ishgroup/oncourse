package ish.oncourse.willow.editor.service;

import ish.oncourse.willow.editor.model.Block;
import ish.oncourse.willow.editor.model.common.CommonError;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

@Path("/")
public interface BlockApi  {

    @POST
    @Path("/block.create")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    Block blockCreatePost();

    @POST
    @Path("/block.delete/{id}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    void blockDeleteIdPost(@PathParam("id") String id);

    @GET
    @Path("/block.list")
    @Produces({ "application/json" })
    @AuthFilter
    List<Block> blockListGet();

    @POST
    @Path("/block.update")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    Block blockUpdatePost(Block saveBlockRequest);
}

