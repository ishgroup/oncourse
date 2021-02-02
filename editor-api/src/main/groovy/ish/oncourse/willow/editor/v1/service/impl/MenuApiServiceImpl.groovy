package ish.oncourse.willow.editor.v1.service.impl

import com.google.inject.Inject
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.willow.editor.rest.UpdateMenu
import ish.oncourse.willow.editor.rest.WebMenuToMenuItem
import ish.oncourse.api.request.RequestService
import ish.oncourse.willow.editor.v1.service.MenuApi
import ish.oncourse.willow.editor.website.WebMenuFunctions
import org.apache.cayenne.ObjectContext
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import ish.oncourse.willow.editor.v1.model.MenuItem

import groovy.transform.CompileStatic
import org.eclipse.jetty.server.Request

import javax.ws.rs.ClientErrorException
import javax.ws.rs.core.Response

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
    
    List<MenuItem> getMenus() {
        WebMenuFunctions.getTopLelevMenus(requestService.request, cayenneService.newContext())
                .collect { menu -> WebMenuToMenuItem.valueOf(menu).menuItem }
    }
    
    List<MenuItem> updateMenus(List<MenuItem> menus) {
        ObjectContext context = cayenneService.newContext()
        Request request = requestService.request
        UpdateMenu updater = UpdateMenu.valueOf(menus, context, request).update()
        if (!updater.errors.empty) {
            context.rollbackChanges()
            String message = updater.errors.join('\n')
            throw new ClientErrorException(message, Response.status(Response.Status.BAD_REQUEST).entity(menus).build())
        }
        context.commitChanges()
        return menus
    }
}

