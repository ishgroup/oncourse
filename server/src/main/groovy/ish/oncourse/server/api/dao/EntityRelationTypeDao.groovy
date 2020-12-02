package ish.oncourse.server.api.dao

import ish.oncourse.server.cayenne.EntityRelationType
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById

class EntityRelationTypeDao implements CayenneLayer<EntityRelationType> {

    @Override
    EntityRelationType newObject(ObjectContext context) {
        context.newObject(EntityRelationType)
    }

    @Override
    EntityRelationType getById(ObjectContext context, Long id) {
        SelectById.query(EntityRelationType, id)
                .selectOne(context)
    }

    static List<EntityRelationType> getTypes(ObjectContext context) {
        ObjectSelect.query(EntityRelationType).select(context)
    }
}
