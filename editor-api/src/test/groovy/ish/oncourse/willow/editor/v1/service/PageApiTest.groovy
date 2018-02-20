package ish.oncourse.willow.editor.v1.service

import groovy.transform.CompileStatic
import ish.oncourse.model.WebNode
import ish.oncourse.willow.editor.service.AbstractEditorTest;
import ish.oncourse.willow.editor.v1.model.Page;
import ish.oncourse.willow.editor.v1.model.common.CommonError
import ish.oncourse.willow.editor.v1.service.impl.PageApiServiceImpl
import ish.oncourse.willow.editor.website.WebNodeFunctions;
import org.junit.Test;
import org.junit.Before

import javax.ws.rs.ClientErrorException;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API tests for PageApi
 */
@CompileStatic
public class PageApiTest extends AbstractEditorTest {

    private PageApi api;
    
    @Before
    public void setup() {
        super.setup()
        api = new PageApiServiceImpl(cayenneService, requestService)
    }



    /**
     * Add new page
     */
    @Test
    void addPageTest() {
        Page page = api.pageCreatePost()
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
        Page newPage = api.pageCreatePost()
        assertNotNull(newPage)

        api.pageDeleteIdPost(newPage.id.toString())
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
        Page newPage = api.pageCreatePost()
        String number = newPage.serialNumber.toString()
        String pageUrl = "/page/${number}"

        assertEquals(pageUrl, "/page/2")

        Page page = api.pageGetGet(pageUrl)
        assertNotNull(page)
        assertEquals(page.serialNumber, 2)
    }

    /**
     * Get page by provided page url
     * Get page object by provided page url
     */
    @Test
    void getNotExistingPageByTechnicalUrlTest() {
        String pageUrl = "/page/123"

        try {
            Page page = api.pageGetGet(pageUrl)
            assertNotNull(page)
            assertEquals(page.serialNumber, 2)
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
        try {
            api.pageGetGet('/course/123')
        } catch (ClientErrorException e) {
            assertTrue('Client Exception should be thrown',false)
        }

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
