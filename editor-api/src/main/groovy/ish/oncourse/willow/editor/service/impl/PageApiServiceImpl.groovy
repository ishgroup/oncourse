package ish.oncourse.willow.editor.service.impl

import com.google.inject.Inject
import ish.oncourse.model.WebNode
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.willow.editor.rest.WebNodeToPage
import ish.oncourse.willow.editor.service.*
import ish.oncourse.willow.editor.model.Page
import ish.oncourse.willow.editor.model.api.PageRenderResponse

import groovy.transform.CompileStatic
import ish.oncourse.willow.editor.services.RequestService
import ish.oncourse.willow.editor.website.WebNodeFunctions
import org.apache.cayenne.ObjectContext

@CompileStatic
class PageApiServiceImpl implements PageApi {

    private ICayenneService cayenneService
    private RequestService requestService

    @Inject
    PageApiServiceImpl(ICayenneService cayenneService, RequestService requestService) {
        this.cayenneService = cayenneService
        this.requestService = requestService
    }

    Page addPage() {
        ObjectContext context = cayenneService.newContext()
        WebNode node = WebNodeFunctions.createNewNode(requestService.request, cayenneService.newContext())
        context.commitChanges()

        return WebNodeToPage.valueOf(node).page
    }
    
    void deletePage(Double id) {
        // TODO: Implement...
        
        
    }
    
    Page getPageByUrl(String pageUrl) {
        WebNode node = WebNodeFunctions.getNodeByPath(pageUrl, requestService.request, cayenneService.newContext())

        return null
    }
    
    PageRenderResponse getPageRender(Double pageId) {
        
        return null
    }
    
    List<Page> getPages() {
        return WebNodeFunctions.getNodes(requestService.request, cayenneService.newContext())
                .collect { node -> WebNodeToPage.valueOf(node).page }
    }
    
    Page savePage(Page pageParams) {
        // TODO: Implement...
        
        return null
    }
    
}

