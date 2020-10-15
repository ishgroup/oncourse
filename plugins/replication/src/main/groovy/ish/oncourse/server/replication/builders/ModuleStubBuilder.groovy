package ish.oncourse.server.replication.builders

import ish.oncourse.server.cayenne.Module
import ish.oncourse.webservices.v21.stubs.replication.ModuleStub

class ModuleStubBuilder extends AbstractAngelStubBuilder<Module, ModuleStub> {

    @Override
    protected ModuleStub createFullStub(Module entity) {
        def moduleStub = new ModuleStub()
        moduleStub.setCreated(entity.getCreatedOn())
        moduleStub.setCreditPoints(entity.getCreditPoints())
        moduleStub.setExpiryDays(entity.getExpiryDays())
        moduleStub.setFieldOfEducation(entity.getFieldOfEducation())
        moduleStub.setType(entity.getType().getDatabaseValue())
        moduleStub.setModified(entity.getModifiedOn())
        moduleStub.setNationalCode(entity.getNationalCode())
        moduleStub.setSpecialisation(entity.getSpecialization())
        moduleStub.setTitle(entity.getTitle())
        return moduleStub
    }
}
