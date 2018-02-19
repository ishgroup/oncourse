package ish.oncourse.willow.editor.rest

import ish.oncourse.model.WebUrlAlias
import ish.oncourse.willow.editor.v1.model.PageUrl

class WebUrlAliasToPageUrl {

    private WebUrlAlias webUrlAlias
    
    private WebUrlAliasToPageUrl(){}

    static WebUrlAliasToPageUrl valueOf(WebUrlAlias webUrlAlias) {
        WebUrlAliasToPageUrl serializer = new WebUrlAliasToPageUrl()
        serializer.webUrlAlias = webUrlAlias
        serializer
    }
    
    PageUrl getPageUrl() {
        return new PageUrl().with { url ->
            url.link = webUrlAlias.urlPath
            url.isDefault = webUrlAlias.default
            url
        }
    }
}
