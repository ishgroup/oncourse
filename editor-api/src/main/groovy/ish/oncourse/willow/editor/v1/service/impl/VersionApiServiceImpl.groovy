package ish.oncourse.willow.editor.v1.service.impl

import com.google.inject.Inject
import ish.oncourse.configuration.Configuration
import ish.oncourse.model.SystemUser
import ish.oncourse.model.WebSite
import ish.oncourse.model.WebSiteVersion
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.services.site.DeleteVersion
import ish.oncourse.services.site.GetDeployedVersion
import ish.oncourse.services.site.GetNextSiteVersion
import ish.oncourse.services.site.WebSitePublisher
import ish.oncourse.services.site.WebSiteVersionCopy
import ish.oncourse.services.site.WebSiteVersionsDelete
import ish.oncourse.solr.SolrCollection
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
import org.apache.zookeeper.ZooKeeper
import org.apache.zookeeper.data.Stat
import org.eclipse.jetty.server.Request

import javax.ws.rs.ClientErrorException
import javax.ws.rs.InternalServerErrorException
import javax.ws.rs.core.Response
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

import static ish.oncourse.cayenne.cache.ICacheEnabledService.CacheDisableReason.PUBLISHER
import static ish.oncourse.configuration.InitZKRootNode.EDITOR_LOCK_NODE
import static ish.oncourse.solr.ReindexConstants.*
import static ish.oncourse.configuration.Configuration.AdminProperty.DEPLOY_SCRIPT_PATH
import static ish.oncourse.willow.editor.EditorProperty.SERVICES_LOCATION
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
    private String deployScriptPath
    private String servicesUrl


    @Inject
    PublishApiServiceImpl(ICayenneService cayenneService, 
                          RequestService requestService, 
                          UserService userService, 
                          ZKProvider provider) {
        this.cayenneService = cayenneService
        this.requestService = requestService
        this.userService = userService
        this.zkProvider = provider
        this.executorService = Executors.newCachedThreadPool()
        this.deployScriptPath = Configuration.getValue(DEPLOY_SCRIPT_PATH)
        this.servicesUrl = Configuration.getValue(SERVICES_LOCATION)

    }

    List<Version> getVersions() {
        
        ObjectContext context = cayenneService.newContext() 
        Request request = requestService.request
        List<Version> versions = WebSiteVersionFunctions.getSiteVersions(request, context)
                .collect { webVersion ->
            WebVersionToVersion.valueOf(webVersion).version
        }

        WebSiteVersion draftVersion = WebSiteVersionFunctions.getCurrentVersion(request, context)
        WebSiteVersion deployedVersion = WebSiteVersionFunctions.getDeployedVersion(request, context)

        versions.find { it.id  == draftVersion.siteVersion.toInteger()}.status = VersionStatus.DRAFT
        versions.find { it.id  == deployedVersion.siteVersion.toInteger()}.status = VersionStatus.PUBLISHED
        versions
    }

    void updateVersion(String id, Version diff) {

        Request request = requestService.request
        ObjectContext context = cayenneService.newContext()
        
        WebSite webSite = WebSiteFunctions.getCurrentWebSite(request, context)

        if (!updateLock(webSite.siteKey)) {
            throw new ClientErrorException(Response.status(423).build())
        }
        
        VersionStatus status = diff.status
        if (!status) {
            throw createClientException('Version status is required')
        }
        
        WebSiteVersion version = WebSiteVersionFunctions.getVersionBy(id.toLong(), request, context)
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
                    revert(version.siteVersion)
                }
                break
        }
    }

    private void publish() {
        
        Request request = requestService.request
        WebSite webSite = WebSiteFunctions.getCurrentWebSite(request, cayenneService.newContext())
        SystemUser user = userService.systemUser
        String userName = "${userService.userFirstName}  ${userService.userLastName}"
        String userEmail = userService.userEmail
        String serverName = request.serverName
        WebSiteVersion draftVersion = WebSiteVersionFunctions.getCurrentVersion(request, cayenneService.newContext())
        URL webappServiceUrl = new URL(request.scheme, request.serverName, request.serverPort, "")

        executorService.submit {
            try {
                logger.warn("Start to publish: $serverName, draft version id: ${draftVersion.id}, started by: $userName")
                Long time = System.currentTimeMillis()

                WebSitePublisher.valueOf(deployScriptPath, draftVersion, user, userEmail, cayenneService.newContext()).publish()

                //refresh draft version after publishing
                WebSiteVersion freshDraft = WebSiteVersionFunctions.getCurrentVersion(webSite, cayenneService.newContext())

                WebSiteVersion newVersion = GetDeployedVersion.valueOf(cayenneService.newContext(), webSite, false).get()
                WebSiteVersionsDelete.valueOf(webSite, freshDraft, newVersion, cayenneService.newContext()).delete()

                logger.warn("Site publishing finished successfully: $serverName from draft version id: ${freshDraft.id}," +
                        " new version id:${newVersion.id}, took: ${System.currentTimeMillis() - time} milliseconds")

                logger.warn("Run cache clean for $webSite.siteKey")
                cleanWebappServiceCache(webappServiceUrl)

                logger.warn("Run classes reindex for $webSite.siteKey")
                new URL("$servicesUrl${REINDEX_PATH}?${PARAM_COLLECTION}=${SolrCollection.classes.name()}&${PARAM_WEB_SITE}=${webSite.siteKey}").text


            } catch (Exception e) {
                logger.error("Something unexpected has happened, publish was not completed. See error message for details")
                logger.catching(e)
            }
        }
 
    }

    private void cleanWebappServiceCache(URL serviceUrl) {
        URLConnection conn = serviceUrl.openConnection()
        conn.setRequestProperty("Cookie", "${PUBLISHER.toString().toLowerCase()}=true")
        if (conn.content instanceof InputStream) {
            (new BufferedReader(new InputStreamReader(conn.content as InputStream))).text
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
        WebSiteVersion sourceVersion = WebSiteVersionFunctions.getVersionBy(id.toLong(), request, context)

        try {
            DeleteVersion.valueOf(draftVersion, context, true).delete()
            draftVersion.setSiteVersion(sourceVersion.getSiteVersion())
            sourceVersion.setSiteVersion(GetNextSiteVersion.valueOf(context, sourceVersion.getWebSite()).get())
            context.commitChanges()
            WebSiteVersionCopy.valueOf(context, sourceVersion, draftVersion).copyContent()
        } catch (Exception e) {
            logger.catching(e)
            context.rollbackChanges()
            throw new InternalServerErrorException()
        }
    }
    
    private ClientErrorException createClientException(String message) {
        logger.error("$message, server name: $requestService.request.serverName")
        new ClientErrorException(Response.status(400).entity(new UnexpectedError(message)).build())
    }
}

