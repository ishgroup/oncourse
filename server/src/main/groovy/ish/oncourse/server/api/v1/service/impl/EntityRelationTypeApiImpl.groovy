package ish.oncourse.server.api.v1.service.impl

import javax.inject.Inject
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.api.dao.EntityRelationTypeDao
import ish.oncourse.server.api.service.EntityRelationTypeApiService
import ish.oncourse.server.api.v1.model.EntityRelationTypeDTO
import ish.oncourse.server.api.v1.service.EntityRelationTypeApi

import static ish.oncourse.server.api.service.EntityRelationTypeApiService.validateDuplicates

class EntityRelationTypeApiImpl implements EntityRelationTypeApi {

    @Inject
    private ICayenneService cayenneService

    @Inject
    private EntityRelationTypeApiService entityRelationTypeApiService

    @Override
    List<EntityRelationTypeDTO> get() {
        EntityRelationTypeDao.getTypes(cayenneService.newContext)
                .collect { entityRelationTypeApiService.toRestModel(it) }
    }

    @Override
    void remove(String id) {
        entityRelationTypeApiService.remove(Long.valueOf(id))
    }

    @Override
    void update(List<EntityRelationTypeDTO> relationTypes) {
        validateDuplicates(relationTypes)
        relationTypes.each { it ->
            if (!it.id) {
                entityRelationTypeApiService.create(it)
            } else {
                entityRelationTypeApiService.update(it.id, it)
            }
        }
    }
}
