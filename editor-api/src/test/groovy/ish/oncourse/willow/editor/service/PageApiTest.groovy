package ish.oncourse.willow.editor.service

import ish.oncourse.willow.editor.model.Page
import ish.oncourse.willow.editor.service.impl.PageApiServiceImpl
import org.junit.Assert
import org.junit.Test
import org.junit.Before

/**
 * API tests for PageApi
 */ 
class PageApiTest extends AbstractEditorTest{

    private PageApi api
    
    @Before
    void setup() {
        super.setup()
        api = new PageApiServiceImpl(cayenneService, requestService)
    }
    
    /**
     * Add new page
     */
    @Test
    void addPageTest() {
        Page page = api.addPage()
        Assert.assertNotNull(page)
    }
    
    /**
     * Remove page by provided Id
     * Remove page
     */
    @Test
    void deletePageTest() {
	//api.deletePage()
        
        // TODO: test validations
        
        
    }
    
    /**
     * Get page by provided page url
     * Get page object by provided page url
     */
    @Test
    void getPageByUrlTest() {
        String pageUrl = null
	//Page response = api.getPageByUrl(pageUrl)
        //assertNotNull(response)
        // TODO: test validations
    }
    
    /**
     * Get page rendered html by provided page id
     * Get page rendered html by provided page id
     */
    @Test
    void getPageRenderTest() {
        String pageId = null
	//Model200 response = api.getPageRender(pageId)
        //assertNotNull(response)
        // TODO: test validations
        
        
    }
    
    /**
     * Get pages
     * Get pages
     */
    @Test
    void getPagesTest() {
	//List<Page> response = api.getPages()
        //assertNotNull(response)
        // TODO: test validations
        
        
    }
    
    /**
     * Save page parameters
     * Save page parameters
     */
    @Test
    void savePageTest() {
        Page pageParams = null
	//Page response = api.savePage(pageParams)
        //assertNotNull(response)
        // TODO: test validations
        
        
    }
    
}
