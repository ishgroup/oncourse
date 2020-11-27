package ish.oncourse.server.api.traits

import ish.common.types.EntityRelationCartAction
import ish.common.types.MessageStatus
import ish.oncourse.server.api.v1.model.EntityRelationCartActionDTO
import ish.oncourse.server.api.v1.model.MessageStatusDTO

trait EntityRelationCartActionDTOTrait {


    EntityRelationCartAction getDbType() {
        switch (this as EntityRelationCartActionDTO) {
            case EntityRelationCartActionDTO.NO_ACTION:
                return EntityRelationCartAction.NO_ACTION
            case EntityRelationCartActionDTO.SUGGESTION:
                return EntityRelationCartAction.SUGGESTION
            case EntityRelationCartActionDTO.ADD_BUT_DO_NOT_ALLOW_REMOVAL:
                return EntityRelationCartAction.ADD_NO_REMOVAL
            case EntityRelationCartActionDTO.ADD_AND_ALLOW_REMOVAL:
                return EntityRelationCartAction.ADD_ALLOW_REMOVAL
            default:
                throw new IllegalArgumentException("${toString()}")
        }
    }

    EntityRelationCartActionDTO fromDbType(EntityRelationCartAction cartAction) {
        if(!cartAction) {
            return null
        }
        switch (cartAction) {
            case EntityRelationCartAction.NO_ACTION:
                return EntityRelationCartActionDTO.NO_ACTION
            case EntityRelationCartAction.SUGGESTION:
                return EntityRelationCartActionDTO.NO_ACTION
            case EntityRelationCartAction.ADD_ALLOW_REMOVAL:
                return EntityRelationCartActionDTO.NO_ACTION
            case EntityRelationCartAction.ADD_NO_REMOVAL:
                return EntityRelationCartActionDTO.NO_ACTION
            default:
                throw new IllegalArgumentException("$cartAction.displayName")
        }
    }

}