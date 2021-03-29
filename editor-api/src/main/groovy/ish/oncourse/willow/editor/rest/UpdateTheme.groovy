package ish.oncourse.willow.editor.rest

import ish.oncourse.model.*
import ish.oncourse.willow.editor.v1.model.BlockPosition
import ish.oncourse.willow.editor.v1.model.Theme
import ish.oncourse.willow.editor.website.ResourceNameUtil
import ish.oncourse.willow.editor.website.WebContentFunctions
import ish.oncourse.willow.editor.website.WebNodeTypeFunctions
import ish.oncourse.willow.editor.website.WebSiteLayoutFunctions
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.commons.lang3.StringUtils
import org.eclipse.jetty.server.Request

import static ish.oncourse.model.RegionKey.*

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
        error = ResourceNameUtil.nameValidator.validate(resourceToSave.title)
        if (error) {
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
        
        nodeType.name = resourceToSave.title

        List<String> duplicates = (resourceToSave.paths.countBy {it}.grep { it.value > 1 } as List<Map.Entry<String, Integer>>).collect { it.key }
        if (!duplicates.empty) {
            error = "Theme paths must be unique: ${duplicates.join(',')} "
            return this
        }
        context.deleteObjects(nodeType.webLayoutPaths.findAll {!(it.path in resourceToSave.paths)})
        resourceToSave.paths.findAll {! (it in nodeType.webLayoutPaths*.path) }.each {path ->
            WebLayoutPath duplicatePath = ObjectSelect.query(WebLayoutPath)
                    .where(WebLayoutPath.WEB_SITE_VERSION.eq(nodeType.webSiteVersion))
                    .and(WebLayoutPath.WEB_NODE_TYPE.ne(nodeType))
                    .and(WebLayoutPath.PATH.eq(path)).selectFirst(context)
            
            if (duplicatePath) {
                error = "The '$path' already used for '$duplicatePath.webNodeType.name' theme"
                return this
            } else {
                WebLayoutPath newPath = context.newObject(WebLayoutPath)
                newPath.webNodeType = nodeType
                newPath.path = path
                newPath.webSiteVersion = nodeType.webSiteVersion
                newPath.created = new Date()
                newPath.modified = new Date()
            }
        }
        
        error = updateBlockVisibility()
        return this
    }

    private String updateBlockVisibility() {
        context.deleteObjects(nodeType.webContentVisibilities)

        return assignRegion(resourceToSave.blocks.top, header)?:
            assignRegion(resourceToSave.blocks.left, left)?:
            assignRegion(resourceToSave.blocks.centre, content)?:
            assignRegion(resourceToSave.blocks.right, right)?:
            assignRegion(resourceToSave.blocks.footer, footer)?: null
    }
    
    private String assignRegion(List<BlockPosition> blocks, RegionKey region) {
        return blocks.findResult { block ->  putBlockToPosition(block, region) }
    }

    /**
     * Return string error if BlockItem is invalid or can not be assigned to position
     * @param item
     * @param region
     * @return
     */
    private String putBlockToPosition(BlockPosition item, RegionKey region) {
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
