package ish.oncourse.willow.service;

import java.util.List;
import ish.oncourse.willow.model.checkout.PurchaseItems;
import ish.oncourse.willow.model.common.CommonError;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API tests for CheckoutApi
 */
public class CheckoutApiTest {

    private CheckoutApi api;
    
    @Before
    public void setup() {
    }

    
    /**
     * Get list of purchases
     * Get list of enrolments/applications/product items for certain contact based on selected classes/products
     */
    @Test
    public void getPurchaseItemsTest() {
        String contactId = null;
        List<String> classesIds = null;
        List<String> productIds = null;
        List<String> promotionIds = null;
	//PurchaseItems response = api.getPurchaseItems(contactId, classesIds, productIds, promotionIds);
        //assertNotNull(response);
        // TODO: test validations
        
        
    }
    
}
