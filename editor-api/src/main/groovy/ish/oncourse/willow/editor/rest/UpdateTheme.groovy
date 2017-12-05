package ish.oncourse.willow.editor.rest

import ish.oncourse.model.RegionKey
import ish.oncourse.model.WebContent
import ish.oncourse.model.WebContentVisibility
import ish.oncourse.model.WebNodeType
import ish.oncourse.model.WebSiteLayout
import ish.oncourse.willow.editor.model.BlockItem
import ish.oncourse.willow.editor.model.Theme
import ish.oncourse.willow.editor.website.WebContentFunctions
import ish.oncourse.willow.editor.website.WebNodeTypeFunctions
import ish.oncourse.willow.editor.website.WebSiteLayoutFunctions
import org.apache.cayenne.ObjectContext
import org.apache.commons.lang3.StringUtils
import org.eclipse.jetty.server.Request

import static ish.oncourse.model.RegionKey.content
import static ish.oncourse.model.RegionKey.footer
import static ish.oncourse.model.RegionKey.header
import static ish.oncourse.model.RegionKey.left
import static ish.oncourse.model.RegionKey.right

class UpdateTheme extends AbstractUpdate<Theme> {

    private WebNodeType nodeType
    private List<Long> addedBlocks 
    private Map<RegionKey, List<Integer>> positionsMap


    private UpdateTheme() {}

    static UpdateTheme valueOf(Theme themeToSave, ObjectContext context, Request request) {
        UpdateTheme updater = new UpdateTheme()
        updater.init(themeToSave, context, request)
        updater.nodeType = WebNodeTypeFunctions.getWebNodeTypeById(themeToSave.id.longValue(), request, context)
        updater.addedBlocks = new ArrayList<>()
        updater.positionsMap = [(header) : [], (left) : [], (content) : [], (right) : [], (footer) : []]
        return updater
    }



    @Override
    UpdateTheme update() {
        if (!nodeType) {
            error = "There are no theme for themeParams: $resourceToSave"
            return this
        }
        WebSiteLayout layout = WebSiteLayoutFunctions.getLayoutById(resourceToSave.layoutId.longValue(), request, context)
        if (!layout) {
            error = "There are no layout for pageParams: $resourceToSave"
            return this
        }
        //we need the code to set trimmed name to for the page
        resourceToSave.title = StringUtils.trimToEmpty(resourceToSave.title)
        if (resourceToSave.title.length() < 3) {
            error = "There should be at least 3 characters in theme name"
            return this
        }
        if (nodeType.defaultPageTheme && nodeType.name != resourceToSave.title) {
            error = "The default theme name can't be changed"
            return this
        }
        
        
        WebNodeType duplicate = WebNodeTypeFunctions.getWebNodeTypeByName(resourceToSave.title, request, context)
        if (duplicate  && duplicate.objectId != nodeType.objectId) {
            error = "Theme name must be unique"
            return this
        }
        nodeType.webSiteLayout = layout
        nodeType.name = resourceToSave.title
        
        error = updateBlockVisibility()
        return this
    }

    private String updateBlockVisibility() {
        context.deleteObjects(nodeType.webContentVisibilities)

        return assignRegion(resourceToSave.schema.top, header)?:
            assignRegion(resourceToSave.schema.left, left)?:
            assignRegion(resourceToSave.schema.centre, content)?:
            assignRegion(resourceToSave.schema.right, right)?:
            assignRegion(resourceToSave.schema.footer, footer)?: null
    }
    
    private String assignRegion(List<BlockItem> blocks, RegionKey region) {
        return blocks.findResult { block ->  putBlockToPosition(block, region) }
    }

    /**
     * Return string error if BlockItem is invalid or can not be assigned to position
     * @param item
     * @param region
     * @return
     */
    private String putBlockToPosition(BlockItem item, RegionKey region) {
        Long id = item.id.toLong()
        Integer position = item.position.toInteger()

                WebContent block = WebContentFunctions.getBlockById(request, context, item.id.longValue())
        if (!block) {
            return  "Block is invalid"
        }
        if (id in addedBlocks) {
            return "Can not assign the same block twice, block name: $block.name"
        }
        if (position in positionsMap[region]) {
            return "Can not assign more the one block to position, block name: $block.name, region:${region.name()}, order:${position}"
        }
        
        addedBlocks << block.id
        positionsMap[region] << position
        
        WebContentVisibility visibility = context.newObject(WebContentVisibility)
        visibility.webContent = block
        visibility.regionKey = region
        visibility.webNodeType = nodeType
        visibility.weight = position
        
        return null
    }

}
