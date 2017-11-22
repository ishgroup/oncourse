package ish.oncourse.willow.editor.service;

import ish.oncourse.willow.editor.model.Block;
import ish.oncourse.willow.editor.model.common.CommonError
import ish.oncourse.willow.editor.service.impl.BlockApiServiceImpl
import org.junit.Assert
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API tests for BlockApi
 */
class BlockApiTest extends AbstractEditorTest{

    private BlockApi api;
    
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

        Assert.assertNotNull(block)
        Assert.assertNotNull(block.title)
        Assert.assertEquals(block.html, 'Sample content')
        Assert.assertEquals(block.id, 2,0)
    }
    
    /**
     * Remove block by provided Id
     * Remove block
     */
    @Test
    void deleteBlockTest() {
	//api.deleteBlock();
        
        // TODO: test validations
        
        
    }
    
    /**
     * Get array of blocks
     * Get array of blocks
     */
    @Test
    void getBlocksTest() {
        Block newBlock = api.addBlock()
	    List blocks = api.getBlocks()

        Assert.assertNotNull(newBlock)
        Assert.assertNotNull(blocks)

        Assert.assertEquals(blocks[0].id, newBlock.id, 0)
        Assert.assertEquals(blocks[0].title, newBlock.title)
        Assert.assertEquals(blocks[0].html, newBlock.html)
    }
    
    /**
     * block with updated params
     * Save block params
     */
    @Test
    void saveBlockTest() {
        Block saveBlockRequest = null;
	//Block response = api.saveBlock(saveBlockRequest);
        //assertNotNull(response);
        // TODO: test validations
        
        
    }
    
}
