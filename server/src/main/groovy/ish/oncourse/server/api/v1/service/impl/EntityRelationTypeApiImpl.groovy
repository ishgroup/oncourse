package ish.oncourse.server.api.v1.service.impl

import com.google.inject.Inject
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.api.v1.model.EntityRelationTypeDTO
import ish.oncourse.server.api.v1.service.EntityRelationTypeApi
import ish.oncourse.server.api.validation.EntityValidator
import ish.oncourse.server.cayenne.EntityRelationType
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById

import static ish.oncourse.server.api.v1.function.EntityRelationFunctions.toCayenneModelEntityRelationType
import static ish.oncourse.server.api.v1.function.EntityRelationFunctions.toRestEntityRelationType
import static ish.oncourse.server.api.v1.function.EntityRelationFunctions.validateBeforeRemove
import static ish.oncourse.server.api.v1.function.EntityRelationFunctions.validateBeforeUpdate

class EntityRelationTypeApiImpl implements EntityRelationTypeApi {

    @Inject
    private ICayenneService cayenneService

    @Override
    List<EntityRelationTypeDTO> get() {
        ObjectSelect.query(EntityRelationType)
                .select(cayenneService.newContext)
                .collect { toRestEntityRelationType(it) }
    }

    @Override
    void remove(String id) {
        ObjectContext context = cayenneService.newContext
        EntityRelationType dbEntity = SelectById.query(EntityRelationType, Long.valueOf(id)).selectOne(context)
        if (dbEntity) {
            validateBeforeRemove(dbEntity)
            context.deleteObject(dbEntity)
            context.commitChanges()
        } else {
            EntityValidator.throwClientErrorException( id, 'id', "Cannot remove a non-existent EntityRelationType")
        }

    }

    @Override
    void update(List<EntityRelationTypeDTO> relationTypes) {
        ObjectContext context = cayenneService.newContext

        relationTypes.each { dtoModel ->
            validateBeforeUpdate(dtoModel)
            EntityRelationType dbEntity = dtoModel.id ?
                    SelectById.query(EntityRelationType, dtoModel.id).selectOne(context) :
                    context.newObject(EntityRelationType)
            if (dbEntity) {
                toCayenneModelEntityRelationType(dbEntity, dtoModel)
                context.commitChanges()
            } else {
                EntityValidator.throwClientErrorException( dtoModel.id, 'id', "Cannot update a non-existent EntityRelationType")
            }
        }

    }
}
