package ish.oncourse.willow.editor.rest

import ish.oncourse.model.WebContent
import ish.oncourse.services.textile.ConvertCoreTextile
import ish.oncourse.willow.editor.v1.model.Block
import ish.oncourse.willow.editor.website.ResourceNameUtil
import ish.oncourse.willow.editor.website.WebContentFunctions
import org.apache.cayenne.ObjectContext
import org.apache.commons.lang3.StringUtils
import org.eclipse.jetty.server.Request

class UpdateBlock extends AbstractUpdate<Block> {

    private UpdateBlock() {}

    static UpdateBlock valueOf(Block blockToSave, ObjectContext context, Request request) {
        UpdateBlock updater = new UpdateBlock()
        updater.init(blockToSave, context, request)
        return updater
    }

    UpdateBlock update() {
        
        WebContent block = WebContentFunctions.getBlockById(request, context, resourceToSave.id.longValue())
        if (!block) {
            error = "There are no block for blockParams: $resourceToSave"
            return this
        }

        resourceToSave.title = StringUtils.trimToEmpty(resourceToSave.title)
        error = ResourceNameUtil.nameValidator.validate(resourceToSave.title)
        if (error) {
            return this
        }
        
        WebContent duplicate = WebContentFunctions.getBlockByName(request, context, resourceToSave.title)
        if (duplicate && duplicate.objectId != block.objectId){
            error = "Block name must be unique, block name: $resourceToSave.title"
            return this
        }

        block.name = resourceToSave.title
        block.contentTextile =  resourceToSave.content
        block.content = ConvertCoreTextile.valueOf(resourceToSave.content).convert()
        
        return this
    }
}
