package ish.oncourse.willow.service;

import ish.oncourse.willow.model.common.CommonError;
import ish.oncourse.willow.model.web.Product;
import ish.oncourse.willow.model.web.ProductsParams;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

@Path("/")
public interface ProductsApi  {

    @POST
    @Path("/products")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @CollegeInfo
    List<Product> getProducts(ProductsParams productsParams);
}

