package ish.oncourse.willow.editor.v1.service.impl

import com.google.inject.Inject
import ish.oncourse.linktransform.PageIdentifier
import ish.oncourse.model.WebNode
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.willow.editor.v1.model.common.CommonError
import ish.oncourse.willow.editor.rest.UpdatePage
import ish.oncourse.willow.editor.rest.WebNodeToPage
import ish.oncourse.willow.editor.v1.model.Page

import groovy.transform.CompileStatic
import ish.oncourse.willow.editor.services.RequestService
import ish.oncourse.willow.editor.v1.service.PageApi
import ish.oncourse.willow.editor.website.WebNodeFunctions
import org.apache.cayenne.ObjectContext
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.ws.rs.ClientErrorException
import javax.ws.rs.ForbiddenException
import javax.ws.rs.core.Response

import static ish.oncourse.linktransform.PageIdentifier.*

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

    Page pageCreatePost() {
        ObjectContext context = cayenneService.newContext()
        WebNode node = WebNodeFunctions.createNewNode(requestService.request, context)
        context.commitChanges()

        return WebNodeToPage.valueOf(node).page
    }
    
    void pageDeleteIdPost(String id) {
        ObjectContext ctx = cayenneService.newContext()
        WebNode node = WebNodeFunctions.getNodeForId(id.toLong(), requestService.request, cayenneService.newContext())

        if (node) {
            node.webContentVisibility.each { ctx.deleteObjects(ctx.localObject(it.webContent)) }
            WebNode localNode = ctx.localObject(node)
            ctx.deleteObjects(localNode)
            ctx.commitChanges()

        } else {
            throw createClientException("There is no page to remove for provided number. id: $id")
        }
    }
    
    Page pageGetGet(String pageUrl) {
        if (!StringUtils.trimToNull(pageUrl)) {
            throw createClientException("Page url required." )
        }
        
        WebNode node = WebNodeFunctions.getNodeByPath(pageUrl, requestService.request, cayenneService.newContext())
        if (node) {
            return WebNodeToPage.valueOf(node).page
        } else {
            String lowerCaseUrl = pageUrl.toLowerCase()
            PageIdentifier identifier = getPageIdentifierByPath(lowerCaseUrl)
            // Throw forbidden exception  if URL matches PageNotFound or any other reserved page. 
            // Throw client exception if URL matches Page or Home pattern - this URLs should has corresponded WebNodes in DB (see first condition)
            // Throw client exception if URL doesn't match of any reserved page.
            if (PageNotFound.getMatcher().matches(lowerCaseUrl) || !(identifier in [PageNotFound, Home, PageIdentifier.Page])) {
                throw new ForbiddenException("Attempting to get special system page.")
            }
            throw createClientException("There are no pages for provided url. Url: $pageUrl")
        }
    }
    
    List<Page> pageListGet() {
        return WebNodeFunctions.getNodes(requestService.request, cayenneService.newContext())
                .collect { node -> WebNodeToPage.valueOf(node).page }
    }
    
    Page pageUpdatePost(Page pageParams) {
        ObjectContext context = cayenneService.newContext()
        UpdatePage updater = UpdatePage.valueOf(pageParams, context, requestService.request).update()
        if (updater.error) {
            context.rollbackChanges()
            throw createClientException(updater.error)
        }
        context.commitChanges()
        return pageParams
    }
    
    private ClientErrorException createClientException(String message) {
        logger.error("$message, server name: $requestService.request.serverName")
        new ClientErrorException(Response.status(400).entity(new CommonError(message: message)).build())
    }
}

