package ish.oncourse.willow.editor.service.impl

import com.google.inject.Inject
import ish.oncourse.model.RegionKey
import ish.oncourse.model.WebContent
import ish.oncourse.model.WebContentVisibility
import ish.oncourse.model.WebNode
import ish.oncourse.model.WebNodeType
import ish.oncourse.model.WebUrlAlias
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.services.textile.ConvertCoreTextile
import ish.oncourse.willow.editor.model.PageUrl
import ish.oncourse.willow.editor.model.common.CommonError
import ish.oncourse.willow.editor.rest.UpdatePage
import ish.oncourse.willow.editor.rest.WebNodeToPage
import ish.oncourse.willow.editor.service.*
import ish.oncourse.willow.editor.model.Page
import ish.oncourse.willow.editor.model.api.PageRenderResponse

import groovy.transform.CompileStatic
import ish.oncourse.willow.editor.services.RequestService
import ish.oncourse.willow.editor.website.WebNodeFunctions
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.naming.Context
import javax.ws.rs.ClientErrorException
import javax.ws.rs.core.Response

@CompileStatic
class PageApiServiceImpl implements PageApi {

    private static Logger logger = LogManager.logger

    private ICayenneService cayenneService
    private RequestService requestService

    @Inject
    PageApiServiceImpl(ICayenneService cayenneService, RequestService requestService) {
        this.cayenneService = cayenneService
        this.requestService = requestService
    }

    Page addPage() {
        ObjectContext context = cayenneService.newContext()
        WebNode node = WebNodeFunctions.createNewNode(requestService.request, context)
        context.commitChanges()

        return WebNodeToPage.valueOf(node).page
    }
    
    void deletePage(Double number) {
        ObjectContext ctx = cayenneService.newContext()
        Integer intNumber = number.toInteger()
        WebNode node = WebNodeFunctions.getNodeForNumber(intNumber, requestService.request, cayenneService.newContext())

        if (!node) {
            node.webContentVisibility.each { ctx.deleteObjects(ctx.localObject(it.webContent)) }
            WebNode localNode = ctx.localObject(node)
            ctx.deleteObjects(localNode)
            ctx.commitChanges()

        } else {
            throw createClientException("There is no page to remove for provided number. Number: $intNumber")
        }
    }
    
    Page getPageByUrl(String pageUrl) {
        WebNode node = WebNodeFunctions.getNodeByPath(pageUrl, requestService.request, cayenneService.newContext())

        if (node) {
            return WebNodeToPage.valueOf(node).page
        } else {
            throw createClientException("There are no pages for provided url. Url: $pageUrl")
        }
    }
    
    PageRenderResponse getPageRender(Double pageId) {
        
        return null
    }
    
    List<Page> getPages() {
        return WebNodeFunctions.getNodes(requestService.request, cayenneService.newContext())
                .collect { node -> WebNodeToPage.valueOf(node).page }
    }
    
    Page savePage(Page pageParams) {
        ObjectContext context = cayenneService.newContext()
        UpdatePage updater = UpdatePage.valueOf(pageParams, context, requestService.request).updatePage()
        if (updater.error) {
            throw createClientException(updater.error)
        }
        return pageParams
    }
    
    private ClientErrorException createClientException(String message) {
        logger.error("$message, server name: $requestService.request.serverName")
         new ClientErrorException(Response.status(400).entity(new CommonError(message: message)).build())
    }
    
}

