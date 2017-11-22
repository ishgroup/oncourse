package ish.oncourse.willow.editor.service.impl

import ish.oncourse.model.WebContent
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.willow.editor.service.*
import ish.oncourse.willow.editor.model.Block
import ish.oncourse.willow.editor.model.common.CommonError

import groovy.transform.CompileStatic
import ish.oncourse.willow.editor.services.RequestService
import ish.oncourse.willow.editor.website.WebContentFunctions
import org.apache.cayenne.ObjectContext

@CompileStatic
class BlockApiServiceImpl implements BlockApi {

    private ICayenneService cayenneService
    private RequestService requestService

    BlockApiServiceImpl(ICayenneService cayenneService, RequestService requestService) {
        this.cayenneService = cayenneService
        this.requestService = requestService
    }

    Block addBlock() {
        ObjectContext context = cayenneService.newContext()
        WebContent content = WebContentFunctions.createNewWebContent(requestService.request, context)
        context.commitChanges()

        return new Block().with {block ->
            block.id = content.id.doubleValue()
            block.title = content.name
            block.html = content.contentTextile
            block
        }
    }
    
    void deleteBlock(Double id) {
        // TODO: Implement...
    }
    
    List<Block> getBlocks() {
        List<WebContent> blocks = WebContentFunctions.getBlocks(requestService.request, cayenneService.newContext())

        return blocks.collect { node -> new Block().with { block ->
            block.id = node.id.doubleValue()
            block.title = node.name
            block.html = node.contentTextile
            block
        }}
    }
    
    Block saveBlock(Block saveBlockRequest) {
        // TODO: Implement...
        
        return null
    }
    
}

