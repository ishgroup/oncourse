package ish.oncourse.webservices.replication.updaters;

import ish.oncourse.model.SystemUser;
import ish.oncourse.webservices.v4.stubs.replication.SystemUserStub;

public class SystemUserUpdater extends AbstractWillowUpdater<SystemUserStub, SystemUser> {

	@Override
	protected void updateEntity(SystemUserStub stub, SystemUser entity, RelationShipCallback callback) {
		entity.setCanEditCMS(stub.isEditCMS());
		entity.setCanEditTara(stub.isEditTara());
		entity.setDefaultAdministrationCentreId(stub.getDefaultAdministrationCentreId());
		entity.setEmail(stub.getEmail());
		entity.setFirstName(stub.getFirstName());
		entity.setIsActive(stub.isIsActive());
		entity.setIsAdmin(stub.isIsAdmin());
		//entity.setLastLoginIP(stub.getLastLoginIP());
		//entity.setLastLoginOn(stub.getLastLoginOn());
		entity.setLastName(stub.getSurname());
		entity.setLogin(stub.getLogin());
		entity.setPassword(stub.getPassword());
		//TODO: change stub versions
	}

}
