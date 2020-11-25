package ish.oncourse.webservices.replication.v23.updaters;

import ish.common.types.QualificationType;
import ish.common.types.TypesUtil;
import ish.oncourse.model.Qualification;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v23.stubs.replication.QualificationStub;

public class QualificationUpdater extends AbstractWillowUpdater<QualificationStub, Qualification> {

    @Override
    protected void updateEntity(QualificationStub stub, Qualification entity, RelationShipCallback callback) {
        entity.setAnzsco(stub.getAnzsco());
        entity.setCreated(stub.getCreated());
        entity.setFieldOfEducation(stub.getFieldOfEducation());
        entity.setIsAccreditedCourse(TypesUtil.getEnumForDatabaseValue(stub.getIsAccreditedCourse(), QualificationType.class));
        entity.setLevel(stub.getLevel());
        entity.setModified(stub.getModified());
        entity.setNationalCode(stub.getNationalCode());
        entity.setNewApprenticeship(stub.getNewApprentices());
        entity.setNominalHours(stub.getNominalHours());
        entity.setReviewDate(stub.getReviewDate());
        entity.setSpecialization(stub.getSpecialisation());
        entity.setTitle(stub.getTitle());
        entity.setTrainingPackageId(stub.getTrainingPackageId());
    }
}
