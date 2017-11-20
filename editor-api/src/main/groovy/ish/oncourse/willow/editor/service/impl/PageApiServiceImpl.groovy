package ish.oncourse.willow.editor.service.impl

import com.google.inject.Inject
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.willow.editor.service.*
import ish.oncourse.willow.editor.model.Page
import ish.oncourse.willow.editor.model.api.PageRenderResponse

import groovy.transform.CompileStatic
import ish.oncourse.willow.editor.services.RequestService

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
        return null
    }
    
    void deletePage(Double id) {
        // TODO: Implement...
        
        
    }
    
    Page getPageByUrl(String pageUrl) {
        // TODO: Implement...
        
        return null
    }
    
    PageRenderResponse getPageRender(Double pageId) {
        // TODO: Implement...
        
        return null
    }
    
    List<Page> getPages() {
        // TODO: Implement...
        
        return null
    }
    
    Page savePage(Page pageParams) {
        // TODO: Implement...
        
        return null
    }
    
}

