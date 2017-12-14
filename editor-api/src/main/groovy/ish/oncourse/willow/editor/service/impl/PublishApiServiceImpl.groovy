package ish.oncourse.willow.editor.service.impl

import com.google.inject.Inject
import ish.oncourse.configuration.Configuration
import ish.oncourse.model.SystemUser
import ish.oncourse.model.WebSite
import ish.oncourse.model.WebSiteVersion
import ish.oncourse.model.WillowUser
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.services.site.GetDeployedVersion
import ish.oncourse.services.site.WebSitePublisher
import ish.oncourse.services.site.WebSiteVersionsDelete
import ish.oncourse.willow.editor.EditorProperty
import ish.oncourse.willow.editor.service.*
import ish.oncourse.willow.editor.model.Version
import ish.oncourse.willow.editor.model.api.SetVersionRequest

import groovy.transform.CompileStatic
import ish.oncourse.willow.editor.services.RequestService
import ish.oncourse.willow.editor.services.access.AuthenticationService
import ish.oncourse.willow.editor.website.WebSiteFunctions
import ish.oncourse.willow.editor.website.WebSiteVersionFunctions
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.eclipse.jetty.server.Request

import javax.ws.rs.ClientErrorException
import javax.ws.rs.InternalServerErrorException
import javax.ws.rs.NotAllowedException
import javax.ws.rs.NotSupportedException
import javax.ws.rs.ServerErrorException
import javax.ws.rs.core.Response

@CompileStatic
class PublishApiServiceImpl implements PublishApi {

    private static Logger logger = LogManager.logger

    private ICayenneService cayenneService
    private RequestService requestService
    private AuthenticationService authenticationService

    @Inject
    PublishApiServiceImpl(ICayenneService cayenneService, RequestService requestService, AuthenticationService authenticationService) {
        this.cayenneService = cayenneService
        this.requestService = requestService
        this.authenticationService = authenticationService
    }

    void publish() {
        Request request = requestService.request
        WebSiteVersion draftVersion = WebSiteVersionFunctions.getCurrentVersion(request, cayenneService.newContext())
        SystemUser user = authenticationService.getSystemUser()
        WillowUser willowUser = authenticationService.getWillowUser()

        logger.warn("Start to publish: ${request.serverName}, draft version id: ${draftVersion.id}," +
                " started by: ${user? user.firstName + ' ' + user.surname : willowUser.firstName + ' ' + willowUser.lastName }")

        Long time = System.currentTimeMillis()
        WebSitePublisher.valueOf(Configuration.getValue(EditorProperty.DEPLOY_SCRIPT_PATH), draftVersion,
                user,
                authenticationService.userEmail,
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
    
    List<Version> getVersions() {
        throw new ServerErrorException(Response.Status.NOT_IMPLEMENTED)
    }
    
    void setVersion(SetVersionRequest setVersionRequest) {
        throw new NotAllowedException('publish')
    }
    
}

