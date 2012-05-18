package ish.oncourse.webservices.replication.builders;

import ish.oncourse.model.SystemUser;
import ish.oncourse.webservices.v4.stubs.replication.SystemUserStub;

public class SystemUserStubBuilder extends AbstractWillowStubBuilder<SystemUser, SystemUserStub> {

	@Override
	protected SystemUserStub createFullStub(SystemUser entity) {
		SystemUserStub stub = new SystemUserStub();
		stub.setDefaultAdministrationCentreId(entity.getDefaultAdministrationCentreId());
		stub.setEditCMS(entity.getCanEditCMS());
		stub.setEditTara(entity.getCanEditTara());
		stub.setEmail(entity.getEmail());
		stub.setFirstName(entity.getFirstName());
		stub.setIsActive(entity.getIsActive());
		stub.setIsAdmin(entity.getIsAdmin());
		stub.setLogin(entity.getLogin());
		stub.setPassword(entity.getPassword());
		stub.setSurname(entity.getLastName());
		//stub.setLastLoginIP(entity.getLastLoginIP());
		//stub.setLastLoginOn(entity.getLastLoginOn());
		//TODO: change stub versions
		return stub;
	}

}
