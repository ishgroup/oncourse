package ish.oncourse.willow.editor.rest

import ish.oncourse.model.RegionKey
import ish.oncourse.model.WebNodeType
import ish.oncourse.services.content.BlocksForRegionKey
import ish.oncourse.willow.editor.model.BlockItem
import ish.oncourse.willow.editor.model.Theme
import ish.oncourse.willow.editor.model.ThemeSchema

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
            theme.schema = new ThemeSchema().with { schema ->
                schema.top = getBlocksBy(header)
                schema.left = getBlocksBy(left)
                schema.centre = getBlocksBy(content)
                schema.right = getBlocksBy(right)
                schema.footer = getBlocksBy(footer)
                schema
            }
            theme
        }
    }
    
    private List<BlockItem> getBlocksBy(RegionKey regionKey) {
        return BlocksForRegionKey.valueOf(webNodeType, regionKey, webNodeType.webSiteVersion).get()
                .collect { block -> 
                            new BlockItem().with { item ->
                                item.id = block.id.intValue()
                                item.position = block.getWebContentVisibility(webNodeType).weight
                                item
                            }
                }
    }   

}
