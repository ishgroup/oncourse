package ish.oncourse.webservices.replication.v18.updaters;

import ish.oncourse.model.Module;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v18.stubs.replication.ModuleStub;

public class ModuleUpdater extends AbstractWillowUpdater<ModuleStub, Module> {

    @Override
    protected void updateEntity(ModuleStub stub, Module entity, RelationShipCallback callback) {
        entity.setCreated(stub.getCreated());
        entity.setCreditPoints(stub.getCreditPoints());
        entity.setDisciplineCode(stub.getDisciplineCode());
        entity.setExpiryDays(stub.getExpiryDays());
        entity.setFieldOfEducation(stub.getFieldOfEducation());
        entity.setIsModule((byte)(stub.isIsModule() ? 1 : 0));
        entity.setModified(stub.getModified());
        entity.setNationalCode(stub.getNationalCode());
        entity.setSpecialization(stub.getSpecialisation());
        entity.setTitle(stub.getTitle());
        entity.setTrainingPackageId(stub.getTrainingPackageId());
    }
}
