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

import ish.oncourse.server.api.v1.model.AvetmissExportFeeDTO
import ish.oncourse.server.export.avetmiss.AvetmissExport

trait AvetmissExportFeeDTOTrait {


    AvetmissExport getDbType() {
        switch (this as AvetmissExportFeeDTO) {
            case AvetmissExportFeeDTO.FEE_FOR_SERVICE_VET_NON_FUNDED_:
                return AvetmissExport.AVETMISS_VET
            case AvetmissExportFeeDTO.QUEENSLAND:
                return AvetmissExport.AVETMISS_QLD
            case AvetmissExportFeeDTO.NEW_SOUTH_WALES:
                return AvetmissExport.AVETMISS_NSW
            case AvetmissExportFeeDTO.VICTORIA:
                return AvetmissExport.AVETMISS_VIC
            case AvetmissExportFeeDTO.TASMANIA:
                return AvetmissExport.AVETMISS_TAS
            case AvetmissExportFeeDTO.AUSTRALIAN_CAPITAL_TERRITORY:
                return AvetmissExport.AVETMISS_ACT
            case AvetmissExportFeeDTO.WESTERN_AUSTRALIA:
                return AvetmissExport.AVETMISS_WA
            case AvetmissExportFeeDTO.SOUTH_AUSTRALIA:
                return AvetmissExport.AVETMISS_SA
            case AvetmissExportFeeDTO.NORTHERN_TERRITORY:
                return AvetmissExport.AVETMISS_NT
            case AvetmissExportFeeDTO.NO_AUSTRALIAN_STATE_DEFINED:
                return AvetmissExport.AVETMISS_NO_STATE
            case AvetmissExportFeeDTO.NON_VET:
                return AvetmissExport.AVETMISS_NON_VET
            default:
                throw new IllegalArgumentException("${toString()}")
        }
    }

    AvetmissExportFeeDTO fromDbType(AvetmissExport dataType) {
        if(!dataType) {
            return null
        }
        switch (dataType) {
            case AvetmissExport.AVETMISS_VET:
                return AvetmissExportFeeDTO.FEE_FOR_SERVICE_VET_NON_FUNDED_
            case AvetmissExport.AVETMISS_QLD:
                return AvetmissExportFeeDTO.QUEENSLAND
            case AvetmissExport.AVETMISS_NSW:
                return AvetmissExportFeeDTO.NEW_SOUTH_WALES
            case AvetmissExport.AVETMISS_VIC:
                return AvetmissExportFeeDTO.VICTORIA
            case AvetmissExport.AVETMISS_TAS:
                return AvetmissExportFeeDTO.TASMANIA
            case AvetmissExport.AVETMISS_ACT:
                return AvetmissExportFeeDTO.AUSTRALIAN_CAPITAL_TERRITORY
            case AvetmissExport.AVETMISS_WA:
                return AvetmissExportFeeDTO.WESTERN_AUSTRALIA
            case AvetmissExport.AVETMISS_SA:
                return AvetmissExportFeeDTO.SOUTH_AUSTRALIA
            case AvetmissExport.AVETMISS_NT:
                return AvetmissExportFeeDTO.NORTHERN_TERRITORY
            case AvetmissExport.AVETMISS_NO_STATE:
                return AvetmissExportFeeDTO.NO_AUSTRALIAN_STATE_DEFINED
            case AvetmissExport.AVETMISS_NON_VET:
                return AvetmissExportFeeDTO.NON_VET
            default:
                throw new IllegalArgumentException("$dataType.displayName")
        }
    }
}
