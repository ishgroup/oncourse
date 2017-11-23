package ish.oncourse.willow.editor.rest

import ish.oncourse.model.RegionKey
import ish.oncourse.model.WebContent
import ish.oncourse.model.WebNode
import ish.oncourse.model.WebNodeType
import ish.oncourse.model.WebUrlAlias
import ish.oncourse.services.textile.ConvertCoreTextile
import ish.oncourse.willow.editor.model.Page
import ish.oncourse.willow.editor.model.PageUrl
import ish.oncourse.willow.editor.website.WebNodeFunctions
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.SelectById
import org.eclipse.jetty.server.Request

class UpdatePage {

    private Page pageToSave
    private ObjectContext context
    private Request request
    
    private String error = null
    
    String getError() {
        return error
    }
    
    private UpdatePage() {}

    static UpdatePage valueOf(Page pageToSave, ObjectContext context, Request request) {
        UpdatePage updatePage = new UpdatePage()
        updatePage.pageToSave = pageToSave
        updatePage.context = context
        updatePage.request = request
        return updatePage
    }


    UpdatePage updatePage() {

        WebNode node = WebNodeFunctions.getNodeForNumber(pageToSave.number.intValue(), request, context)
        if (!node) {
            error = "There are no pages for pageParams: $pageToSave"
            return
        }
        WebNodeType theme = SelectById.query(WebNodeType, pageToSave.themeId.longValue()).selectFirst(context)
        if (!theme) {
            error = "There are no page theme for pageParams: $pageToSave"
            return
        }
        WebContent defaultBlock = node.webContentVisibility?.find { it.regionKey == RegionKey.content }?.webContent
        if (!theme) {
            error = "There are no default Web Content for pageParams: $pageToSave"
            return
        }

        node.name = pageToSave.title
        node.webNodeType = theme
        defaultBlock.contentTextile = pageToSave.content
        defaultBlock.content = ConvertCoreTextile.valueOf(pageToSave.content).convert()
        node.published = pageToSave.visible
        updateAliases(node)
        
        return this
    }
    
    private void updateAliases(WebNode node) {
        Map<String, PageUrl> providedUrlsMap = pageToSave.urls.collectEntries { [(it.link): it] }

        new ArrayList<WebUrlAlias>(node.webUrlAliases).each { alias ->
            PageUrl pageUrl = providedUrlsMap.remove(alias.urlPath)
            if (pageUrl) {
                //update existing alias
                alias.default = pageUrl.isDefault
            } else {
                //remove node if no present in save request
                node.removeFromWebUrlAliases(alias)
            }
        }
        
        //create new aliases (not presented in persistent object)
        providedUrlsMap.values().each { pageUrl ->
            WebUrlAlias alias = context.newObject(WebUrlAlias)
            alias.webSiteVersion = node.webSiteVersion
            alias.urlPath = pageUrl
            //both sides relationship set required to prevent#14485 persist issue.
            node.addToWebUrlAliases(alias)
            alias.webNode = node
        }
    }
}
