/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.server.replication.builders

import ish.oncourse.server.cayenne.Qualification
import ish.oncourse.webservices.v21.stubs.replication.QualificationStub
import ish.util.LocalDateUtils

class QualificationStubBuilder extends AbstractAngelStubBuilder<Qualification, QualificationStub> {

    @Override
    protected QualificationStub createFullStub(Qualification entity) {
        def qualificationStub = new QualificationStub()
        qualificationStub.setAnzsco(entity.getAnzsco())
        qualificationStub.setCreated(entity.getCreatedOn())
        qualificationStub.setFieldOfEducation(entity.getFieldOfEducation())
        qualificationStub.setIsAccreditedCourse(entity.getType().getDatabaseValue())
        qualificationStub.setLevel(entity.getLevel())
        qualificationStub.setModified(entity.getModifiedOn())
        qualificationStub.setNationalCode(entity.getNationalCode())
        qualificationStub.setNewApprentices(entity.getNewApprenticeship())
        if (entity.getNominalHours() != null) {
            qualificationStub.setNominalHours(entity.getNominalHours().floatValue())
        }
        qualificationStub.setReviewDate(LocalDateUtils.valueToDate(entity.getReviewDate()))
        qualificationStub.setSpecialisation(entity.getSpecialization())
        qualificationStub.setTitle(entity.getTitle())

        if (entity.getTrainingPackage() != null) {
            qualificationStub.setTrainingPackageId(entity.getTrainingPackage().getWillowId())
        }
        return qualificationStub
    }
}
