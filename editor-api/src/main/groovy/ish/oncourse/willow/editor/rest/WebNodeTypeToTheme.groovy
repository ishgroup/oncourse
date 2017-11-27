package ish.oncourse.willow.editor.rest

import ish.oncourse.model.WebNodeType
import ish.oncourse.willow.editor.model.Theme

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
            theme.id = webNodeType.id.doubleValue()
//            theme.layoutId = webNodeType.webSiteLayout
            theme
        }
    }

}
