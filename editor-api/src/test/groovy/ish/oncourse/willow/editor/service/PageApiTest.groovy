package ish.oncourse.willow.editor.service;

import ish.oncourse.willow.editor.model.Model200;
import ish.oncourse.willow.editor.model.Page;
import ish.oncourse.willow.editor.model.common.CommonError;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API tests for PageApi
 */
public class PageApiTest {

    private PageApi api;
    
    @Before
    public void setup() {
    }

    
    /**
     * Add new page
     * Add new page
     */
    @Test
    public void addPageTest() {
	//CommonError response = api.addPage();
        //assertNotNull(response);
        // TODO: test validations
        
        
    }
    
    /**
     * Remove page by provided Id
     * Remove page
     */
    @Test
    public void deletePageTest() {
	//api.deletePage();
        
        // TODO: test validations
        
        
    }
    
    /**
     * Get page by provided page url
     * Get page object by provided page url
     */
    @Test
    public void getPageByUrlTest() {
        String pageUrl = null;
	//Page response = api.getPageByUrl(pageUrl);
        //assertNotNull(response);
        // TODO: test validations
        
        
    }
    
    /**
     * Get page rendered html by provided page id
     * Get page rendered html by provided page id
     */
    @Test
    public void getPageRenderTest() {
        String pageId = null;
	//Model200 response = api.getPageRender(pageId);
        //assertNotNull(response);
        // TODO: test validations
        
        
    }
    
    /**
     * Get pages
     * Get pages
     */
    @Test
    public void getPagesTest() {
	//List<Page> response = api.getPages();
        //assertNotNull(response);
        // TODO: test validations
        
        
    }
    
    /**
     * Save page parameters
     * Save page parameters
     */
    @Test
    public void savePageTest() {
        Page pageParams = null;
	//Page response = api.savePage(pageParams);
        //assertNotNull(response);
        // TODO: test validations
        
        
    }
    
}
