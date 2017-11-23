package ish.oncourse.willow.editor.service.impl

import ish.oncourse.model.WebContent
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.willow.editor.rest.WebContentToBlock
import ish.oncourse.willow.editor.rest.WebNodeToPage
import ish.oncourse.willow.editor.service.*
import ish.oncourse.willow.editor.model.Block
import ish.oncourse.willow.editor.model.common.CommonError

import groovy.transform.CompileStatic
import ish.oncourse.willow.editor.services.RequestService
import ish.oncourse.willow.editor.website.WebContentFunctions
import org.apache.cayenne.ObjectContext
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.ws.rs.ClientErrorException
import javax.ws.rs.core.Response

@CompileStatic
class BlockApiServiceImpl implements BlockApi {

    private static Logger logger = LogManager.logger

    private ICayenneService cayenneService
    private RequestService requestService

    BlockApiServiceImpl(ICayenneService cayenneService, RequestService requestService) {
        this.cayenneService = cayenneService
        this.requestService = requestService
    }

    Block addBlock() {
        ObjectContext context = cayenneService.newContext()
        WebContent webContent = WebContentFunctions.createNewWebContent(requestService.request, context)
        context.commitChanges()

        return WebContentToBlock.valueOf(webContent).block
    }

    void deleteBlock(Double id) {
        // TODO: Implement...
    }
    
    List<Block> getBlocks() {
        WebContentFunctions.getBlocks(requestService.request, cayenneService.newContext())
                .collect { node -> WebContentToBlock.valueOf(node).block }
    }
    
    Block saveBlock(Block saveBlockRequest) {
        // TODO: Implement...
        
        return null
    }

    private ClientErrorException createClientException(String message) {
        logger.error(message)
        new ClientErrorException(Response.status(400).entity(new CommonError(message: message)).build())
    }
    
}

