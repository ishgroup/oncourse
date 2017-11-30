package ish.oncourse.willow.editor.service.impl

import com.google.inject.Inject
import ish.oncourse.model.WebMenu
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.willow.editor.rest.WebMenuToMenuItem
import ish.oncourse.willow.editor.service.*
import ish.oncourse.willow.editor.services.RequestService
import ish.oncourse.willow.editor.website.WebMenuFunctions
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import ish.oncourse.willow.editor.model.MenuItem

import groovy.transform.CompileStatic

@CompileStatic
class MenuApiServiceImpl implements MenuApi {

    private static Logger logger = LogManager.logger

    private ICayenneService cayenneService
    private RequestService requestService

    @Inject
    PageApiServiceImpl(ICayenneService cayenneService, RequestService requestService) {
        this.cayenneService = cayenneService
        this.requestService = requestService
    }
    
    List<MenuItem> getMenuItems() {
        WebMenuFunctions.getTopLelevMenus(requestService.request, cayenneService.newContext())
                .collect { menu -> WebMenuToMenuItem.valueOf(menu).menuItem }
    }
    
    List<MenuItem> saveMenuItems(List<MenuItem> menu) {
        // TODO: Implement...
        
        return null
    }
    
}

