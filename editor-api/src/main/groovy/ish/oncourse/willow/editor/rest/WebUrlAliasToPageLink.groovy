package ish.oncourse.willow.editor.rest

import ish.oncourse.model.WebUrlAlias
import ish.oncourse.willow.editor.v1.model.PageLink

class WebUrlAliasToPageLink {

    private WebUrlAlias webUrlAlias
    
    private WebUrlAliasToPageUrl(){}

    static WebUrlAliasToPageLink valueOf(WebUrlAlias webUrlAlias) {
        WebUrlAliasToPageLink serializer = new WebUrlAliasToPageLink()
        serializer.webUrlAlias = webUrlAlias
        serializer
    }
    
    PageLink getPageLink() {
        return new PageLink().with { url ->
            url.link = webUrlAlias.urlPath
            url.isDefault = webUrlAlias.default
            url
        }
    }
}
