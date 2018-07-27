package ish.oncourse.solr.update.listener

import ish.oncourse.model.Course
import org.apache.cayenne.ObjectId
import org.apache.cayenne.commitlog.model.ObjectChange
import org.apache.cayenne.commitlog.model.ObjectChangeType

/**
 * Created by alex on 2/8/18.
 */
class GetCoursesToDelete {
    Set<ObjectChange> objectChanges
    
    Set<Long> get(){
        objectChanges.findAll {wasRemoved(it) || becomeInvisible(it)}.collect {it.postCommitId.idSnapshot['id'] as Long}
    }
    
    private static boolean becomeInvisible(ObjectChange change){
        change.type == ObjectChangeType.UPDATE && change.attributeChanges.find {it.key == Course.IS_WEB_VISIBLE_PROPERTY && !it.value.newValue }
    }
    
    private static wasRemoved(ObjectChange change){
        change.type == ObjectChangeType.DELETE
    }
    
    static GetCoursesToDelete valueOf(Set<ObjectChange> objectChanges){
        GetCoursesToDelete condition = new GetCoursesToDelete()
        condition.objectChanges = objectChanges
        
        condition
    }
}
