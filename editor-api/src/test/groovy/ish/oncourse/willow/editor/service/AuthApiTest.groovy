package ish.oncourse.willow.editor.service;

import ish.oncourse.willow.editor.model.User;
import ish.oncourse.willow.editor.model.api.LoginRequest;
import ish.oncourse.willow.editor.model.common.CommonError;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API tests for AuthApi
 */
public class AuthApiTest {

    private AuthApi api;
    
    @Before
    public void setup() {
    }

    
    /**
     * Get user by email and password
     * Get user by email and password (Login)
     */
    @Test
    public void getUserTest() {
        LoginRequest loginRequest = null;
	//User response = api.getUser(loginRequest);
        //assertNotNull(response);
        // TODO: test validations
        
        
    }
    
    /**
     * Unauthorize user from CMS
     * Unauthorize user from CMS
     */
    @Test
    public void logoutTest() {
	//api.logout();
        
        // TODO: test validations
        
        
    }
    
}
