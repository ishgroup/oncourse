package ish.oncourse.willow.editor.v1.service;

import ish.oncourse.willow.editor.v1.model.LoginRequest;
import ish.oncourse.willow.editor.v1.model.User;
import ish.oncourse.willow.editor.v1.model.common.CommonError;
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
     * Get current logged user
     * 
     */
    @Test
    public void userGetGetTest() {
	//User response = api.userGetGet();
        //assertNotNull(response);
        // TODO: test validations
        
        
    }
    
    /**
     * Authenticate user by email and password. Put session toke to cookie on success
     * 
     */
    @Test
    public void userLoginPostTest() {
        LoginRequest loginRequest = null;
	//api.userLoginPost(loginRequest);
        
        // TODO: test validations
        
        
    }
    
    /**
     * De-authorize user from editor
     * 
     */
    @Test
    public void userLogoutPostTest() {
	//api.userLogoutPost();
        
        // TODO: test validations
        
        
    }
    
}
