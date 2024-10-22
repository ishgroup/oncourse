package ish.oncourse.server.api.traits


import ish.common.types.FieldValidationType
import ish.oncourse.server.api.v1.model.FieldValidationTypeDTO

trait FieldValidationTypeDTOTrait {
    FieldValidationType getDbType() {
        switch (this as FieldValidationTypeDTO) {
            case FieldValidationTypeDTO.RELATED_CONTACT_EMAIL:
                return FieldValidationType.CONTACT_EMAIL_ADDRESS
            default:
                throw new IllegalArgumentException("${toString()}")
        }
    }

    FieldValidationTypeDTO fromDbType(FieldValidationType dataType) {
        if(!dataType) {
            return null
        }
        switch(dataType) {
            case FieldValidationType.CONTACT_EMAIL_ADDRESS:
                return FieldValidationTypeDTO.RELATED_CONTACT_EMAIL
            default:
                throw new IllegalArgumentException("$dataType.displayName")
        }
    }
}