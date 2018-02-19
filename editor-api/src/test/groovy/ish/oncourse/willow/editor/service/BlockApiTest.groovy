package ish.oncourse.willow.editor.service

import ish.oncourse.model.WebContent
import ish.oncourse.model.WebSite
import ish.oncourse.model.WebSiteVersion
import ish.oncourse.willow.editor.model.Block
import ish.oncourse.willow.editor.model.common.CommonError
import ish.oncourse.willow.editor.v1.service.impl.BlockApiServiceImpl
import ish.oncourse.willow.editor.website.WebSiteVersionFunctions
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.junit.Test
import org.junit.Before

import javax.ws.rs.ClientErrorException

import static org.junit.Assert.*

/**
 * API tests for BlockApi
 */
class BlockApiTest extends AbstractEditorTest{

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
        Block block = api.addBlock()
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
        try {
            api.deleteBlock('New block (1)')
        } catch (ClientErrorException e) {
            assertTrue(e.response.entity instanceof CommonError)
            assertEquals('The block (name: New block (1)) could not be removed', (e.response.entity as CommonError).message)
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

        api.deleteBlock('New block (100)')

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
        
        api.saveBlock(new Block().with { b ->
            b.id = block.id.doubleValue()
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
        AbstractEditorTest.CreateDefaultWebSiteStructure.valueOf('other', cayenneService.newContext()).create()

        WebContent block = (ObjectSelect.query(WebContent).where(WebContent.NAME.eq('New block (1)')) & WebContent.WEB_SITE_VERSION.dot(WebSiteVersion.WEB_SITE).dot(WebSite.SITE_KEY).eq('other'))
                .selectOne(cayenneService.newContext())
        
        assertNotNull(block)
        assertEquals(2L, block.webSiteVersion.webSite.id)
        
        Block blockRequest = new Block().with { b ->
            b.id = block.id.doubleValue()
            b.title = 'changed name'
            b.content = 'changed content'
            b
        }
        try {
            api.saveBlock(blockRequest)
        } catch (ClientErrorException e) {
            assertTrue(e.response.entity instanceof CommonError)
            assertEquals("There are no block for blockParams: $blockRequest".toString(), (e.response.entity as CommonError).message)
        }
    }
    
}
