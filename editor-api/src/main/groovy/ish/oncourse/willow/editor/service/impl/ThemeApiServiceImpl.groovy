package ish.oncourse.willow.editor.service.impl

import ish.oncourse.model.WebNodeType
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.willow.editor.rest.WebNodeTypeToTheme
import ish.oncourse.willow.editor.service.*
import ish.oncourse.willow.editor.model.Theme
import ish.oncourse.willow.editor.model.common.CommonError

import groovy.transform.CompileStatic
import ish.oncourse.willow.editor.services.RequestService
import ish.oncourse.willow.editor.website.WebNodeTypeFunctions
import org.apache.cayenne.ObjectContext
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@CompileStatic
class ThemeApiServiceImpl implements ThemeApi {
    private static Logger logger = LogManager.logger

    private ICayenneService cayenneService
    private RequestService requestService

    ThemeApiServiceImpl(ICayenneService cayenneService, RequestService requestService) {
        this.cayenneService = cayenneService
        this.requestService = requestService
    }

    Theme addTheme() {
        ObjectContext context = cayenneService.newContext()
        WebNodeType webNodeType = WebNodeTypeFunctions.createNewWebNodeType(requestService.request, context)
        context.commitChanges()
        return WebNodeTypeToTheme.valueOf(webNodeType).theme
    }
    
    void deleteTheme(Double id) {
        // TODO: Implement...
        
        
    }
    
    List<Theme> getLayouts() {
        // TODO: Implement...
        
        return null
    }
    
    List<Theme> getThemes() {
//        WebNodeTypeFunctions.getWebNodeTypes(requestService.request, cayenneService.newContext())
//                .collect { node -> WebNodeTypeToTheme.valueOf(node).theme }
    }
    
    Theme saveTheme(Theme saveThemeRequest) {
        // TODO: Implement...
        
        return null
    }
    
}

