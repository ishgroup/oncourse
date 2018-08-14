package ish.oncourse.webservices.solr

import com.google.inject.Injector
import ish.oncourse.model.Course
import ish.oncourse.model.CourseClass
import ish.oncourse.model.Session
import ish.oncourse.model.Site
import ish.oncourse.model.Taggable
import ish.oncourse.solr.SolrCollection
import ish.oncourse.solr.reindex.ReindexCourses
import ish.oncourse.solr.update.listener.GetCoursesFromClasses
import ish.oncourse.solr.update.listener.GetCoursesFromSessions
import ish.oncourse.solr.update.listener.GetCoursesFromSite
import ish.oncourse.solr.update.listener.GetCoursesFromTag
import ish.oncourse.solr.update.listener.GetCoursesToDelete
import ish.oncourse.solr.update.listener.GetCoursesToUpdate
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.commitlog.model.ChangeMap
import org.apache.cayenne.commitlog.model.ObjectChange
import org.apache.cayenne.configuration.server.ServerRuntime
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.solr.client.solrj.SolrClient

class SolrUpdateCourseDocumentsListener extends ish.oncourse.solr.update.listener.SolrUpdateCourseDocumentsListener {

    private static final Logger logger = LogManager.logger

    private SolrClient solrClient
    private ServerRuntime serverRuntime
    private Injector injector
    
    
    SolrUpdateCourseDocumentsListener injector(Injector injector) {
        this.injector = injector
        return this
    }

    @Override
    void onPostCommit(ObjectContext originatingContext, ChangeMap changes) {
        
        Set<Long> courseIdToUpdate = new HashSet<>()
        Set<Long> courseIdToRemove = new HashSet<>()
        
        try {
            Set<ObjectChange> courseChanges = filter(changes, Course.simpleName)
            if (!courseChanges.empty) {
                courseIdToUpdate.addAll(GetCoursesToUpdate.valueOf(courseChanges).get())
                courseIdToRemove.addAll(GetCoursesToDelete.valueOf(courseChanges).get())
            }

            Set<ObjectChange> classChanges = filter(changes, CourseClass.simpleName)
            if (!classChanges.empty) {
                courseIdToUpdate.addAll(GetCoursesFromClasses.valueOf(classChanges, getCayenne().newContext()).get())
            }

            Set<ObjectChange> sessionChanges = filter(changes, Session.simpleName)
            if (!sessionChanges.empty) {
                courseIdToUpdate.addAll(GetCoursesFromSessions.valueOf(sessionChanges, getCayenne().newContext()).get())
            }

            Set<ObjectChange> tagChanges = filter(changes, Taggable.simpleName)
            if (!tagChanges.empty) {
                courseIdToUpdate.addAll(GetCoursesFromTag.get(tagChanges))
            }
            
            Set<ObjectChange> siteChanges = filter(changes, Site.simpleName)
            if (!siteChanges.empty) {
                courseIdToUpdate.addAll(GetCoursesFromSite.valueOf(siteChanges,getCayenne().newContext()).get())
            }
            
            
            courseIdToUpdate.removeAll(courseIdToRemove)
            executeDelete(courseIdToRemove)
            executeUpdate(courseIdToUpdate)
            
        } catch (Exception e) {
            logger.error("Exception occurred during changes listening")
            logger.catching(e)
        }
        
    }

    void executeUpdate(Set<Long> courseIdToUpdate) {
        if (courseIdToUpdate.size() > 0) {
            try {
                new ReindexCourses(getCayenne().newContext(), getSolr(), courseIdToUpdate).run()
            } catch (Exception e) {
                logger.error("Exception occurred during courses reindex: ${courseIdToUpdate.join(',')}")
                logger.catching(e)
            }
        }
    }

    void executeDelete(Set<Long> courseIdToRemove){
        if (courseIdToRemove.size() > 0) {
            List<String> stringIds = courseIdToRemove.collect {it.toString()}
            try {
                getSolr().deleteById(SolrCollection.courses.name(), stringIds)
                getSolr().commit(SolrCollection.courses.name())
            } catch (Exception e) {
                logger.error("Exception occurred during courses deletion: ${stringIds.join(',')}")
                logger.catching(e)
            }
        }
    }
    
    protected ServerRuntime getCayenne() {
        if(!serverRuntime) {
            serverRuntime = injector.getInstance(ServerRuntime)
        }
        return serverRuntime
    }

    private SolrClient getSolr() {
        if(!solrClient) {
            solrClient = injector.getInstance(SolrClient)
        }
        return solrClient
    }

    private static Set<ObjectChange> filter(ChangeMap changes, String entity) {
        changes.uniqueChanges.findAll {it.postCommitId.entityName == entity}
    }
}