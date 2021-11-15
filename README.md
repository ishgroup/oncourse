#How to check current deployed version of willow apps

Each willow app has  /ISHHealthCheck servlet implemented, so to check current app version just open followed URL 

services:
https://secure-payment.oncourse.net.au/services/ISHHealthCheck

usi:
https://secure-payment.oncourse.net.au/usi/ISHHealthCheck

web:
https://template-a.oncourse.cc/ISHHealthCheck

checkout-api:
https://template-a.oncourse.cc/a/ISHHealthCheck

editor-api:
https://template-a.oncourse.cc/editor/ISHHealthCheck

billing-api:
https://provisioning.ish.com.au/b/ISHHealthCheck

new portal:
https://www.skillsoncourse.com.au/p/ISHHealthCheck

old portal:
https://www.skillsoncourse.com.au/portal/ISHHealthCheck

Ract apps could be checked by:

checkout:
https://template-a.oncourse.cc/s/oncourse-releases/checkout/stable/dynamic.js
and look for APP_VERSION variable

editor:
https://template-a.oncourse.cc/s/oncourse-releases/editor/stable/editor.js

#run willow app
lets review unexample of admin app
Admin, as an almost all app, require zk srver, so
1. unzip solr-7.zip (se root dir) to any dir on your mashine
2. Run zk cluster by followed command
`{zkDir}/zk.sh start`
3. Copy config file example

    from /admin/src/dist/application.properties
   
    to /admin/application.properties
4. Change db props to your ones (db_user, db_pass ,db_url)
5. Run admin via intellij run congiguration

    main class: ish.oncourse.admin.AdminApp

    working dir: /admin
6. http://127.0.0.1/8306/willowAdmin

#Zookepper usage on willow

All most all app use ZK for different approaches. There are 2 main ways of usage:
   
   - Pass zk host as a param to build SolrClient: note that we have a cluster of solr instances. ZK istance need to
organise solr cluster, it keeps a clister config and allowe to route solr query requests.

   - Use ZK nodes directly for different reasons


   1. Web App:
      - Use to create solr cloud cliennt to make solr search requests, see:
      ```
         ish.oncourse.solr.BuildSolrClient
         ish.oncourse.services.search.SearchService
      ```
      - Listen special ZK node changes to invalidate web site templates cache for certain web site:
      ```
          ish.oncourse.website.cache.CacheInvalidationService
      ```
      zk node example:

      path: [/willow/publish]()

      data: [template-a]


   2. Services app
   
      - Use to create solr cloud cliennt to update solr indexes:
      ```
        ish.oncourse.webservices.quartz.job.solr.AReindexCollectionJob
      ```
      - Use special zk node value as lock flag for reindex action (preve parallel execution) 
      ``` 
         ish.oncourse.webservices.solr.ReindexServlet
      ```
      zk node example:
   
      path: [/willow/reindexLock/template-a]()
   
      data: no data used, just check for node existance
         
      Note that feature currently disabled.
      ```
         ish.oncourse.webservices.ServicesModule
      
      //	@Singleton
      //	@Provides
      //	MappedServlet<ReindexServlet> createReindexServlet(SolrClient solrClient, ZooKeeper zk, ServerRuntime serverRuntime) {
      //		return new MappedServlet<>(new ReindexServlet(solrClient, zk, serverRuntime), Collections.singleton(ReindexConstants.REINDEX_PATH));
      //	}
      ```
      
   3. Portal app
      
      - use zk nodes to handle logged user sessions, also implement 'portal access delegation' feature, see:
      ```
        ish.oncourse.portal.access.ZKSessionManager
      ```
      zk node example:

      contact (id==200) has no active session (logged out):                                      [/willow/sessions/200]()

      contact (id==200) has  active session:                                                     [/willow/sessions/200/he64meosmee2uu3mpwdccmo23]()

      contact (id==200) has  active session and use delegation access to contact with id == 220: [/willow/sessions/200/he64meosmee2uu3mpwdccmo23/220]()

   4. new Portal app (portal-api)

      - use zk nodes to handle logged in user sessions (no access delegation implemented yet)
      ```
        ish.oncourse.willow.portal.auth.ZKSessionManager
      ```
      zk node example:

      user (id==200) has no active session (logged out): [/willow/portal/User-200]()

      user (id==200) has  active session:                [/willow/portal/User-200/he64meosmee2uu3mpwdccmo23]()

   5. Billing (billing-api)
      
      - use zk nodes to handle sessions for Guest users (case when user try to create new onCourse) after recapcha check
      ```
      ish.oncourse.willow.billing.filter.GuestSessionFilter
      ```
      zk node example,Guest-1 part always the same for any guest, just session id (last 20 chars) is different:

      path: [/willow/portal/Guest-1/he64meosmee2uu3mpwdccmo23]()
      
      That node removing imideatelly after guest finised onCourse creeation.

      - use zk nodes to handle regulat SystemUser sessions
       ```
      ish.oncourse.willow.billing.filter.UserSessionFilter
      ```
      zk node example for system user wit id==200 

      path: [/willow/portal/SystemUser-200/he64meosmee2uu3mpwdccmo23]()

   6. Editor (editor-api):
      
      - handl editor login for SystemUsers
      ```
      ish.oncourse.willow.editor.services.access.AuthenticationService
      ```
      node example: [/willow/editorSessions/SystemUser-200/he64meosmee2uu3mpwdccmo23]()

      - handl editor login for WillowUsers
        
      node example: [/willow/editorSessions/WillowUser-200/he64meosmee2uu3mpwdccmo23]()
      
      - use zk node value (update node with specific data) to trigger wb site cache cleanup (immidetelly after web site published)
      ```
      ish.oncourse.willow.editor.v1.service.impl.VersionApiServiceImpl.cleanWebappServiceCache()
      ```
      zk node example:

      path: [/willow/publish]()

      data: [template-a]

      - usezk node as lock flag to preven cuncyrent publishing of the same site
      ```
      ish.oncourse.willow.editor.v1.service.impl.VersionApiServiceImpl.updateLock()
      ```
      zk node example:

      path: [/willow/editorLock/template-a]()

      if node prsent that means web site in middle of publishing process right now

   
   ##summary list of zk root nodes   

   [/willow]() - root of roots, all willow data under that path
     
   [/willow/sessions]() - old portal authorization
      
   [/willow/editorSessions]() - editor authorization
     
   [/willow/editorLock]() - prevent cancurent website publishing
     
   [/willow/reindexLock]() - prevent cancurent solr reindexing (trihher through URL call) - that featur disabled now 
      
   [/willow/billing]() - billing authorization, include guest sessions on provisioning 
      
   [/willow/publish]() - cache cleanup mchanism, ditor set the value for that node, web listen for changes   
      
   [/willow/portal]() - new portal authorization