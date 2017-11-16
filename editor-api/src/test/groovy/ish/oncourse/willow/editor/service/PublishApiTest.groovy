package ish.oncourse.willow.editor.service;

import ish.oncourse.willow.editor.model.SetVersionRequest;
import ish.oncourse.willow.editor.model.Version;
import ish.oncourse.willow.editor.model.common.CommonError;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API tests for PublishApi
 */
public class PublishApiTest {

    private PublishApi api;
    
    @Before
    public void setup() {
    }

    
    /**
     * Get array of published versions
     * Get array of published versions
     */
    @Test
    public void getVersionsTest() {
	//List<Version> response = api.getVersions();
        //assertNotNull(response);
        // TODO: test validations
        
        
    }
    
    /**
     * Publish current draft version
     * Publish current draft changes to live site
     */
    @Test
    public void publishTest() {
	//api.publish();
        
        // TODO: test validations
        
        
    }
    
    /**
     * Set current published version by provided version Id
     * Change current published version
     */
    @Test
    public void setVersionTest() {
        SetVersionRequest setVersionRequest = null;
	//api.setVersion(setVersionRequest);
        
        // TODO: test validations
        
        
    }
    
}
