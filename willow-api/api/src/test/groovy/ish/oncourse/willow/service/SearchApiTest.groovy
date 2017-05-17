package ish.oncourse.willow.service;

import ish.oncourse.willow.model.common.CommonError;
import ish.oncourse.willow.model.common.Item;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API tests for SearchApi
 */
public class SearchApiTest {

    private SearchApi api;
    
    @Before
    public void setup() {
    }

    
    /**
     * Suburb endpoint
     * Provide autocomplete candidates for searche text
     */
    @Test
    public void getCountriesTest() {
        String text = null;
	//List<Item> response = api.getCountries(text);
        //assertNotNull(response);
        // TODO: test validations
        
        
    }
    
    /**
     * Suburb endpoint
     * Provide autocomplete candidates for searche text
     */
    @Test
    public void getLanguagesTest() {
        String text = null;
	//List<Item> response = api.getLanguages(text);
        //assertNotNull(response);
        // TODO: test validations
        
        
    }
    
    /**
     * Postcode endpoint
     * Provide autocomplete candidates for provided text
     */
    @Test
    public void getPostcodesTest() {
        String text = null;
	//List<Item> response = api.getPostcodes(text);
        //assertNotNull(response);
        // TODO: test validations
        
        
    }
    
    /**
     * Suburb endpoint
     * Provide autocomplete candidates for searche text
     */
    @Test
    public void getSuburbsTest() {
        String text = null;
	//List<Item> response = api.getSuburbs(text);
        //assertNotNull(response);
        // TODO: test validations
        
        
    }
    
}
