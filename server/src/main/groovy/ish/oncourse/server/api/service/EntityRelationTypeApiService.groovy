package ish.oncourse.server.api.service

import ish.oncourse.server.api.dao.EntityRelationTypeDao
import ish.oncourse.server.api.v1.model.EntityRelationCartActionDTO
import ish.oncourse.server.api.v1.model.EntityRelationTypeDTO
import ish.oncourse.server.api.validation.EntityValidator
import ish.oncourse.server.cayenne.Discount
import ish.oncourse.server.cayenne.EntityRelationType
import ish.util.LocalDateUtils
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.SelectById

class EntityRelationTypeApiService extends EntityApiService<EntityRelationTypeDTO, EntityRelationType, EntityRelationTypeDao> {

    @Override
    Class<EntityRelationType> getPersistentClass() {
        return EntityRelationType
    }

    @Override
    EntityRelationTypeDTO toRestModel(EntityRelationType dbEntity) {
        new EntityRelationTypeDTO().with { dtoModel ->
            dtoModel.id = dbEntity.id
            dtoModel.created = LocalDateUtils.dateToTimeValue(dbEntity.createdOn)
            dtoModel.modified = LocalDateUtils.dateToTimeValue(dbEntity.modifiedOn)
            dtoModel.name = dbEntity.name
            dtoModel.fromName = dbEntity.fromName
            dtoModel.toName = dbEntity.toName
            dtoModel.description = dbEntity.description
            dtoModel.isShownOnWeb = dbEntity.isShownOnWeb
            dtoModel.considerHistory = dbEntity.considerHistory
            dtoModel.shoppingCart = EntityRelationCartActionDTO.values()[0].fromDbType(dbEntity.shoppingCart)
            dtoModel.discountId = dbEntity.entityRelationTypeDiscount?.id
            dtoModel
        }
    }

    @Override
    EntityRelationType toCayenneModel(EntityRelationTypeDTO dtoModel, EntityRelationType dbEntity) {
        dbEntity.name = dtoModel.name
        dbEntity.toName = dtoModel.toName
        dbEntity.fromName = dtoModel.fromName
        dbEntity.description = dtoModel.description
        dbEntity.isShownOnWeb = dtoModel.isIsShownOnWeb()
        dbEntity.considerHistory = dtoModel.isConsiderHistory()
        if (dtoModel.discountId) {
            dbEntity.entityRelationTypeDiscount = SelectById.query(Discount, dtoModel.discountId).selectOne(dbEntity.context)
        } else {
            dbEntity.entityRelationTypeDiscount = null
        }
        if (dtoModel.shoppingCart) {
            dbEntity.shoppingCart = dtoModel.shoppingCart.dbType
        }
        dbEntity
    }

    @Override
    void validateModelBeforeSave(EntityRelationTypeDTO dtoModel, ObjectContext context, Long id) {
        if (!dtoModel.name || dtoModel.name.empty) {
            EntityValidator.throwClientErrorException("name", "Sellable item should has a name.")
        }

        if (!dtoModel.toName || dtoModel.toName.empty) {
            EntityValidator.throwClientErrorException("name", "Name of entity is related to cannot be empty.")
        }

        if (!dtoModel.fromName || dtoModel.fromName.empty) {
            EntityValidator.throwClientErrorException("name", "Name of entity is related from cannot be empty.")
        }

        if (dtoModel.shoppingCart == null) {
            EntityValidator.throwClientErrorException("shoppingCart", "Sellable item should has a shoppingCart.")
        }
    }

    @Override
    void validateModelBeforeRemove(EntityRelationType dbEntity) {
        if (dbEntity.id <= 0) {
            EntityValidator.throwClientErrorException(dbEntity.id, "id", "System sellable item relation type cannot be removed")
        }

        if (!dbEntity.entityRelations.empty) {
            EntityValidator.throwClientErrorException(dbEntity.id, "entityRelations", "Sellable item cannot be removed. It has assigned Entity relations.")
        }
    }

    static void validateDuplicates(List<EntityRelationTypeDTO> relationTypes) {
        List<String> names =  relationTypes*.name.flatten() as List<String>
        List<String> duplicates = names.findAll { names.count(it) > 1 }.unique()

        if (!duplicates.empty) {
            EntityValidator.throwClientErrorException("name", "Sellable item should has unique name!")
        }
    }
}
