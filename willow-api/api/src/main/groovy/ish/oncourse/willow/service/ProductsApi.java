package ish.oncourse.willow.service;

import ish.oncourse.willow.model.web.Product;
import ish.oncourse.willow.model.web.ProductsParams;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.List;

@Path("/")
public interface ProductsApi  {

    @POST
    @Path("/products")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @CollegeInfo
    List<Product> getProducts(ProductsParams productsParams);
}

