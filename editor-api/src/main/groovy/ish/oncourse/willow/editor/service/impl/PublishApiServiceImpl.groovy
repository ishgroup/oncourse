package ish.oncourse.willow.editor.service.impl

import com.google.inject.Inject
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.willow.editor.service.*
import ish.oncourse.willow.editor.model.Version
import ish.oncourse.willow.editor.model.api.SetVersionRequest

import groovy.transform.CompileStatic
import ish.oncourse.willow.editor.services.RequestService
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@CompileStatic
class PublishApiServiceImpl implements PublishApi {

    private static Logger logger = LogManager.logger

    private ICayenneService cayenneService
    private RequestService requestService

    @Inject
    PublishApiServiceImpl(ICayenneService cayenneService, RequestService requestService) {
        this.cayenneService = cayenneService
        this.requestService = requestService
    }
    
    List<Version> getVersions() {
        // TODO: Implement...
        
        return null
    }
    
    void publish() {
        // TODO: Implement...
        
        
    }
    
    void setVersion(SetVersionRequest setVersionRequest) {
        // TODO: Implement...
        
        
    }
    
}

