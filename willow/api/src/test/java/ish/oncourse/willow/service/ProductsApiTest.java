package ish.oncourse.willow.service;

import ish.oncourse.willow.model.Error;
import ish.oncourse.willow.model.Product;
import ish.oncourse.willow.model.ProductsParams;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API tests for ProductsApi
 */
public class ProductsApiTest {

    private ProductsApi api;
    
    @Before
    public void setup() {
    }

    
    /**
     * Get list of products
     * Get list of products based on current shopping cart state
     */
    @Test
    public void getProductsTest() {
        ProductsParams productsParams = null;
	//List<Product> response = api.getProducts(productsParams);
        //assertNotNull(response);
        // TODO: test validations
        
        
    }
    
}
