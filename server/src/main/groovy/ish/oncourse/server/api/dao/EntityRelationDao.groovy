package ish.oncourse.server.api.dao

import ish.oncourse.server.cayenne.EntityRelation
import ish.oncourse.server.cayenne.EntityRelationType
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById

class EntityRelationDao implements CayenneLayer<EntityRelation> {

    @Override
    EntityRelation newObject(ObjectContext context) {
        context.newObject(EntityRelation)
    }

    @Override
    EntityRelation getById(ObjectContext context, Long id) {
        SelectById.query(EntityRelation, id)
                .selectOne(context)
    }

    static List<EntityRelation> getRelatedTo(ObjectContext context, String entityName, Long entityId) {
        ObjectSelect.query(EntityRelation)
                .where(EntityRelation.FROM_ENTITY_IDENTIFIER.eq(entityName))
                .and(EntityRelation.FROM_ENTITY_ANGEL_ID.eq(entityId))
                .select(context)
    }

    static List<EntityRelation> getRelatedFrom(ObjectContext context, String entityName, Long entityId) {
        ObjectSelect.query(EntityRelation)
                .where(EntityRelation.TO_ENTITY_IDENTIFIER.eq(entityName))
                .and(EntityRelation.TO_ENTITY_ANGEL_ID.eq(entityId))
                .select(context)
    }

    static List<EntityRelation> getRelatedToByName(ObjectContext context, String entityName, Long entityId, String relationName) {
        ObjectSelect.query(EntityRelation)
                .where(EntityRelation.FROM_ENTITY_IDENTIFIER.eq(entityName))
                .and(EntityRelation.FROM_ENTITY_ANGEL_ID.eq(entityId))
                .and(EntityRelation.RELATION_TYPE.dot(EntityRelationType.NAME).eq(relationName))
                .select(context)
    }

    static List<EntityRelation> getRelatedFromByName(ObjectContext context, String entityName, Long entityId, String relationName) {
        ObjectSelect.query(EntityRelation)
                .where(EntityRelation.TO_ENTITY_IDENTIFIER.eq(entityName))
                .and(EntityRelation.TO_ENTITY_ANGEL_ID.eq(entityId))
                .and(EntityRelation.RELATION_TYPE.dot(EntityRelationType.NAME).eq(relationName))
                .select(context)
    }

    static List<EntityRelation> getRelatedToOrEqual(ObjectContext context, String entityName, Long entityId) {
        ObjectSelect.query(EntityRelation)
                .or(EntityRelation.FROM_ENTITY_IDENTIFIER.eq(entityName).andExp(EntityRelation.FROM_ENTITY_ANGEL_ID.eq(entityId)),
                        EntityRelation.RELATION_TYPE.dot(EntityRelationType.FROM_NAME).eq(EntityRelation.RELATION_TYPE.dot(EntityRelationType.TO_NAME))
                )
                .select(context)
    }

    static List<EntityRelation> getRelatedFromOrEqual(ObjectContext context, String entityName, Long entityId) {
        ObjectSelect.query(EntityRelation)
                .or(EntityRelation.TO_ENTITY_IDENTIFIER.eq(entityName).andExp(EntityRelation.TO_ENTITY_ANGEL_ID.eq(entityId)),
                        EntityRelation.RELATION_TYPE.dot(EntityRelationType.FROM_NAME).eq(EntityRelation.RELATION_TYPE.dot(EntityRelationType.TO_NAME))
                )
                .select(context)
    }
}
