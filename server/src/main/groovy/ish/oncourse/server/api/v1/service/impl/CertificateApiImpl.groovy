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

package ish.oncourse.server.api.v1.service.impl

import com.google.inject.Inject
import groovy.transform.CompileStatic
import ish.oncourse.aql.AqlService
import ish.oncourse.server.CayenneService
import ish.oncourse.server.api.service.CertificateApiService
import static ish.oncourse.server.api.v1.function.export.ExportFunctions.getSelectedRecords
import ish.oncourse.server.api.v1.model.CertificateDTO
import ish.oncourse.server.api.v1.model.CertificateRevokeRequestDTO
import ish.oncourse.server.api.v1.model.CertificateValidationRequestDTO
import ish.oncourse.server.api.v1.service.CertificateApi
import ish.oncourse.server.api.v1.model.CertificateCreateForEnrolmentsRequestDTO
import ish.oncourse.server.cayenne.Certificate
import ish.oncourse.server.print.CertificatePrintStatus
import org.apache.cayenne.Cayenne
import org.apache.cayenne.ObjectContext

@CompileStatic
class CertificateApiImpl implements CertificateApi {

    @Inject
    private CertificateApiService service

    @Inject
    private AqlService aqlService

    @Inject
    private CayenneService cayenneService

    @Override
    void create(CertificateDTO certificate) {
        service.create(certificate)
    }

    @Override
    List<Long> createForEnrolments(CertificateCreateForEnrolmentsRequestDTO createRequest) {
        return service.createCertificates(createRequest);
    }

    @Override
    CertificateDTO get(Long id) {
        service.get(id)
    }

    @Override
    void remove(Long id) {
        service.remove(id)
    }

    @Override
    void update(Long id, CertificateDTO certificate) {
        service.update(id, certificate)
    }

    @Override
    void revoke(CertificateRevokeRequestDTO revokeRequest) {
        for (Long id : revokeRequest.ids) {
            service.revoke(id, revokeRequest.revokeReason)
        }
    }

    @Override
    String validateForPrint(CertificateValidationRequestDTO request) {

        List<Certificate> certificates = new ArrayList<>()
        boolean hasRevokedCertificates = false

        ObjectContext context = cayenneService.newContext
        getSelectedRecords(Certificate.simpleName,request.filter, request.search, request.tagGroups, request.sorting, aqlService, context)
                .each { record   ->
                        certificates.add(record as Certificate)
                        hasRevokedCertificates |= (record as Certificate).revokedOn != null
                }


        boolean hasNoUsi = false
        boolean hasNotVerifiedUsi = false
        certificates.each { certificate ->
            CertificatePrintStatus validationResult = service.validateForPrint(certificate)
            if (CertificatePrintStatus.NO_USI == validationResult) {
                hasNoUsi = true
            } else if (CertificatePrintStatus.USI_NOT_VERIFIED == validationResult) {
                hasNotVerifiedUsi = true
            }
        }
        StringBuilder builder = new StringBuilder("")
        if (hasNoUsi) {
            builder.append("One of selected users has no USI.\n")
        }
        if (hasNotVerifiedUsi) {
            builder.append("One of selected users has not verified USI.\n")
        }
        if (hasRevokedCertificates) {
            builder.append("One of selected certificates was revoked. Revoked certificates won't be printed.\n")
        }
        return builder.toString()
    }
}
