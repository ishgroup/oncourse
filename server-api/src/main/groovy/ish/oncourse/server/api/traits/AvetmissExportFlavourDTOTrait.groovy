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

import ish.oncourse.common.ExportJurisdiction
import ish.oncourse.server.api.v1.model.AvetmissExportFlavourDTO

trait AvetmissExportFlavourDTOTrait {


    ExportJurisdiction getDbType() {
        switch (this as AvetmissExportFlavourDTO) {

            case AvetmissExportFlavourDTO.NCVER_STANDARD_AVETMISS_:
                return ExportJurisdiction.PLAIN
            case AvetmissExportFlavourDTO.CSO_COMMUNITY_COLLEGES_:
                return ExportJurisdiction.OLIV
            case AvetmissExportFlavourDTO.STSONLINE_NSW_:
                return ExportJurisdiction.SMART
            case AvetmissExportFlavourDTO.DETCONNECT_QUEENSLAND_:
                return ExportJurisdiction.QLD
            case AvetmissExportFlavourDTO.STELA_SOUTH_AUSTRALIA_:
                return ExportJurisdiction.SA
            case AvetmissExportFlavourDTO.SKILLS_TASMANIA:
                return ExportJurisdiction.TAS
            case AvetmissExportFlavourDTO.SKILLS_VICTORIA:
                return ExportJurisdiction.VIC
            case AvetmissExportFlavourDTO.STARS_WA_:
                return ExportJurisdiction.WA
            case AvetmissExportFlavourDTO.WA_RAPT:
                return ExportJurisdiction.RAPT
            case AvetmissExportFlavourDTO.NORTHERN_TERRITORIES_VET_PROVIDER_PORTAL:
                return ExportJurisdiction.NTVETPP
            case AvetmissExportFlavourDTO.AVETARS_ACT_:
                return ExportJurisdiction.AVETARS
            default:
                throw new IllegalArgumentException("${toString()}")
        }
    }

    AvetmissExportFlavourDTO fromDbType(ExportJurisdiction dataType) {
        if(!dataType) {
            return null
        }
        switch (dataType) {
            case ExportJurisdiction.PLAIN:
                return AvetmissExportFlavourDTO.NCVER_STANDARD_AVETMISS_
            case ExportJurisdiction.OLIV:
                return AvetmissExportFlavourDTO.CSO_COMMUNITY_COLLEGES_
            case ExportJurisdiction.SMART:
                return AvetmissExportFlavourDTO.STSONLINE_NSW_
            case ExportJurisdiction.QLD:
                return AvetmissExportFlavourDTO.DETCONNECT_QUEENSLAND_
            case ExportJurisdiction.SA:
                return AvetmissExportFlavourDTO.STELA_SOUTH_AUSTRALIA_
            case ExportJurisdiction.TAS:
                return AvetmissExportFlavourDTO.SKILLS_TASMANIA
            case ExportJurisdiction.VIC:
                return AvetmissExportFlavourDTO.SKILLS_VICTORIA
            case ExportJurisdiction.WA:
                return AvetmissExportFlavourDTO.STARS_WA_
            case ExportJurisdiction.RAPT:
                return AvetmissExportFlavourDTO.WA_RAPT
            case ExportJurisdiction.NTVETPP:
                return AvetmissExportFlavourDTO.NORTHERN_TERRITORIES_VET_PROVIDER_PORTAL
            case ExportJurisdiction.AVETARS:
                return AvetmissExportFlavourDTO.AVETARS_ACT_
            default:
                throw new IllegalArgumentException("$dataType.displayName")
        }
    }
}
