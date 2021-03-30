package ish.oncourse.willow.editor.v1.service.impl

import com.google.inject.Inject
import ish.oncourse.model.WebNodeType
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.willow.editor.v1.model.Layout
import ish.oncourse.willow.editor.rest.UpdateTheme
import ish.oncourse.willow.editor.rest.WebNodeTypeToTheme
import ish.oncourse.willow.editor.v1.model.Theme

import groovy.transform.CompileStatic
import ish.oncourse.api.request.RequestService
import ish.oncourse.willow.editor.v1.model.UnexpectedError
import ish.oncourse.willow.editor.v1.service.ThemeApi
import ish.oncourse.willow.editor.website.WebNodeTypeFunctions
import ish.oncourse.willow.editor.website.WebSiteLayoutFunctions
import org.apache.cayenne.ObjectContext
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.ws.rs.ClientErrorException
import javax.ws.rs.core.Response

@CompileStatic
class ThemeApiServiceImpl implements ThemeApi {
    private static Logger logger = LogManager.logger

    private ICayenneService cayenneService
    private RequestService requestService

    @Inject
    ThemeApiServiceImpl(ICayenneService cayenneService, RequestService requestService) {
        this.cayenneService = cayenneService
        this.requestService = requestService
    }

    Theme createTheme() {
        ObjectContext context = cayenneService.newContext()
        WebNodeType webNodeType = WebNodeTypeFunctions.createNewWebNodeType(requestService.request, context)
        context.commitChanges()
        return WebNodeTypeToTheme.valueOf(webNodeType).theme
    }
    
    void deleteTheme(String id) {
        ObjectContext context = cayenneService.newContext()
        WebNodeType theme = WebNodeTypeFunctions.getWebNodeTypeById(id.toLong(), requestService.request, context)
        if (!theme) {
            throw createClientException("There is no theme for id: $id")
        }
        if (theme.defaultPageTheme) {
            throw createClientException("The default theme can't be deleted")
        }

        context.deleteObjects(theme.webContentVisibilities)
        context.deleteObjects(theme.webLayoutPaths)

        context.deleteObject(theme)
        context.commitChanges()
    }
    
    List<Layout> getLayouts() {
        WebSiteLayoutFunctions.getLayouts(requestService.request, cayenneService.newContext())
                .collect { webLayout -> new  Layout().with { layout ->
                    layout.id = webLayout.id.intValue()
                    layout.title = webLayout.layoutKey
                    layout
            }
        }
    }
    
    List<Theme> getThemes() {
        WebNodeTypeFunctions.getWebNodeTypes(requestService.request, cayenneService.newContext())
                .collect { theme -> WebNodeTypeToTheme.valueOf(theme).theme }
    }
    
    Theme updateTheme(Theme saveThemeRequest)  {
        ObjectContext context = cayenneService.newContext()
        UpdateTheme updater = UpdateTheme.valueOf(saveThemeRequest, context, requestService.request).update()
        if (updater.error) {
            context.rollbackChanges()
            throw createClientException(updater.error)
        }
        context.commitChanges()
        return saveThemeRequest
    }

    private ClientErrorException createClientException(String message) {
        logger.error("$message, server name: $requestService.request.serverName")
        new ClientErrorException(Response.status(400).entity(new UnexpectedError(message)).build())
    }
    
}

