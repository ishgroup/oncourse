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

package ish.oncourse.server.api.v1.function

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.transform.CompileStatic
import ish.oncourse.types.FundingStatus
import ish.oncourse.server.api.BidiMap
import ish.oncourse.server.api.v1.model.AvetmissExportSettingsDTO
import ish.oncourse.server.api.v1.model.FundingStatusDTO
import ish.oncourse.server.api.v1.model.FundingUploadDTO
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.cayenne.FundingUpload

import java.time.ZoneOffset

@CompileStatic
class FundingUploadFunctions {

    static final BidiMap<FundingStatus, FundingStatusDTO> fundingUploadStatusMap = new BidiMap<FundingStatus, FundingStatusDTO>() {{
        put(FundingStatus.EXPORTED, FundingStatusDTO.UNKNOWN)
        put(FundingStatus.SUCCESS, FundingStatusDTO.SUCCESS)
        put(FundingStatus.FAILED, FundingStatusDTO.FAIL)

    }}

    static FundingUploadDTO toRestFundingUpload(FundingUpload dbObj, boolean lastSettings = false) {
        new FundingUploadDTO().with { it ->
            it.id = dbObj.id
            it.created = dbObj.createdOn?.toInstant()?.atZone(ZoneOffset.UTC)?.toLocalDateTime()
            it.status = fundingUploadStatusMap[dbObj.status]
            it.systemUser = "$dbObj.systemUser.firstName $dbObj.systemUser.lastName"
            it.outcomesCount = dbObj.outcomeCount.toInteger()
            it.settings = dbObj.settings ? new ObjectMapper().readValue(dbObj.settings, AvetmissExportSettingsDTO) : null as AvetmissExportSettingsDTO
            it.lastSettings = lastSettings
            it
        }
    }

    static ValidationErrorDTO validateStatus(FundingStatusDTO status) {
        if (status == null) {
            return new ValidationErrorDTO(null, 'status', "Incorrect status for funding upload.")
        }
        null
    }
}
