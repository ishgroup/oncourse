package ish.oncourse.willow.editor.v1.service.impl

import com.google.inject.Inject
import ish.oncourse.configuration.Configuration
import ish.oncourse.model.WebSite
import ish.oncourse.model.WebSiteVersion
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.services.site.GetDeployedVersion
import ish.oncourse.services.site.WebSitePublisher
import ish.oncourse.services.site.WebSiteVersionRevert
import ish.oncourse.services.site.WebSiteVersionsDelete
import ish.oncourse.willow.editor.EditorProperty
import ish.oncourse.willow.editor.rest.WebVersionToVersion
import ish.oncourse.willow.editor.v1.model.CommonError
import ish.oncourse.willow.editor.v1.model.Version

import groovy.transform.CompileStatic
import ish.oncourse.willow.editor.services.RequestService
import ish.oncourse.willow.editor.services.access.UserService
import ish.oncourse.willow.editor.v1.model.VersionStatus
import ish.oncourse.willow.editor.v1.service.VersionApi
import ish.oncourse.willow.editor.website.WebSiteFunctions
import ish.oncourse.willow.editor.website.WebSiteVersionFunctions
import org.apache.cayenne.ObjectContext
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.eclipse.jetty.server.Request

import javax.ws.rs.ClientErrorException
import javax.ws.rs.InternalServerErrorException
import javax.ws.rs.core.Response

@CompileStatic
class VersionApiServiceImpl implements VersionApi {

    private static Logger logger = LogManager.logger

    private ICayenneService cayenneService
    private RequestService requestService
    private UserService userService

    @Inject
    PublishApiServiceImpl(ICayenneService cayenneService, 
                          RequestService requestService, 
                          UserService userService) {
        this.cayenneService = cayenneService
        this.requestService = requestService
        this.userService = userService
    }

    List<Version> getVersions() {
        Request request = requestService.request
        return WebSiteVersionFunctions.getSiteVersions(request, cayenneService.newContext())
                .collect { webVersion ->
            WebVersionToVersion.valueOf(webVersion).version
        }
    }

    void updateVersion(String id, Version diff) {
        Request request = requestService.request
        ObjectContext context = cayenneService.newContext()
        
        VersionStatus status = diff.status
        if (!status) {
            throw createClientException('Version status is required')
        }
        
        WebSiteVersion version = WebSiteVersionFunctions.getVersionById(id.toLong(), request, context)
        if (!version) {
            throw createClientException("There is no version for provided id:$id")
        }

        WebSiteVersion draft = WebSiteVersionFunctions.getVersionById(id.toLong(), request, context)

        switch (status) {
            case VersionStatus.PUBLISHED:
                if (draft.id == version.id) {
                    publish()
                } else {
                    throw createClientException('You can not publish non draft version')
                }
                break
            case VersionStatus.DRAFT:
                if (draft.id == version.id) {
                    throw createClientException("Version (id:$id) is already draft")
                } else {
                    revert(version.id)
                }
                break
        }
    }

    private void publish() {
        Request request = requestService.request
        WebSiteVersion draftVersion = WebSiteVersionFunctions.getCurrentVersion(request, cayenneService.newContext())

        logger.warn("Start to publish: ${request.serverName}, draft version id: ${draftVersion.id}," +
                " started by: ${userService.userFirstName}  ${userService.userLastName}")

        Long time = System.currentTimeMillis()
        WebSitePublisher.valueOf(Configuration.getValue(EditorProperty.DEPLOY_SCRIPT_PATH), draftVersion,
                userService.systemUser,
                userService.userEmail,
                cayenneService.newContext()).publish()

        //refresh draft version after publishing
        draftVersion = WebSiteVersionFunctions.getCurrentVersion(request, cayenneService.newContext())
        WebSite webSite = WebSiteFunctions.getCurrentWebSite(request, cayenneService.newContext())
        WebSiteVersion newVersion = GetDeployedVersion.valueOf(cayenneService.newContext(), webSite, false).get()
        WebSiteVersionsDelete.valueOf(webSite, draftVersion, newVersion,
                cayenneService.newContext()).delete()
        
        logger.warn("Site publishing finished successfully:${request.serverName} from draft version id: ${draftVersion.id}," +
                " new version id:${newVersion.id}, took: ${ System.currentTimeMillis() - time} milliseconds")
    }



    private void revert(Long id) {
        Request request = requestService.request
        ObjectContext context = cayenneService.newContext()

        WebSiteVersion draftVersion = WebSiteVersionFunctions.getCurrentVersion(request, context)
        WebSiteVersion sourceVersion = WebSiteVersionFunctions.getVersionById(id.toLong(), request, context)

        try {
            cayenneService.performTransaction {
                WebSiteVersionRevert.valueOf(draftVersion, sourceVersion, context).revert()
            }
        } catch (Exception e) {
            logger.error(e)
            context.rollbackChanges()
            throw new InternalServerErrorException()
        }
    }
    
    private ClientErrorException createClientException(String message) {
        logger.error("$message, server name: $requestService.request.serverName")
        new ClientErrorException(Response.status(400).entity(new CommonError(message: message)).build())
    }
}

