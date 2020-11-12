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

package ish.oncourse.server.export.avetmiss8

import groovy.transform.CompileStatic
import ish.oncourse.common.ExportJurisdiction
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.export.avetmiss.AvetmissExportResult
import org.apache.commons.lang3.StringUtils
/**
 * AVETMISS export - file 010 - training organisation details.
 */
@CompileStatic
class Avetmiss010Factory extends AvetmissFactory {
    
    Avetmiss010Factory(AvetmissExportResult result, ExportJurisdiction jurisdiction, PreferenceController preferenceController) {
        super(result, jurisdiction, preferenceController)
    }

    Avetmiss010Line createLine() {
        Avetmiss010Line line = new Avetmiss010Line(jurisdiction)
        line.setIdentifier("one") // there can only be one output row

        // training organisation type identifier p117
        // Secondary school
        // 21 School - Government
        // 23 School - Australian Technical College
        // 25 School - Catholic
        // 27 School - Independent
        // TAFE
        // 31 Technical and Further Education institute
        // University
        // 41 University - Government
        // 43 University - Non-Government Catholic
        // 45 University - Non-Government Independent
        // Enterprise
        // 51 Enterprise - Government
        // 53 Enterprise - Non-government
        // Community-based adult education
        // 61 Community-based adult education provider
        // Other training provider
        // 91 Education/training business or centre: Privately operated registered training organisation
        // 93 Professional association
        // 95 Industry association
        // 97 Equipment and/or product manufacturer or supplier
        // 99 Other - not elsewhere classified
        def toi = ExportJurisdiction.QLD == jurisdiction && StringUtils.isNotBlank(preferenceController.getAvetmissQldIdentifier()) ?
                preferenceController.getAvetmissQldIdentifier() : preferenceController.getAvetmissID()
        line.setTrainingOrganisationIdentifier(toi)
        line.setTrainingOrganisationName(preferenceController.getAvetmissCollegeName())

        line.setContactName(preferenceController.getAvetmissContactName())
        line.setTelephoneNumber(preferenceController.getAvetmissPhone())
        line.setFacsimileNumber(preferenceController.getAvetmissFax())
        line.setEmailAddress(preferenceController.getAvetmissEmail())

        line.setPostCode(preferenceController.getAvetmissPostcode())

        result.avetmiss010Lines.putIfAbsent(line.identifier, line)
        return line
    }
    
}
