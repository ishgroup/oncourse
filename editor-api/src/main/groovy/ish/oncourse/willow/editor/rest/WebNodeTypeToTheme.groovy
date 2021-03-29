package ish.oncourse.willow.editor.rest

import ish.oncourse.model.RegionKey
import ish.oncourse.model.WebNodeType
import ish.oncourse.services.content.BlocksForRegionKey
import ish.oncourse.willow.editor.v1.model.BlockPosition
import ish.oncourse.willow.editor.v1.model.Theme
import ish.oncourse.willow.editor.v1.model.ThemeBlocks

import static ish.oncourse.model.RegionKey.*

class WebNodeTypeToTheme {

    private WebNodeType webNodeType

    private WebNodeTypeToTheme(){}

    static WebNodeTypeToTheme valueOf(WebNodeType webNodeType) {
        WebNodeTypeToTheme serializer = new WebNodeTypeToTheme()
        serializer.webNodeType = webNodeType
        serializer
    }

    Theme getTheme() {
        return new Theme().with { theme ->
            theme.title = webNodeType.name
            theme.id = webNodeType.id.intValue()
            theme.layoutId = webNodeType.webSiteLayout.id.intValue()
            theme.blocks = new ThemeBlocks().with { schema ->
                schema.top = getBlocksBy(header)
                schema.left = getBlocksBy(left)
                schema.centre = getBlocksBy(content)
                schema.right = getBlocksBy(right)
                schema.footer = getBlocksBy(footer)
                schema
            }
            theme.paths = webNodeType.webLayoutPaths*.path
            theme
        }
    }
    
    private List<BlockPosition> getBlocksBy(RegionKey regionKey) {
        return BlocksForRegionKey.valueOf(webNodeType, regionKey, webNodeType.webSiteVersion).get()
                .collect { block -> 
                            new BlockPosition().with { item ->
                                item.id = block.id.intValue()
                                item.title = block.name
                                item.position = block.getWebContentVisibility(webNodeType).weight
                                item
                            }
                }
    }   

}
