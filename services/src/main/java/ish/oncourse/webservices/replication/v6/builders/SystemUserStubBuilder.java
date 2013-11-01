package ish.oncourse.webservices.replication.v6.builders;

import ish.oncourse.model.SystemUser;
import ish.oncourse.webservices.replication.v4.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v6.stubs.replication.SystemUserStub;

public class SystemUserStubBuilder extends AbstractWillowStubBuilder<SystemUser, SystemUserStub> {

	@Override
	protected SystemUserStub createFullStub(SystemUser entity) {
		SystemUserStub stub = new SystemUserStub();
		stub.setDefaultAdministrationCentreId(entity.getDefaultAdministrationCentreId());
		stub.setEditCMS(entity.getEditCMS());
		stub.setEditTara(entity.getEditTara());
		stub.setEmail(entity.getEmail());
		stub.setFirstName(entity.getFirstName());
		stub.setIsActive(entity.getIsActive());
		stub.setIsAdmin(entity.getIsAdmin());
		stub.setLogin(entity.getLogin());
		stub.setPassword(entity.getPassword());
		stub.setSurname(entity.getSurname());
		stub.setLastLoginIP(entity.getLastLoginIP());
		stub.setLastLoginOn(entity.getLastLoginOn());
		stub.setModified(entity.getModified());
		return stub;
	}

}
