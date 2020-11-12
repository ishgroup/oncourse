/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.traits

import ish.common.types.ClientIndustryEmploymentType
import ish.oncourse.server.api.v1.model.ClientIndustryEmploymentTypeDTO

trait ClientIndustryEmploymentTypeDTOTrait {

    ClientIndustryEmploymentType getDbType() {
        switch (this as ClientIndustryEmploymentTypeDTO) {
            case ClientIndustryEmploymentTypeDTO.NOT_STATED:
                return ClientIndustryEmploymentType.NOT_SET
            case ClientIndustryEmploymentTypeDTO.AGRICULTURE_FORESTRY_AND_FISHING_A_:
                return ClientIndustryEmploymentType.AGRICULTURE
            case ClientIndustryEmploymentTypeDTO.MINING_B_:
                return ClientIndustryEmploymentType.MINING
            case ClientIndustryEmploymentTypeDTO.MANUFACTURING_C_:
                return ClientIndustryEmploymentType.MANUFACTURING
            case ClientIndustryEmploymentTypeDTO.ELECTRICITY_GAS_WATER_AND_WASTE_SERVICES_D_:
                return ClientIndustryEmploymentType.ELECTRICITY
            case ClientIndustryEmploymentTypeDTO.WHOLESALE_TRADE_F_:
                return ClientIndustryEmploymentType.WHOLESALE
            case ClientIndustryEmploymentTypeDTO.RETAIL_TRADE_G_:
                return ClientIndustryEmploymentType.RETAIL
            case ClientIndustryEmploymentTypeDTO.ACCOMMODATION_AND_FOOD_SERVICES_H_:
                return ClientIndustryEmploymentType.ACCOMODATION
            case ClientIndustryEmploymentTypeDTO.TRANSPORT_POSTAL_AND_WAREHOUSING_I_:
                return ClientIndustryEmploymentType.TRANSPORT
            case ClientIndustryEmploymentTypeDTO.INFORMATION_MEDIA_AND_TELECOMMUNICATIONS_J_:
                return ClientIndustryEmploymentType.MEDIA
            case ClientIndustryEmploymentTypeDTO.FINANCIAL_AND_INSURANCE_SERVICES_K_:
                return ClientIndustryEmploymentType.FINANCIAL
            case ClientIndustryEmploymentTypeDTO.RENTAL_HIRING_AND_REAL_ESTATE_SERVICES_L_:
                return ClientIndustryEmploymentType.RENTAL
            case ClientIndustryEmploymentTypeDTO.PROFESSIONAL_SCIENTIFIC_AND_TECHNICAL_SERVICES_ADMINISTRATIVE_AND_SUPPORT_SERVICES_M_:
                return ClientIndustryEmploymentType.PROFESSIONAL
            case ClientIndustryEmploymentTypeDTO.PUBLIC_ADMINISTRATION_AND_SAFETY_N_:
                return ClientIndustryEmploymentType.ADMIN
            case ClientIndustryEmploymentTypeDTO.EDUCATION_AND_TRAINING_O_:
                return ClientIndustryEmploymentType.EDUCATION
            case ClientIndustryEmploymentTypeDTO.HEALTH_CARE_AND_SOCIAL_ASSISTANCE_P_:
                return ClientIndustryEmploymentType.HEALTH
            case ClientIndustryEmploymentTypeDTO.ARTS_AND_RECREATION_SERVICES_Q_:
                return ClientIndustryEmploymentType.ARTS
            case ClientIndustryEmploymentTypeDTO.OTHER_SERVICES_R_:
                return ClientIndustryEmploymentType.OTHER
            case ClientIndustryEmploymentTypeDTO.CONSTRUCTION_E_:
                return ClientIndustryEmploymentType.CONSTRUCTION
            default:
                throw new IllegalArgumentException("${toString()}")
        }
    }

    ClientIndustryEmploymentTypeDTO fromDbType(ClientIndustryEmploymentType dataType) {
        if(!dataType) {
            return null
        }
        switch (dataType) {
            case ClientIndustryEmploymentType.NOT_SET:
                return ClientIndustryEmploymentTypeDTO.NOT_STATED
            case ClientIndustryEmploymentType.AGRICULTURE:
                return ClientIndustryEmploymentTypeDTO.AGRICULTURE_FORESTRY_AND_FISHING_A_
            case ClientIndustryEmploymentType.MINING:
                return ClientIndustryEmploymentTypeDTO.MINING_B_
            case ClientIndustryEmploymentType.MANUFACTURING:
                return ClientIndustryEmploymentTypeDTO.MANUFACTURING_C_
            case ClientIndustryEmploymentType.ELECTRICITY:
                return ClientIndustryEmploymentTypeDTO.ELECTRICITY_GAS_WATER_AND_WASTE_SERVICES_D_
            case ClientIndustryEmploymentType.WHOLESALE:
                return ClientIndustryEmploymentTypeDTO.WHOLESALE_TRADE_F_
            case ClientIndustryEmploymentType.RETAIL:
                return ClientIndustryEmploymentTypeDTO.RETAIL_TRADE_G_
            case ClientIndustryEmploymentType.ACCOMODATION:
                return ClientIndustryEmploymentTypeDTO.ACCOMMODATION_AND_FOOD_SERVICES_H_
            case ClientIndustryEmploymentType.TRANSPORT:
                return ClientIndustryEmploymentTypeDTO.TRANSPORT_POSTAL_AND_WAREHOUSING_I_
            case ClientIndustryEmploymentType.MEDIA:
                return ClientIndustryEmploymentTypeDTO.INFORMATION_MEDIA_AND_TELECOMMUNICATIONS_J_
            case ClientIndustryEmploymentType.FINANCIAL:
                return ClientIndustryEmploymentTypeDTO.FINANCIAL_AND_INSURANCE_SERVICES_K_
            case ClientIndustryEmploymentType.RENTAL:
                return ClientIndustryEmploymentTypeDTO.RENTAL_HIRING_AND_REAL_ESTATE_SERVICES_L_
            case ClientIndustryEmploymentType.PROFESSIONAL:
                return ClientIndustryEmploymentTypeDTO.PROFESSIONAL_SCIENTIFIC_AND_TECHNICAL_SERVICES_ADMINISTRATIVE_AND_SUPPORT_SERVICES_M_
            case ClientIndustryEmploymentType.ADMIN:
                return ClientIndustryEmploymentTypeDTO.PUBLIC_ADMINISTRATION_AND_SAFETY_N_
            case ClientIndustryEmploymentType.EDUCATION:
                return ClientIndustryEmploymentTypeDTO.EDUCATION_AND_TRAINING_O_
            case ClientIndustryEmploymentType.HEALTH:
                return ClientIndustryEmploymentTypeDTO.HEALTH_CARE_AND_SOCIAL_ASSISTANCE_P_
            case ClientIndustryEmploymentType.ARTS:
                return ClientIndustryEmploymentTypeDTO.ARTS_AND_RECREATION_SERVICES_Q_
            case ClientIndustryEmploymentType.OTHER:
                return ClientIndustryEmploymentTypeDTO.OTHER_SERVICES_R_
            case ClientIndustryEmploymentType.CONSTRUCTION:
                return ClientIndustryEmploymentTypeDTO.CONSTRUCTION_E_
            default:
                throw new IllegalArgumentException("$dataType.displayName")
        }
    }

}
