#Cache
We have two types of cache

1. website template cache - that is query cache that has cache groups per web site. It has no expiry settings and we invalidate it only on website publish using ZK watcher

see ish.oncourse.website.cache.CacheInvalidationService


2. other cayenne object cache, which is regular object cache. Works for courses, classes and other site content entities

We have followed config

TTL == 10 min

cache size = 10000 entries

see  ish.oncourse.cache.caffeine.CaffeineFactory

#Prerendered html cache for class item 

###Problem 
Rendering of /courses page takes significan time. 
Significant part takes rendering of 
[common/common-templates/src/main/resources/ish/oncourse/ui/components/CourseItem.tml]()
that comprises number of  
[common/common-templates/src/main/resources/ish/oncourse/ui/components/CourseClassItem.tml]()

To render each class item we make lots of db queris to build timetable, price, discounts, tutors, venue, description.
Additional time we also spend to convert reach text of class description.

###Main goal
Incease load speed of /courses page.

###Idea
To improve performance we would save pre rendered html on each class item and use it instead of render tapestry teplates each time (like a cach). Rebuild it if needed

###Implmented part
Note that we already implement the idea partially.

1. Solr collection to store rendered html and class id, see:
[common/solr-index/src/main/resources/solr/courses/conf/schema.xml]()
that part alrady done and applied for solr

2. Populate classes collection with actuall data in real time

    All that also implemented:

- as soon as class data changed trough replication - build new class index and put it to solr
    
    entrypoint same with course reindex:
    ```
    ish.oncourse.webservices.solr.SolrUpdateCourseDocumentsListener
    ```
    collect all classes for course and put it to corresponded collection:
    ```
    ish.oncourse.solr.reindex.ReindexCourses.addBean()
     for (SCourseClass c : sCourse.getClasses()) {
        solrClient.addBean(SolrCollection.classes.name(), c);
    }
    ```
- render html content for each class using special url to web app

[http://{siteKey}.oncourse.cc/ish.render?component=class&id={class.id}](https://127.0.0.1:8182/services)    ```
        ish.oncourse.solr.functions.course.GetSCourseClass.getContent()
    ```
- same class can has different html content for different college web site.
So we need to render class for each site individually and update solr index with corresponded siteId  

3. Reindex classes if web site *.tml templates has been changed, since class item html depend on tapestry templats
That part also done:
As soon as user publish certain web site 
the editor api make spcial srvices app URL invoation to reindex all classes for certain college/web site:

   [https://127.0.0.1:8182/services/ish.reindex?collection=classes&webSite={webSite.siteKey}]()
  ```
  ish.oncourse.willow.editor.v1.service.impl.VersionApiServiceImpl.publish()
  ```
Note that coresponded services app url handler currently disabled, see:
  ```
  ish.oncourse.webservices.ServicesModule
  //
//	@Singleton
//	@Provides
//	MappedServlet<ReindexServlet> createReindexServlet(SolrClient solrClient, ZooKeeper zk, ServerRuntime serverRuntime) {
//		return new MappedServlet<>(new ReindexServlet(solrClient, zk, serverRuntime), Collections.singleton(ReindexConstants.REINDEX_PATH));
//	}
  ```

4. render html cache:

  We already implemented special web page
```
  ish.oncourse.specialpages.SpecialWebPage.COURSES_SKELETON
```
And corresponded tml page/components and java classes.
```
Willow.common.common-templates.main
CourseItemSkeleton.tml
CoursesListSkeleton.tml
CoursesSkeleton.tml

CourseItemSkeleton.java
CourseItemSkeletonModel.java
CoursesListSkeleton.java
CoursesSkeleton.java
```



###Remained work see [https://ish-group.atlassian.net/browse/OD-10275](https://ish-group.atlassian.net/browse/OD-10275) jira epic link

1. Finish special page feature:

[https://ish-group.atlassian.net/browse/OD-12442](https://ish-group.atlassian.net/browse/OD-12442)

That allow us mount the new courses sceleton page to certain path and verify it.

2. Make sure `CoursesSkeleton` page has the same functionality as `Courses` page:

  - pagination on scrol down
  - collect classes venues on Google map
  - has exact content as old page
    [https://ish-group.atlassian.net/browse/OD-12060](https://ish-group.atlassian.net/browse/OD-12060)


3. make sure that targed classes comes from solr instead of selecting them from db. old web app doing it here:
  ```
  ish.oncourse.ui.utils.CourseItemModel.fullFillClasses()
  ```
The method body should complitelly be replaced with solr search queries:
- all solr cclasses already web visible (web invisible classes already filtered out on indexing stage)
- filter out classes that not sutisfy 'hideClassOnWebAge'/'stopWebEnrolmentsAge' criterias
- filter out classes that did not sutisfy other search criterias
  [https://ish-group.atlassian.net/browse/OD-12458](https://ish-group.atlassian.net/browse/OD-12458)

4. Fix course class prices in solr index
  [ https://ish-group.atlassian.net/browse/OD-11554](https://ish-group.atlassian.net/browse/OD-11554)


6. Mount new `CoursesSceleton` to default `/courses` path for all colleges permanently


###unresolved problems yet

1. In #editor mode we need to show draft page layout so the html cache doesn't work here. 
We need to render tml files from druft version, exactly how it works right now (16-11-2021)
I have no Idea right now, we might put some fork some where to keep old rendering mechanism.

### alternativ way 

Instead of fetching classes from solr on back end we could complitelly do it on client side with direct JS queries to the solr 

1. open /courses/arts url in browser
2. server only fetch cources ids (even all at ones, we could geet rid on pagination)
3. render only course items and leave classes content empty 
4. leave some placeholders for classes
5. after server complite the browser request and return html the JS start fetching classes directly from solr and populate placeholders
6. just enough to populate viewport 
7. on user scroll down - fetch more classes.
8. build solr query bases on course id and user filters (do all this on client)  - like we do o server right now, see 
```
ish.oncourse.services.search.SearchService.searchClasses()
```
9. To simplify class query we might remove class indexes that do not satisfy webSite restriction ('show class 10 days after  start' or 'hide class 5 days before end' ).
So the query do not include that filter.
see:
`   ish.oncourse.ui.utils.CourseItemModel.fillClasses()
`


### Production solr server:
We could use real solr server to debug queries:
[http://10.100.48.1:8983/solr/#/]()


### Run web + checkout-api + checkout

1. run web app (see 'run willow app' from ./README.md)

`./gradlew web:start`


2. add header (use ModeHeader chrome extantion)
   `X-Site-Key:palaven`

3. add resources redirects (use Resource Owerride chrome extantion)

- js bundle

   `http://127.0.0.1:8305/s/js/all.js* -> http://127.0.0.1:8080/js/all.js`

- css bundle

   `http://127.0.0.1:8305/s/stylesheets/css/site.css* -> http://127.0.0.1:8080/stylesheets/css/site.css`

- checkout bundle

    `http://127.0.0.1:8305/s/oncourse-releases/checkout/stable/dynamic.js* -> http://localhost:8100/assets/dynamic.js`

- checkout-api requests 

    `http://127.0.0.1:8305/a/* -> http://127.0.0.1:8283/a/*`

4. Unzip ./web/template-palaven.zip

5. Run live server from resurces root (template-palaven directory)

    `#live-serve`

6. Run checkout in webpack watch mode

    `cd ./checkout`
    `yarn start`

7. Run checkout api (see 'run willow app' from ./README.md)

    `./gradlew checkout-api:start`
