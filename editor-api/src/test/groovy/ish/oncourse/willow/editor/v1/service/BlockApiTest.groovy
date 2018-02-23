package ish.oncourse.willow.editor.v1.service

import groovy.transform.CompileStatic
import ish.oncourse.model.WebContent
import ish.oncourse.model.WebSite
import ish.oncourse.model.WebSiteVersion
import ish.oncourse.willow.editor.service.AbstractEditorTest;
import ish.oncourse.willow.editor.v1.model.Block;
import ish.oncourse.willow.editor.v1.model.CommonError
import ish.oncourse.willow.editor.v1.service.impl.BlockApiServiceImpl
import ish.oncourse.willow.editor.website.WebContentFunctions
import ish.oncourse.willow.editor.website.WebSiteVersionFunctions
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect;
import org.junit.Test;
import org.junit.Before
import ish.oncourse.willow.editor.service.AbstractEditorTest.CreateDefaultWebSiteStructure

import javax.ws.rs.ClientErrorException

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API tests for BlockApi
 */
@CompileStatic
public class BlockApiTest  extends AbstractEditorTest{

    private BlockApi api

    @Before
    void setup() {
        super.setup()
        api = new BlockApiServiceImpl(cayenneService, requestService)
    }


    /**
     * Add new block
     * Add new block
     */
    @Test
    void addBlockTest() {
        Block block = api.createBlock()
        assertNotNull(block)
        assertNotNull(block.title)
        assertNotNull('New Block (2)')
        assertNull(block.content)
    }

    /**
     * Remove block by provided Id
     * Remove block
     */
    @Test
    void deleteBlockTest() {
        
        WebContent blockTodelete = WebContentFunctions.getBlockByName(requestService.request, cayenneService.newContext(),'New block (1)')
        try {
            api.deleteBlock(blockTodelete.id.toString())
        } catch (ClientErrorException e) {
            assertTrue(e.response.entity instanceof CommonError)
            assertEquals("The block (id: $blockTodelete.id) could not be removed".toString(), (e.response.entity as CommonError).message)
        }

        ObjectContext context = cayenneService.newContext()
        WebContent webContent = context.newObject(WebContent)
        webContent.name = 'New block (100)'
        webContent.webSiteVersion =  WebSiteVersionFunctions.getCurrentVersion(requestService.request,context)
        webContent.content = 'Content'
        webContent.contentTextile = 'Content'
        context.commitChanges()
        WebContent block = ObjectSelect.query(WebContent).where(WebContent.NAME.eq('New block (100)')).selectOne(cayenneService.newContext())
        assertNotNull(block)

        api.deleteBlock(webContent.id.toString())

        block = ObjectSelect.query(WebContent).where(WebContent.NAME.eq('New block (100)')).selectOne(cayenneService.newContext())
        assertNull(block)

    }

    /**
     * Get array of blocks
     * Get array of blocks
     */
    @Test
    void getBlocksTest() {
        List<Block> blocks = api.getBlocks()
        assertEquals(1, blocks.size())
        assertNotNull(blocks[0])
        assertEquals('New block (1)', blocks[0].title)
        assertEquals('Content', blocks[0].content)
    }

    /**
     * block with updated params
     * Save block params
     */
    @Test
    void saveBlockTest() {
        WebContent block = ObjectSelect.query(WebContent).where(WebContent.NAME.eq('New block (1)')).selectOne(cayenneService.newContext())

        api.updateBlock(new Block().with { b ->
            b.id = block.id.intValue()
            b.title = 'changed name'
            b.content = 'changed content'
            b
        })

        block = ObjectSelect.query(WebContent).where(WebContent.NAME.eq('New block (1)')).selectOne(cayenneService.newContext())
        assertNull(block)
        block = ObjectSelect.query(WebContent).where(WebContent.NAME.eq('changed name')).selectOne(cayenneService.newContext())
        assertNotNull(block)
        assertEquals('changed content', block.content)
    }

    /**
     * try to change block for other site
     * Save block params
     */
    @Test
    void crossSiteRequests() {
        //create structure for additional web site
        CreateDefaultWebSiteStructure.valueOf('other', cayenneService.newContext()).create()

        WebContent block = (ObjectSelect.query(WebContent).where(WebContent.NAME.eq('New block (1)')) & WebContent.WEB_SITE_VERSION.dot(WebSiteVersion.WEB_SITE).dot(WebSite.SITE_KEY).eq('other'))
                .selectOne(cayenneService.newContext())

        assertNotNull(block)
        assertEquals(2L, block.webSiteVersion.webSite.id)

        Block blockRequest = new Block().with { b ->
            b.id = block.id.intValue()
            b.title = 'changed name'
            b.content = 'changed content'
            b
        }
        try {
            api.updateBlock(blockRequest)
        } catch (ClientErrorException e) {
            assertTrue(e.response.entity instanceof CommonError)
            assertEquals("There are no block for blockParams: $blockRequest".toString(), (e.response.entity as CommonError).message)
        }
    }
    
}
