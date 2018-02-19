package ish.oncourse.willow.editor.rest

import ish.oncourse.model.WebContent
import ish.oncourse.willow.editor.v1.model.Block

class WebContentToBlock {

    private WebContent webContent

    private WebContentToBlock(){}

    static WebContentToBlock valueOf(WebContent webContent) {
        WebContentToBlock serializer = new WebContentToBlock()
        serializer.webContent = webContent
        serializer
    }

    Block getBlock() {
        return new Block().with { block ->
            block.id = webContent.id.intValue()
            block.title = webContent.name
            block.content = webContent.contentTextile
            block
        }
    }

}
