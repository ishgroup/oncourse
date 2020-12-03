/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.builders

import ish.oncourse.server.cayenne.Module
import ish.oncourse.webservices.v23.stubs.replication.ModuleStub

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
