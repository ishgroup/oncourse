package ish.oncourse.willow.editor.rest

import ish.oncourse.model.RegionKey
import ish.oncourse.model.WebNode
import ish.oncourse.willow.editor.model.Page

class WebNodeToPage {
    
    private WebNode webNode
    
    private WebNodeToPage(){}

    static WebNodeToPage valueOf(WebNode webNode) {
        WebNodeToPage serializer = new WebNodeToPage()
        serializer.webNode = webNode
        serializer
    }

    Page getPage() {
        return new Page().with {page ->
            page.id = webNode.id.doubleValue()
            page.title = webNode.name
            page.themeId = webNode.webNodeType.id.doubleValue()
            page.content = webNode.webContentVisibility?.find {it.regionKey == RegionKey.content}?.webContent?.contentTextile
            page.visible = webNode.published
            page.urls += webNode.webUrlAliases .collect { webUrlAlias ->  WebUrlAliasToPageUrl.valueOf(webUrlAlias).pageUrl }
            page
        }
    } 
    
}
