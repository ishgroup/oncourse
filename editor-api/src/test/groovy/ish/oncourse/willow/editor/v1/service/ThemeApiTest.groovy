package ish.oncourse.willow.editor.v1.service;

import ish.oncourse.willow.editor.v1.model.Layout;
import ish.oncourse.willow.editor.v1.model.Theme;
import ish.oncourse.willow.editor.v1.model.common.CommonError;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API tests for ThemeApi
 */
public class ThemeApiTest {

    private ThemeApi api;
    
    @Before
    public void setup() {
    }

    
    /**
     * Get array of layouts
     * Get array of layouts
     */
    @Test
    public void layoutListGetTest() {
	//List<Layout> response = api.layoutListGet();
        //assertNotNull(response);
        // TODO: test validations
        
        
    }
    
    /**
     * Add new theme
     * Add new theme
     */
    @Test
    public void themeCreatePostTest() {
	//Theme response = api.themeCreatePost();
        //assertNotNull(response);
        // TODO: test validations
        
        
    }
    
    /**
     * Remove theme by unique identifier
     * Remove theme
     */
    @Test
    public void themeDeleteIdPostTest() {
        String id = null;
	//api.themeDeleteIdPost(id);
        
        // TODO: test validations
        
        
    }
    
    /**
     * Get array of themes
     * Get array of themes
     */
    @Test
    public void themeListGetTest() {
	//List<Theme> response = api.themeListGet();
        //assertNotNull(response);
        // TODO: test validations
        
        
    }
    
    /**
     * Theme with updated params
     * Save theme params
     */
    @Test
    public void themeUpdatePostTest() {
        Theme saveThemeRequest = null;
	//Theme response = api.themeUpdatePost(saveThemeRequest);
        //assertNotNull(response);
        // TODO: test validations
        
        
    }
    
}
