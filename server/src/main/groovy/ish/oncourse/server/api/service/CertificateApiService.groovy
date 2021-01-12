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

package ish.oncourse.server.api.service

import com.google.inject.Inject
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import ish.common.types.KeyCode
import ish.common.types.Mask
import ish.common.types.OutcomeStatus
import ish.common.types.UsiStatus
import ish.oncourse.function.GetContactFullName
import ish.oncourse.server.api.dao.CertificateDao
import ish.oncourse.server.api.dao.CertificateOutcomeDao
import ish.oncourse.server.api.dao.ContactDao
import ish.oncourse.server.api.dao.EnrolmentDao
import ish.oncourse.server.api.dao.OutcomeDao
import ish.oncourse.server.api.dao.QualificationDao
import ish.oncourse.server.api.v1.model.CertificateCreateForEnrolmentsRequestDTO
import ish.oncourse.server.api.v1.model.CertificateDTO
import ish.oncourse.server.api.v1.model.CertificateOutcomeDTO
import ish.oncourse.server.cayenne.Certificate
import ish.oncourse.server.cayenne.CertificateOutcome
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.cayenne.Outcome
import ish.oncourse.server.cayenne.Course
import ish.oncourse.server.print.CertificatePrintStatus
import ish.oncourse.server.security.api.IPermissionService
import ish.oncourse.server.users.SystemUserService
import ish.util.LocalDateUtils
import ish.validation.ValidationResult
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.SelectById
import org.apache.cayenne.validation.ValidationFailure
import static org.apache.commons.lang3.StringUtils.isEmpty
import static org.apache.commons.lang3.StringUtils.trimToNull

import java.time.LocalDate

class CertificateApiService extends EntityApiService<CertificateDTO, Certificate, CertificateDao> {

    @Inject
    private CertificateOutcomeDao certificateOutcomeDao

    @Inject
    private ContactDao contactDao

    @Inject
    private OutcomeDao outcomeDao

    @Inject
    private QualificationDao qualificationDao

    @Inject
    private SystemUserService systemUserService

    @Inject
    private EnrolmentDao enrolmentDao

