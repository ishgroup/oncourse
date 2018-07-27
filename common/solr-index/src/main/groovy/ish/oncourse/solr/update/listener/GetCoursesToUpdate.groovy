package ish.oncourse.solr.update.listener

import ish.oncourse.model.Course
import org.apache.cayenne.ObjectId
import org.apache.cayenne.commitlog.model.ObjectChange
import org.apache.cayenne.commitlog.model.ObjectChangeType

/**
 * Created by alex on 2/8/18.
 */
class GetCoursesToUpdate {
    Set<ObjectChange> objectChanges

    Set<Long> get(){
        objectChanges.findAll { newCourseAdded(it) || becomeVisible(it) || nameChanged(it) || codeChanged(it) || detailChanged(it) || classesChanged(it)}.collect { it.postCommitId.idSnapshot['id'] as Long }
    }
    
    private static boolean newCourseAdded(ObjectChange change){
        change.type == ObjectChangeType.INSERT && becomeVisible(change)
    }

    private static boolean becomeVisible(ObjectChange change){
        change.attributeChanges.any {it.key == Course.IS_WEB_VISIBLE_PROPERTY && it.value.newValue }
    }
    
    private static boolean nameChanged(ObjectChange change) {
        change.attributeChanges.any {it.key == Course.NAME_PROPERTY}
    }

    private static boolean codeChanged(ObjectChange change) {
        change.attributeChanges.any {it.key == Course.CODE_PROPERTY}
    }

    private static boolean detailChanged(ObjectChange change) {
        change.attributeChanges.any {it.key == Course.DETAIL_PROPERTY}
    }

    private static boolean classesChanged(ObjectChange change) {
        change.toManyRelationshipChanges.any {it.key == Course.COURSE_CLASSES_PROPERTY}
    }

    static GetCoursesToUpdate valueOf(Set<ObjectChange> objectChanges){
        GetCoursesToUpdate condition = new GetCoursesToUpdate()
        condition.objectChanges = objectChanges

        condition
    }
}
