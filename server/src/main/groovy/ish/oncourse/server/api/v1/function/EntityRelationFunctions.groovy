package ish.oncourse.server.api.v1.function

import ish.common.types.EntityRelationCartAction
import ish.common.types.TypesUtil
import ish.oncourse.server.api.v1.model.EntityRelationCartActionDTO
import ish.oncourse.server.api.v1.model.EntityRelationTypeDTO
import ish.oncourse.server.api.validation.EntityValidator
import ish.oncourse.server.cayenne.Discount
import ish.oncourse.server.cayenne.EntityRelationType
import ish.util.LocalDateUtils
import org.apache.cayenne.query.SelectById


class EntityRelationFunctions {

    static EntityRelationTypeDTO toRestEntityRelationType(EntityRelationType dbEntity) {
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
            dtoModel.shoppingCart = EntityRelationCartActionDTO.fromValue(dbEntity.shoppingCart?.displayName)
            dtoModel.discountId = dbEntity.entityRelationTypeDiscount?.id
            dtoModel
        }
    }

    static void toCayenneModelEntityRelationType(EntityRelationType dbEntity, EntityRelationTypeDTO dtoModel) {
        dbEntity.name = dtoModel.name
        dbEntity.toName = dtoModel.toName
        dbEntity.fromName = dtoModel.fromName
        dbEntity.description = dtoModel.description
        dbEntity.isShownOnWeb = dtoModel.isShownOnWeb
        dbEntity.considerHistory = dtoModel.considerHistory
        if (dtoModel.discountId) {
            dbEntity.entityRelationTypeDiscount = SelectById.query(Discount, dtoModel.discountId).selectOne(dbEntity.context)
        }
        if (dtoModel.shoppingCart) {
            dbEntity.shoppingCart = TypesUtil.getEnumForDisplayName(dtoModel.shoppingCart.toString(), EntityRelationCartAction)
        }
    }

    static void validateBeforeRemove(EntityRelationType dbEntity) {

        if (dbEntity.id <= 0) {
            EntityValidator.throwClientErrorException(dbEntity.id, "id", "System entity relation type cannot be removed")
        }

        if (!dbEntity.entityRelations.empty) {
            EntityValidator.throwClientErrorException(dbEntity.id, "entityRelations", "Entity relation type cannot be removed. It has assigned Entity relations.")
        }
    }

    static void validateBeforeUpdate(EntityRelationTypeDTO dtoModel) {
        if (!dtoModel.name || dtoModel.name.empty) {
            EntityValidator.throwClientErrorException("name", "Entity relation type should has a name.")
        }

        if (!dtoModel.toName || dtoModel.toName.empty) {
            EntityValidator.throwClientErrorException("name", "Name of entity is related to cannot be empty.")
        }

        if (!dtoModel.fromName || dtoModel.fromName.empty) {
            EntityValidator.throwClientErrorException("name", "Name of entity is related from cannot be empty.")
        }

        if (dtoModel.shoppingCart == null) {
            EntityValidator.throwClientErrorException("shoppingCart", "Entity relation type should has a shoppingCart.")
        }
    }
}
