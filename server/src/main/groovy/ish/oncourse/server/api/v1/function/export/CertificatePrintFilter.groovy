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

package ish.oncourse.server.api.v1.function.export

import ish.common.types.KeyCode
import ish.common.types.Mask
import ish.common.types.OutcomeStatus
import ish.common.types.UsiStatus
import ish.oncourse.server.api.validation.EntityValidator
import ish.oncourse.server.cayenne.Certificate
import ish.oncourse.server.cayenne.SystemUser
import ish.oncourse.server.security.api.IPermissionService
import org.apache.cayenne.CayenneDataObject

/**
 * PrintFilter for Certificates.
 * Revoked or certificates with outcomes which has NOT_SET status won't be printed.
 */
class CertificatePrintFilter extends PrintFilter {

    static final APPLYABLE_ENTITY = Certificate.class.getSimpleName()


    @Override
    String getApplyableName() {
        return APPLYABLE_ENTITY
    }

    @Override
    boolean apply(CayenneDataObject dataObject, IPermissionService permissionService, SystemUser user) {
        if (dataObject instanceof Certificate) {
            dataObject = dataObject as Certificate
            boolean isUserCanPrintCertificatesNoUSI = permissionService.userCan(user, KeyCode.PRINT_CERTIFICATE_WITHOUT_USI, Mask.VIEW)
            boolean isUserCanPrintCertificatesWithUSINotVerified = permissionService.userCan(user, KeyCode.PRINT_CERTIFICATE_WITHOUT_VERIFIED_USI, Mask.VIEW)
            if (dataObject.student.usi == null && !isUserCanPrintCertificatesNoUSI) {
                EntityValidator.throwForbiddenErrorException(null, null, 'Sorry, you have not permission to print certificate for student without USI')
            } else if (!(dataObject.student.usiStatus in [UsiStatus.VERIFIED, UsiStatus.EXEMPTION, UsiStatus.INTERNATIONAL]) && !isUserCanPrintCertificatesWithUSINotVerified) {
                EntityValidator.throwForbiddenErrorException(null, null, 'Sorry, you have not permission to print certificate for student without valid USI')
            }
            boolean canPrintCertificate = true
            canPrintCertificate &= dataObject.revokedOn == null
            canPrintCertificate &= !dataObject.certificateOutcomes.stream()
                    .anyMatch({ outcome -> outcome.outcome.status.equals(OutcomeStatus.STATUS_NOT_SET) })
            return canPrintCertificate
        }
        return false
    }
}
