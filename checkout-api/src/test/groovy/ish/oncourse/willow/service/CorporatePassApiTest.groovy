package ish.oncourse.willow.service;

import ish.oncourse.willow.model.checkout.corporatepass.GetCorporatePassRequest;
import ish.oncourse.willow.model.checkout.corporatepass.MakeCorporatePassRequest;
import org.junit.Test
import org.junit.Before

/**
 * API tests for CorporatePassApi
 */
class CorporatePassApiTest {

    private CorporatePassApi api;
    
    @Before
    void setup() {
    }

    
    /**
     * Check if Corporate Pass avalible
     * Check if Corporate Pass by provided code exist and avalible for choosen Classes/Products
     */
    @Test
    void getCorporatePassTest() {
        GetCorporatePassRequest request = null;
	//CorporatePass response = api.getCorporatePass(request);
        //assertNotNull(response);
        // TODO: test validations
        
        
    }
    
    /**
     * Check if Corporate Pass process enabled for college
     * Check if Corporate Pass process enabled for college
     */
    @Test
    void isCorporatePassEnabledTest() {
	//Boolean response = api.isCorporatePassEnabled();
        //assertNotNull(response);
        // TODO: test validations
        
        
    }
    
    /**
     * Make corporate pass process
     * Validate model, save purchase items into db, tine invoices to corporate pass contact
     */
    @Test
    void makeCorporatePassTest() {
        MakeCorporatePassRequest request = null;
	//api.makeCorporatePass(request);
        
        // TODO: test validations
        
        
    }
    
}
