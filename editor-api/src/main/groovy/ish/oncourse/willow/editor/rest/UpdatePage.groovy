package ish.oncourse.willow.editor.rest

import ish.oncourse.model.RegionKey
import ish.oncourse.model.WebContent
import ish.oncourse.model.WebNode
import ish.oncourse.model.WebNodeType
import ish.oncourse.model.WebUrlAlias
import ish.oncourse.services.textile.ConvertCoreTextile
import ish.oncourse.utils.ResourceNameValidator
import ish.oncourse.willow.editor.v1.model.Block
import ish.oncourse.willow.editor.v1.model.Page
import ish.oncourse.willow.editor.v1.model.PageUrl
import ish.oncourse.willow.editor.website.ResourceNameUtil
import ish.oncourse.willow.editor.website.WebContentFunctions
import ish.oncourse.willow.editor.website.WebNodeFunctions
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.SelectById
import org.apache.commons.lang3.StringUtils
import org.eclipse.jetty.server.Request

class UpdatePage extends AbstractUpdate<Page> {

    private UpdatePage() {}

    static UpdatePage valueOf(Page pageToSave, ObjectContext context, Request request) {
        UpdatePage updater = new UpdatePage()
        updater.init(pageToSave, context, request)
        return updater
    }


    UpdatePage update() {
        WebNode node = WebNodeFunctions.getNodeForId(resourceToSave.id.longValue(), request, context)
        if (!node) {
            error = "There are no pages for pageParams: $resourceToSave"
            return this
        }
        WebNodeType theme = SelectById.query(WebNodeType, resourceToSave.themeId.longValue()).selectFirst(context)
        if (!theme) {
            error = "There are no page theme for pageParams: $resourceToSave"
            return this
        }
        WebContent defaultBlock = node.webContentVisibility?.find { it.regionKey == RegionKey.content }?.webContent
        if (!theme) {
            error = "There are no default Web Content for pageParams: $resourceToSave"
            return this
        }
        
        resourceToSave.title = StringUtils.trimToEmpty(resourceToSave.title)
        error = new ResourceNameValidator().validate(resourceToSave.title)
        if (error) {
            return this
        }
        
        WebNode duplicate = WebNodeFunctions.getNodeForName(resourceToSave.title, request, context)
        if (duplicate && duplicate.objectId != node.objectId){
            error = "Page name must be unique, page name: $resourceToSave.title"
            return this
        }
        
        node.name = resourceToSave.title
        node.webNodeType = theme
        defaultBlock.contentTextile = resourceToSave.content
        defaultBlock.content = ConvertCoreTextile.valueOf(resourceToSave.content).convert()
        node.published = resourceToSave.visible
        updateAliases(node)
        return this
    }
    
    private void updateAliases(WebNode node) {
        Map<String, PageUrl> providedUrlsMap = resourceToSave.urls.collectEntries { [(it.link): it] }

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
            alias.urlPath = pageUrl.link
            //both sides relationship set required to prevent#14485 persist issue.
            node.addToWebUrlAliases(alias)
            alias.webNode = node
        }
    }
}
