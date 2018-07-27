package ish.oncourse.solr.update.listener

import ish.oncourse.model.Course
import org.apache.cayenne.commitlog.model.AttributeChange
import org.apache.cayenne.commitlog.model.ObjectChange
import org.apache.cayenne.commitlog.model.ObjectChangeType
import static ish.oncourse.model.auto._Taggable.ENTITY_IDENTIFIER_PROPERTY
import static ish.oncourse.model.auto._Taggable.ENTITY_WILLOW_ID_PROPERTY


class GetCoursesFromTag {
    
    private List<Long> unprocessedTags = new ArrayList<>()
    

    private  boolean courseTagged(ObjectChange change) {
        change.type == ObjectChangeType.UPDATE &&
                change.attributeChanges.any { attr -> attr.key == ENTITY_WILLOW_ID_PROPERTY } &&
                unprocessedTags.remove(change.postCommitId.idSnapshot['id'] as Long)
                
    }

    private static boolean courseUnTagged(ObjectChange change) {
        change.type == ObjectChangeType.DELETE && change.attributeChanges.any { attr -> attr.key == ENTITY_IDENTIFIER_PROPERTY &&  attr.value.oldValue == Course.simpleName}
    }
    
    private static Long courseId(ObjectChange change) {
        AttributeChange  attr = change.attributeChanges.find { attr -> attr.key == ENTITY_WILLOW_ID_PROPERTY }.value
        return  (attr.oldValue?: attr.newValue) as Long
    }

    private static boolean unAssignedTaggable(ObjectChange change) {
        change.type == ObjectChangeType.INSERT &&
                change.attributeChanges.any { attr -> attr.key == ENTITY_IDENTIFIER_PROPERTY &&  attr.value.oldValue == Course.simpleName } &&
                !change.attributeChanges.any { attr -> attr.key == ENTITY_WILLOW_ID_PROPERTY }
    }


    private static List<Long> unprocessedTags(Set<ObjectChange> objectChanges) {
        objectChanges.findAll { change -> unAssignedTaggable(change) }.collect {it.postCommitId.idSnapshot['id'] as Long}
    }
    
    Set<Long> get(Set<ObjectChange> objectChanges) {
        
        unprocessedTags += unprocessedTags(objectChanges)
        
        
        return objectChanges
                    .findAll { change -> courseUnTagged(change) || courseTagged(change) }
                    .collect { change -> courseId(change)}
                    .toSet()
    }
}
