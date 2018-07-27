package ish.oncourse.solr.update.listener

import ish.oncourse.model.Course
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.ObjectId
import org.apache.cayenne.commitlog.CommitLogListener
import org.apache.cayenne.commitlog.model.ChangeMap
import org.apache.cayenne.commitlog.model.ObjectChange

/**
 * Created by alex on 2/8/18.
 */
class SolrUpdateCourseDocumentsListener implements CommitLogListener{
    Set<Long> courseIdToUpdate = new HashSet<>()
    Set<Long> courseIdToRemove = new HashSet<>()
    
    @Override
    void onPostCommit(ObjectContext originatingContext, ChangeMap changes) {
        Set<ObjectChange> courseChanges = changes.uniqueChanges.findAll {it.postCommitId.entityName == Course.class.simpleName}
        courseIdToUpdate.addAll(GetCoursesToUpdate.valueOf(courseChanges).get())
        courseIdToRemove.addAll(GetCoursesToDelete.valueOf(courseChanges).get())
        
        courseIdToUpdate.removeAll(courseIdToRemove)
        
        executeDelete(courseIdToRemove)
        executeUpdate(courseIdToUpdate)
    }
    
    void executeUpdate(Set<Long> courseIdToUpdate){}

    void executeDelete(Set<Long> courseIdToRemove){}
}
