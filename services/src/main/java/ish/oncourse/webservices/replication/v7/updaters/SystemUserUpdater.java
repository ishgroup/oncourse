package ish.oncourse.webservices.replication.v7.updaters;

import ish.oncourse.model.Site;
import ish.oncourse.model.SystemUser;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v7.stubs.replication.SystemUserStub;
import org.apache.commons.lang.StringUtils;

public class SystemUserUpdater extends AbstractWillowUpdater<SystemUserStub, SystemUser> {

	@Override
	protected void updateEntity(SystemUserStub stub, SystemUser entity, RelationShipCallback callback) {
		entity.setEditCMS(Boolean.TRUE.equals(stub.isEditCMS()));
		entity.setEditTara(Boolean.TRUE.equals(stub.isEditTara()));
		entity.setDefaultAdministrationCentre(callback.updateRelationShip(stub.getDefaultAdministrationCentreId(), Site.class));
		entity.setEmail(stub.getEmail());
		entity.setFirstName(StringUtils.trimToNull(stub.getFirstName()) != null ? stub.getFirstName() : StringUtils.EMPTY);
		entity.setIsActive(Boolean.TRUE.equals(stub.isIsActive()));
		entity.setIsAdmin(Boolean.TRUE.equals(stub.isIsAdmin()));
		entity.setLastLoginIP(StringUtils.trimToNull(stub.getLastLoginIP()) != null ? stub.getLastLoginIP() : "unknown");
		entity.setLastLoginOn(stub.getLastLoginOn());
		entity.setSurname(StringUtils.trimToNull(stub.getSurname()) != null ? stub.getSurname() : StringUtils.EMPTY);
		entity.setLogin(StringUtils.trimToNull(stub.getLogin()) != null ? stub.getLogin() : StringUtils.EMPTY);
		entity.setPassword(StringUtils.trimToNull(stub.getPassword()) != null ? stub.getPassword() : StringUtils.EMPTY);
	}

}
