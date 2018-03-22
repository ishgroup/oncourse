package ish.oncourse.willow.editor.v1.service.impl

import com.google.inject.Inject
import ish.oncourse.configuration.Configuration
import ish.oncourse.configuration.InitZKRootNode
import ish.oncourse.model.WebSite
import ish.oncourse.model.WebSiteVersion
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.services.site.GetDeployedVersion
import ish.oncourse.services.site.WebSitePublisher
import ish.oncourse.services.site.WebSiteVersionRevert
import ish.oncourse.services.site.WebSiteVersionsDelete
import ish.oncourse.willow.editor.EditorProperty
import ish.oncourse.willow.editor.rest.WebVersionToVersion
import ish.oncourse.willow.editor.services.ZKProvider
import ish.oncourse.willow.editor.v1.model.UnexpectedError
import ish.oncourse.willow.editor.v1.model.Version

import groovy.transform.CompileStatic
import ish.oncourse.willow.editor.services.RequestService
import ish.oncourse.willow.editor.services.access.UserService
import ish.oncourse.willow.editor.v1.model.VersionStatus
import ish.oncourse.willow.editor.v1.service.VersionApi
import ish.oncourse.willow.editor.website.WebSiteFunctions
import ish.oncourse.willow.editor.website.WebSiteVersionFunctions
import org.apache.cayenne.ObjectContext
import org.apache.commons.lang3.SerializationUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.zookeeper.CreateMode
import org.apache.zookeeper.ZooDefs
import org.apache.zookeeper.ZooKeeper
import org.apache.zookeeper.data.Stat
import org.eclipse.jetty.server.Request

import javax.ws.rs.ClientErrorException
import javax.ws.rs.InternalServerErrorException
import javax.ws.rs.core.Response
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

import static ish.oncourse.configuration.InitZKRootNode.EDITOR_LOCK_NODE
import static ish.oncourse.willow.editor.EditorProperty.DEPLOY_SCRIPT_PATH
import static org.apache.zookeeper.CreateMode.PERSISTENT
import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE

@CompileStatic
class VersionApiServiceImpl implements VersionApi {

    private static Logger logger = LogManager.logger
    private static final long lockPeriod = 3

    private ICayenneService cayenneService
    private RequestService requestService
    private UserService userService
    private ZKProvider zkProvider
    private ExecutorService executorService

    @Inject
    PublishApiServiceImpl(ICayenneService cayenneService, 
                          RequestService requestService, 
                          UserService userService, 
                          ZKProvider provider) {
        this.cayenneService = cayenneService
        this.requestService = requestService
        this.userService = userService
        this.zkProvider = provider
        executorService = Executors.newCachedThreadPool()
    }

    List<Version> getVersions() {
        
        ObjectContext context = cayenneService.newContext() 
        Request request = requestService.request
        List<Version> versions = WebSiteVersionFunctions.getSiteVersions(request, context)
                .collect { webVersion ->
            WebVersionToVersion.valueOf(webVersion).version
        }

        WebSiteVersion draftVersion = WebSiteVersionFunctions.getCurrentVersion(request, context)
        WebSiteVersion beployedVersion = WebSiteVersionFunctions.getDeployedVersion(request, context)

        versions.find { it.id  == draftVersion.id.toInteger()}.status = VersionStatus.DRAFT
        versions.find { it.id  == beployedVersion.id.toInteger()}.status = VersionStatus.PUBLISHED
        versions
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

        WebSiteVersion draft = WebSiteVersionFunctions.getCurrentVersion(request, context)

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
        WebSite webSite = WebSiteFunctions.getCurrentWebSite(request, cayenneService.newContext())

        if (!updateLock(webSite.siteKey)) {
           throw new ClientErrorException(Response.status(423).build())
        }

        executorService.submit {

            WebSiteVersion draftVersion = WebSiteVersionFunctions.getCurrentVersion(request, cayenneService.newContext())

            logger.warn("Start to publish: ${request.serverName}, draft version id: ${draftVersion.id}," +
                    " started by: ${userService.userFirstName}  ${userService.userLastName}")

            Long time = System.currentTimeMillis()
            WebSitePublisher.valueOf(Configuration.getValue(DEPLOY_SCRIPT_PATH), draftVersion,
                    userService.systemUser,
                    userService.userEmail,
                    cayenneService.newContext()).publish()

            //refresh draft version after publishing
            draftVersion = WebSiteVersionFunctions.getCurrentVersion(request, cayenneService.newContext())

            WebSiteVersion newVersion = GetDeployedVersion.valueOf(cayenneService.newContext(), webSite, false).get()
            WebSiteVersionsDelete.valueOf(webSite, draftVersion, newVersion,
                    cayenneService.newContext()).delete()

            logger.warn("Site publishing finished successfully:${request.serverName} from draft version id: ${draftVersion.id}," +
                    " new version id:${newVersion.id}, took: ${ System.currentTimeMillis() - time} milliseconds")
        }
 
    }
    
    private boolean updateLock(String siteKey) {

        String nodePath = "/$siteKey"
        try {
            Stat stat = zk.exists(nodePath, false)
            if (stat) {
                byte[] data = zk.getData(nodePath, false, null)
                LocalDateTime lastLock = SerializationUtils.<LocalDateTime>deserialize(data)
                if (Duration.between(lastLock, LocalDateTime.now()).toMinutes() >= lockPeriod) {
                    zk.setData(nodePath, SerializationUtils.serialize(LocalDateTime.now()), stat.version)
                    return true
                }
                return false
            } else {
                zk.create(nodePath, SerializationUtils.serialize(LocalDateTime.now()), OPEN_ACL_UNSAFE, PERSISTENT)
                return true
            }
        } catch (Exception e) {
            logger.error("Fail to check lock for site: $siteKey", e)
            return true
        }
        
    }
    
    private ZooKeeper getZk() {
        return zkProvider.getZk(EDITOR_LOCK_NODE)
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
        new ClientErrorException(Response.status(400).entity(new UnexpectedError(message)).build())
    }
}

