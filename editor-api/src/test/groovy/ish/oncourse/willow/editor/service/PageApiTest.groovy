package ish.oncourse.willow.editor.service

import ish.oncourse.model.WebNode
import ish.oncourse.willow.editor.model.Page
import ish.oncourse.willow.editor.service.impl.PageApiServiceImpl
import ish.oncourse.willow.editor.website.WebNodeFunctions
import org.junit.Test
import org.junit.Before

import javax.ws.rs.ClientErrorException

import static org.junit.Assert.*

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
        assertNotNull(page)
        assertNotNull(page.title)
        assertNotNull(page.themeId)
        assertEquals(page.content, 'Sample content')
        assertEquals(page.urls, [])
        assertEquals(page.visible, false)
    }
    
    /**
     * Remove page by provided Number
     * Remove page
     */
    @Test
    void deletePageTest() {
        Page newPage = api.addPage()
        assertNotNull(newPage)

        api.deletePage(newPage.id.toString())
        Integer id = newPage.id

        WebNode node = WebNodeFunctions.getNodeForId(id.toLong(), requestService.request, cayenneService.newContext())
        assertEquals(node, null)
    }
    
    /**
     * Get page by provided page url
     * Get page object by provided page url
     */
    @Test
    void getPageByTechnicalUrlTest() {
        Page newPage = api.addPage()
        String number = newPage.number.longValue().toString()
        String pageUrl = "/page/${number}"

        assertEquals(pageUrl, "/page/2")

        Page page = api.getPageByUrl(pageUrl)
        assertNotNull(page)
        assertEquals(page.number, 2,0)
    }

    /**
     * Get page by provided page url
     * Get page object by provided page url
     */
    @Test
    void getNotExistingPageByTechnicalUrlTest() {
        String pageUrl = "/page/123"

        try {
            Page page = api.getPageByUrl(pageUrl)
            assertNotNull(page)
            assertEquals(page.number, 2,0)
        } catch (e) {
            Boolean isClientError = e instanceof ClientErrorException
            assertEquals(isClientError, true)
        }
    }

    /**
     * Get page rendered html by provided page id
     * Get page rendered html by provided page id
     */
    @Test
    void getPageByUrl() {
        assertTrue(api.getPageByUrl('/course/123').reservedURL)
        
        try {
            assertTrue(api.getPageByUrl('/course/').reservedURL)
            assertTrue('Client Exception should be thrown',false)
        } catch (ClientErrorException ignored) {}
        
        try {
            assertTrue(api.getPageByUrl('/any').reservedURL)
            assertTrue('Client Exception should be thrown',false)
        } catch (ClientErrorException ignored) {}

        assertTrue(api.getPageByUrl('/pagenotfound').reservedURL)
        assertTrue(api.getPageByUrl('/PageNotFound').reservedURL)
        assertTrue(api.getPageByUrl('/courses/').reservedURL)
        assertTrue(api.getPageByUrl('/courses').reservedURL)
        assertFalse(api.getPageByUrl('/page/1').reservedURL)
        assertFalse(api.getPageByUrl('/').reservedURL)
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
