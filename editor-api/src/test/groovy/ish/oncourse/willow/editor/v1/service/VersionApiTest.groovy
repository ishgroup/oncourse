package ish.oncourse.willow.editor.v1.service;

import ish.oncourse.willow.editor.v1.model.Version;
import ish.oncourse.willow.editor.v1.model.common.CommonError;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API tests for VersionApi
 */
public class VersionApiTest {

    private VersionApi api;
    
    @Before
    public void setup() {
    }

    
    /**
     * Publish current draft version. Support basic Authentication
     * Publish current draft changes to live site. Support basic Authentication
     */
    @Test
    public void versionDraftPublishPostTest() {
	//api.versionDraftPublishPost();
        
        // TODO: test validations
        
        
    }
    
    /**
     * Set current published version by provided version Id. Not supported yet
     * 
     */
    @Test
    public void versionDraftSetIdPostTest() {
        String id = null;
	//api.versionDraftSetIdPost(id);
        
        // TODO: test validations
        
        
    }
    
    /**
     * Get array of published versions. Not supported yet
     * Get array of published versions. Not supported yet
     */
    @Test
    public void versionListGetTest() {
	//List<Version> response = api.versionListGet();
        //assertNotNull(response);
        // TODO: test validations
        
        
    }
    
}