    @Override
    Class<Certificate> getPersistentClass() {
        return Certificate
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    @Override
    CertificateDTO toRestModel(Certificate certificate) {
        new CertificateDTO().with { certificateDTO ->
            certificateDTO.id = certificate.id
            certificate.student.contact.with { contact ->
                certificateDTO.studentContactId = contact.id
                certificateDTO.studentName = contact.with { GetContactFullName.valueOf(it, true).get() }
                certificateDTO.studentSuburb = contact.suburb
                certificateDTO.studentDateOfBirth = LocalDateUtils.dateToValue(contact.dateOfBirth)
            }
            certificate.qualification?.with { qualification ->
                certificateDTO.qualificationId = qualification.id
                certificateDTO.nationalCode = qualification.nationalCode
                certificateDTO.title = qualification.title
                certificateDTO.level = qualification.level
            }

            certificateDTO.isQualification = certificate.isQualification
            certificateDTO.outcomes = certificate.certificateOutcomes.collect { it ->
                new CertificateOutcomeDTO().with { co ->
                    co.id = it.outcome.id
                    co.issueDate = it.outcome.endDate
                    co.code = it.outcome.module.nationalCode
                    co.name = it.outcome.module.title
                    co.status = it.outcome.status.displayName
                    co
                }
            }
            certificateDTO.privateNotes = certificate.privateNotes
            certificateDTO.publicNotes = certificate.publicNotes
            certificateDTO.awardedOn = certificate.awardedOn
            certificateDTO.expiryDate = certificate.expiryDate
            certificateDTO.issuedOn = certificate.issuedOn
            certificateDTO.number = certificate.certificateNumber
            certificateDTO.printedOn = certificate.printedOn
            certificateDTO.revokedOn = certificate.revokedOn
            certificateDTO.code = certificate.printedOn != null && certificate.revokedOn == null ? certificate.uniqueCode : null
            certificateDTO.createdOn = LocalDateUtils.dateToTimeValue(certificate.createdOn)
            certificateDTO.modifiedOn = LocalDateUtils.dateToTimeValue(certificate.modifiedOn)
            certificateDTO
        }
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    @Override
    Certificate toCayenneModel(CertificateDTO certificateDTO, Certificate certificate) {
        if (certificate.newRecord) {
            certificate.student = contactDao.getById(certificate.context, certificateDTO.studentContactId).student
        }
        if (certificate.printedOn == null) {
            if (certificateDTO.qualificationId) {
                certificate.qualification = qualificationDao.getById(certificate.context, certificateDTO.qualificationId)
            }
            certificate.isQualification = certificateDTO.isQualification
            updateOutcomes(certificate, certificateDTO.outcomes)
        }

        certificate.privateNotes = trimToNull(certificateDTO.privateNotes)
        certificate.publicNotes = trimToNull(certificateDTO.publicNotes)
        certificate.awardedOn = certificateDTO.awardedOn
        certificate.expiryDate = certificateDTO.expiryDate
        certificate.issuedOn = certificateDTO.issuedOn

        certificate
    }

    @Override
    void validateModelBeforeSave(CertificateDTO certificateDTO, ObjectContext context, Long id) {
        if (!certificateDTO.studentContactId) {
            validator.throwClientErrorException(id, 'studentContactId', 'Student is required.')
        } else if (!contactDao.getById(context, certificateDTO.studentContactId)?.student) {
            validator.throwClientErrorException(id, 'studentContactId', "Student  with contact id=$certificateDTO.studentContactId doesn't exist.")
        }

        if (!certificateDTO.awardedOn) {
            validator.throwClientErrorException(id, 'awardedOn', 'Date of awarding is required.')
        }

        if (certificateDTO.isQualification && !certificateDTO.qualificationId) {
            validator.throwClientErrorException(id, 'qualificationId', 'Qualification is required.')
        } else if (certificateDTO.qualificationId && !qualificationDao.getById(context, certificateDTO.qualificationId)) {
            validator.throwClientErrorException(id, 'qualificationId', "Qualification  with id=$certificateDTO.qualificationId doesn't exist.")
        }

        if (certificateDTO.outcomes.empty) {
            validator.throwClientErrorException(id, 'outcomes', 'Modules is required.')
        } else {
            certificateDTO.outcomes.each { o ->
                if (!o.id) {
                    validator.throwClientErrorException(id, 'outcomes', 'Outcome id is required.')
                } else {
                    Outcome outcome = outcomeDao.getById(context, o.id)
                    if (!outcome) {
                        validator.throwClientErrorException(id, 'outcomes', "Outcome with id=$o.id doesn't exist.")
                    } else if (!outcome.module) {
                        validator.throwClientErrorException(id, 'outcomes', "Only vet outcomes can be attached to certificate.")
                    } else if (![outcome.enrolment?.student?.contact?.id, outcome.priorLearning?.student?.contact?.id].contains(certificateDTO.studentContactId)) {
                        validator.throwClientErrorException(id, 'outcomes', "Outcome with id=$o.id doesn't relate to this student.")
                    } else if (OutcomeStatus.STATUS_NOT_SET.equals(outcome.status)) {
                        validator.throwClientErrorException(id, 'outcomes', "Outcome with code: '$outcome.module.nationalCode' has NOT_SET status.")
                    }
                }
            }
        }
    }

    @Override
    void validateModelBeforeRemove(Certificate certificate) {
        if (certificate.printedOn) {
            validator.throwClientErrorException(certificate.id, 'printedOn', 'Printed certificates cannot be deleted.')
        }
    }

    private void updateOutcomes(Certificate certificate, List<CertificateOutcomeDTO> outcomeDTOs){
        List<Long> relationsToSave = outcomeDTOs*.id ?: [] as List<Long>
        certificate.context.deleteObjects(certificate.certificateOutcomes.findAll { !relationsToSave.contains(it.outcome.id) })
        outcomeDTOs.findAll { !certificate.certificateOutcomes*.outcome*.id.contains(it.id) }.each { it ->
            certificateOutcomeDao.newObject(certificate.context).with { certificateOutcome ->
                certificateOutcome.outcome = outcomeDao.getById(certificate.context, it.id)
                certificateOutcome.certificate = certificate
                certificateOutcome
            }
        }
    }

    void revoke(Long id, String reason) {
        ObjectContext context = cayenneService.newContext
        Certificate cayenneModel = getEntityAndValidateExistence(context, id)

        // if certificated was revoked before - skip it
        if (cayenneModel.revokedOn != null) {
            validator.throwClientErrorException(id, 'reason', "Certificate with id = " + id + " was revoked before.")
        }

        if (isEmpty(reason.trim())) {
            validator.throwClientErrorException(id, 'reason', 'Reason for revoking is required.')
        }

        StringBuilder newPrivateNotes = new StringBuilder()
                .append('-- REVOKED ON ')
                .append( LocalDate.now().toString())
                .append(' BY ')
                .append(systemUserService.currentUser.email)
                .append(' --')
                .append(System.getProperty('line.separator'))
                .append(trimToNull(reason))
                .append(System.getProperty('line.separator'))
                .append(System.getProperty('line.separator'))

        if (cayenneModel.privateNotes != null) {
            newPrivateNotes.append(cayenneModel.privateNotes)
        }

        cayenneModel.privateNotes = newPrivateNotes.toString()
        cayenneModel.revokedOn = LocalDate.now()

        context.commitChanges()
    }

    CertificatePrintStatus validateForPrint(Certificate certificate) {
        if (certificate.student.usi == null ) {
            CertificatePrintStatus.NO_USI
        } else if (!(certificate.student.usiStatus in [UsiStatus.VERIFIED, UsiStatus.EXEMPTION, UsiStatus.INTERNATIONAL])) {
            CertificatePrintStatus.USI_NOT_VERIFIED
        } else {
            CertificatePrintStatus.OK
        }
    }

    List<Long> createCertificates(CertificateCreateForEnrolmentsRequestDTO createRequest) {

        ObjectContext context = cayenneService.newContext
        List<Certificate> processedCertificates = []
        boolean createStatementOfAtteiment = createRequest.createStatementOfAtteiment


        createRequest.enrolmentIds
            .collect { enrolmentDao.getById(context, it) }
            .findAll { it.vet }
            .forEach { Enrolment e ->

                boolean create = true
                boolean fullQualification = false
                Course course = e.courseClass.course

                if (course.isSufficientForQualification) {
                    if (e.outcomes.findAll { it.status in OutcomeStatus.STATUSES_VALID_FOR_CERTIFICATE }.size() == e.outcomes.size()) {
                        fullQualification = true
                    } else if (createStatementOfAtteiment) {
                        fullQualification = false
                    } else {
                        create = false
                    }
                } else {
                    fullQualification = false
                }

                if (!e.outcomes.any {o -> !OutcomeStatus.STATUS_NOT_SET.equals(o.status)}) {
                    create = false
                }

                if (create) {
                    Certificate certificate = context.newObject(Certificate)
                    certificate.setStudent(e.student)
                    certificate.setQualification(course.qualification)
                    certificate.setIsQualification(fullQualification)
                    certificate.setAwardedOn(new Date().clearTime().toLocalDate())

                    e.outcomes.findAll { o -> !OutcomeStatus.STATUS_NOT_SET.equals(o.status)}.each { o ->
                        CertificateOutcome certificateOutcome = context.newObject(CertificateOutcome)
                        certificateOutcome.outcome = o
                        certificateOutcome.certificate = certificate
                    }
                    processedCertificates << certificate

                }
            }

        context.commitChanges()
        return  processedCertificates.collect {it.id}

    }
}
