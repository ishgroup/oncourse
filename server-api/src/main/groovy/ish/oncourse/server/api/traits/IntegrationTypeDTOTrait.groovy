package ish.oncourse.server.api.traits

import ish.common.types.IntegrationType
import ish.oncourse.server.api.v1.model.IntegrationTypeDTO

trait IntegrationTypeDTOTrait {
    IntegrationType getDbType() {
        switch (this as IntegrationTypeDTO) {
            case IntegrationTypeDTO.MOODLE:
                return IntegrationType.MOODLE
            case IntegrationTypeDTO.MAILCHIMP:
                return IntegrationType.MAILCHIMP
            case IntegrationTypeDTO.SURVEY_MONKEY:
                return IntegrationType.SURVEY_MONKEY
            case IntegrationTypeDTO.ALCHEMER:
                return IntegrationType.ALCHEMER
            case IntegrationTypeDTO.XERO:
                return IntegrationType.XERO
            case IntegrationTypeDTO.MYOB:
                return IntegrationType.MYOB
            case IntegrationTypeDTO.CLOUD_ACCESS:
                return IntegrationType.CLOUD_ACCESS
            case IntegrationTypeDTO.CANVAS:
                return IntegrationType.CANVAS
            case IntegrationTypeDTO.MICROPOWER:
                return IntegrationType.MICROPOWER
            case IntegrationTypeDTO.USI_AGENCY:
                return IntegrationType.USI_AGENCY
            case IntegrationTypeDTO.VET_STUDENT_LOANS:
                return IntegrationType.VET_STUDENT_LOANS
            case IntegrationTypeDTO.GOOGLE_CLASSROOM:
                return IntegrationType.GOOGLE_CLASSROOM
            case IntegrationTypeDTO.COASSEMBLE:
                return IntegrationType.COASSEMBLE
            case IntegrationTypeDTO.TALENT_LMS:
                return IntegrationType.TALENT_LMS
            case IntegrationTypeDTO.LEARN_DASH:
                return IntegrationType.LEARN_DASH
            case IntegrationTypeDTO.AMAZON_S3:
                return IntegrationType.AMAZON_S3
            case IntegrationTypeDTO.MICROSOFT_AZURE:
                return IntegrationType.MISCROSOFT_AZURE
            case IntegrationTypeDTO.SERVICE_NSW_VOUCHER:
                return IntegrationType.SERVICE_NSW_VOUCHER
            case IntegrationTypeDTO.KRONOS:
                return IntegrationType.KRONOS
            case IntegrationTypeDTO.OKTA:
                return IntegrationType.OKTA
            default:
                throw new IllegalArgumentException("${toString()}")
        }
    }

    IntegrationTypeDTO fromDbType(IntegrationType dataType) {
        if(!dataType) {
            return null
        }
        switch(dataType) {
            case IntegrationType.MOODLE:
                return IntegrationTypeDTO.MOODLE
            case IntegrationType.MAILCHIMP:
                return IntegrationTypeDTO.MAILCHIMP
            case IntegrationType.SURVEY_MONKEY:
                return IntegrationTypeDTO.SURVEY_MONKEY
            case IntegrationType.ALCHEMER:
                return IntegrationTypeDTO.ALCHEMER
            case IntegrationType.XERO:
                return IntegrationTypeDTO.XERO
            case IntegrationType.MYOB:
                return IntegrationTypeDTO.MYOB
            case IntegrationType.CLOUD_ACCESS:
                return IntegrationTypeDTO.CLOUD_ACCESS
            case IntegrationType.CANVAS:
                return IntegrationTypeDTO.CANVAS
            case IntegrationType.MICROPOWER:
                return IntegrationTypeDTO.MICROPOWER
            case IntegrationType.USI_AGENCY:
                return IntegrationTypeDTO.USI_AGENCY
            case IntegrationType.VET_STUDENT_LOANS:
                return IntegrationTypeDTO.VET_STUDENT_LOANS
            case IntegrationType.GOOGLE_CLASSROOM:
                return IntegrationTypeDTO.GOOGLE_CLASSROOM
            case IntegrationType.COASSEMBLE:
                return IntegrationTypeDTO.COASSEMBLE
            case IntegrationType.TALENT_LMS:
                return IntegrationTypeDTO.TALENT_LMS
            case IntegrationType.LEARN_DASH:
                return IntegrationTypeDTO.LEARN_DASH
            case IntegrationType.AMAZON_S3:
                return IntegrationTypeDTO.AMAZON_S3
            case IntegrationType.MISCROSOFT_AZURE:
                return IntegrationTypeDTO.MICROSOFT_AZURE
            case IntegrationType.SERVICE_NSW_VOUCHER:
                return IntegrationTypeDTO.SERVICE_NSW_VOUCHER
            case IntegrationType.KRONOS:
                return IntegrationTypeDTO.KRONOS
            case IntegrationType.OKTA:
                return IntegrationTypeDTO.OKTA
            default:
                throw new IllegalArgumentException("$dataType.displayName")
        }
    }
}