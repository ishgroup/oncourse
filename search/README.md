## sold data chema
we have 4 solr collections:
 - Courses
 - Classes
 - Tags
 - Suburbs

To build indexes on services we use Java models
```
ish.oncourse.solr.model.SSuburb
ish.oncourse.solr.model.SCourseClass
ish.oncourse.solr.model.SCourse
ish.oncourse.solr.model.STag
```

That are entry points to collect/build java model indexes for each entity:
```
ish.oncourse.solr.reindex.ReindexClasses
ish.oncourse.solr.reindex.ReindexCourses
ish.oncourse.solr.reindex.ReindexSuburbs
ish.oncourse.solr.reindex.ReindexTags
```

the solr index schema:

[common/solr-index/src/main/resources/solr/courses/conf/schema.xml]()</br>
[common/solr-index/src/main/resources/solr/classes/conf/schema.xml]()</br>
[common/solr-index/src/main/resources/solr/suburbs/conf/schema.xml]()</br>
[common/solr-index/src/main/resources/solr/tags/conf/schema.xml]()</br>

The Java model should absalutelly match to solr xml file (same set of fields with same types)


## How the data gets in

We have implemented real time indexation process for courses, classes.
As soon as data changed on willow we assembe corresponded indexes and put them into solr.
Listed entities can be changed on Angel side only so all changes comes from inbound replication.

The listening of data chanve events implemented bases on cayenne CommitLogListener feature.
Our implementation is (srvices app):
```
ish.oncourse.webservices.solr.SolrUpdateCourseDocumentsListener
```
see javadoc of this class for more detail 

If course has been taged we also run certain course reindex:
```
ish.oncourse.services.lifecycle.TaggableListener.setEntityWillowId()
```

We also make full reindex on Courses/Classes every night. 
The reindexing of Tags and Suburbs triggers by cron only (srvices app):
```
ish.oncourse.webservices.quartz.job.sol.ReindexCoursesJob
ish.oncourse.webservices.quartz.job.sol.ReindexSuburbsJob
ish.oncourse.webservices.quartz.job.sol.ReindexTagsJob
```

to force full reindex any collection execute followed SQL on production db:

```
UPDATE QRTZ_TRIGGERS SET NEXT_FIRE_TIME = {timeInMilliseconds} WHERE TRIGGER_NAME='ish.oncourse.webservices.quartz.job.solr.ReindexCoursesJob'
```

#Course index schema

The Tags/Classes/Suburbs has really simple schema with flatten attribute types 

The Course is little bit outstand from common approach, see:

 - multi valued fields
```
<field name="class_start" type="date" indexed="true" stored="true" multiValued="true" omitNorms="false"/>
```


The `class_start` field actually contains array of all relateed classes start date `multiValued="true"`.

so if Course has 
```
class_start:['10-12-21 10:00:00','11-12-21 10:00:00','12-12-21 10:00:00','13-12-21 10:00:00']
```
the both sarch queries
```
class_start:'10-12-21 10:00:00'
```
and
```
class_start:'11-12-21 10:00:00'
```
will fetch that course.

That is nedeed to find the course which classes has convinient start date.

- spatial search field
```
<field name="course_loc" type="location_rpt" indexed="true" stored="true" multiValued="true" />
```
that field actually store course cordinates (latitude and longitude) in which places related classes take a plase.
So when we make a search by suburb - in reality we take a coordinates of suburb and looking for a cources in 100km radious from that point.
Then range the search results by the distance 

- exact text
```
        <field name="class_code" type="exacttext" indexed="true" stored="true"
               multiValued="true" omitNorms="false"/>
```

index value:
```
class_code:['ABC','123']
```
sarch expression:
```
class_code:'AB'
```
will has no results, and expression:
```
class_code:'ABC'
```
will fetch index

- generic text fields
```
  <field name="name" type="textgen" indexed="true" stored="true"
  omitNorms="false"/>
```

stemming synonyms with similar roots, see example:
```
// index value
name:'react for beginers'

// the query like:
name:'act'
//will fetch that course
```

# how the queries are routed from the web app

Web app is asingle place that produce solr queries.
Entry point is

 - URL query params appended to [/course]() root path like that:

[/courses?near=west-ryde/2114/100&duration=>3]()

exact place where we parse it, see the code for more info:
```
ish.oncourse.services.search.SearchParamsParser
```
 - URL context appended to [/course]() root path like that:

   [courses/what/comedy]()

   exact place where we parse it, see the code for more info:
```
ish.oncourse.linktransform.PageLinkTransformer.decodePageRenderRequest()
....
			case Courses:
```

After collectiong all search params we perform query to course collection using single entry point:

```
ish.oncourse.services.search.SearchService
```

# Handling search responce 

We always make paginated query to solr collection, fetch 10 cources (only their ids) at a time.
```
ish.oncourse.ui.pages.Courses.searchCourses()
```
Java code then select course recrds from db by that ids
Collect courseClasses, and also filter out classes that doesn't fit rearch criterias, see:

```
 ish.oncourse.ui.utils.CourseItemModel.fullFillClasses()
```

Finaly, tapestry template render actuall page html

[common/common-templates/src/main/resources/ish/oncourse/ui/pages/Courses.tml]()

Client side code (java script) control current scrolling position and offset index.
When user scroll down at the end, client code produce ajax request in background to fetch/show next 10 course items (srver side rendering as well), see:

```
ish.oncourse.ui.pages.Courses.beforeRender()
....
if (isXHR() && this.itemIndex != 0) { 
....
```

