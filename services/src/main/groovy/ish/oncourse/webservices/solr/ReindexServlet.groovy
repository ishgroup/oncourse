package ish.oncourse.webservices.solr

import groovy.servlet.GroovyServlet
import ish.oncourse.model.WebSite
import ish.oncourse.solr.SolrCollection
import ish.oncourse.solr.reindex.ReindexClasses
import org.apache.cayenne.configuration.server.ServerRuntime
import org.apache.cayenne.query.ObjectSelect
import org.apache.commons.lang3.SerializationUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.solr.client.solrj.SolrClient
import org.apache.zookeeper.ZooKeeper
import org.apache.zookeeper.data.Stat

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

import static ish.oncourse.configuration.Configuration.AppProperty.ZK_HOST
import static ish.oncourse.configuration.Configuration.getValue
import static ish.oncourse.configuration.InitZKRootNode.*
import static ish.oncourse.solr.ReindexConstants.*
import static org.apache.zookeeper.CreateMode.PERSISTENT
import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE

class ReindexServlet extends GroovyServlet {

    private static Logger logger = LogManager.logger

    private SolrClient solrClient
    private ServerRuntime serverRuntime

    private ExecutorService executorService
    private ZooKeeper zk
    
    private static final int lockPeriod = 3


    ReindexServlet(SolrClient solrClient, ServerRuntime serverRuntime) {
        this.serverRuntime = serverRuntime
        this.solrClient = solrClient
        this.executorService = Executors.newCachedThreadPool()
        this.zk =  new ZooKeeper("${getValue(ZK_HOST)}${REINDEX_LOCK_NODE}", 20000, null)
                
    }
    
    @Override
    void service(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String collection = request.getParameter(PARAM_COLLECTION)
        
        if (!collection) {
            throw new IllegalArgumentException("Collection should be specified")
        }
        
        switch (collection) {
            case SolrCollection.classes.name():
                
                String siteKey = request.getParameter(PARAM_WEB_SITE)
                if (!siteKey) {
                    throw new IllegalArgumentException("SiteKey should be specified")
                }
                
                WebSite webSite = ObjectSelect.query(WebSite).where(WebSite.SITE_KEY.eq(siteKey)).selectOne(serverRuntime.newContext())
                if (!webSite) {
                    throw new IllegalArgumentException("Web site doesn't exist: $siteKey")
                }

                if (testLock(siteKey)) {
                    executorService.submit {
                        try {
                            logger.warn("Run classes reindex for $webSite.siteKey")
                            new ReindexClasses(serverRuntime.newContext(), solrClient, webSite).run()
                        } finally {
                            zk.delete("/$siteKey", -1)
                        }
                    }
                    response.writer.print('Ok')
                } else {
                    response.writer.print('Reindex in progress')
                }
                
                break
            default:
                throw new IllegalArgumentException("Unsupported cllection: $collection)")
            
        }
        
    }

    private boolean testLock(String siteKey) {

        String nodePath = "/$siteKey"

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
        
    }
    
    
}
