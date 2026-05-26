package ish.oncourse.server.api.traits

import ish.common.types.CorporatePassPaymentType
import ish.oncourse.server.api.v1.model.CorporatePassPaymentTypeDTO

trait CorporatePassPaymentTypeDTOTrait {
     CorporatePassPaymentType getDbType() {
        switch (this as CorporatePassPaymentTypeDTO) {
            case CorporatePassPaymentTypeDTO.DEFAULT:
                return CorporatePassPaymentType.DEFAULT
            case CorporatePassPaymentTypeDTO.DECLINE_IF_BALANCE_NOT_ENOUGH:
                return CorporatePassPaymentType.DECLINE_IF_BALANCE_NOT_ENOUGH
            case CorporatePassPaymentTypeDTO.PAY_WITH_CREDIT_NOTES:
                return CorporatePassPaymentType.PAY_WITH_CREDIT_NOTES
            default:
                throw new IllegalArgumentException("${toString()}")
        }
    }


    CorporatePassPaymentTypeDTO fromDbType(CorporatePassPaymentType dataType) {
        if(!dataType) {
            return null
        }

        switch(dataType) {
            case CorporatePassPaymentType.DEFAULT:
                return CorporatePassPaymentTypeDTO.DEFAULT
            case CorporatePassPaymentType.DECLINE_IF_BALANCE_NOT_ENOUGH:
                return CorporatePassPaymentTypeDTO.DECLINE_IF_BALANCE_NOT_ENOUGH
            case CorporatePassPaymentType.PAY_WITH_CREDIT_NOTES:
                return CorporatePassPaymentTypeDTO.PAY_WITH_CREDIT_NOTES
            default:
                throw new IllegalArgumentException("$dataType.displayName")
        }
    }

}