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
    @Path("/addBlock")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    Block addBlock();

    @POST
    @Path("/deleteBlock/{name}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    void deleteBlock(@PathParam("name") String name);

    @GET
    @Path("/getBlocks")
    @Produces({ "application/json" })
    @AuthFilter
    List<Block> getBlocks();

    @POST
    @Path("/saveBlock")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @AuthFilter
    Block saveBlock(Block saveBlockRequest);
}

