package ish.oncourse.webservices.replication.v7.builders;

import ish.oncourse.model.Site;
import ish.oncourse.model.SystemUser;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v7.stubs.replication.SystemUserStub;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;

import java.util.List;

public class SystemUserStubBuilder extends AbstractWillowStubBuilder<SystemUser, SystemUserStub> {

	@Override
	protected SystemUserStub createFullStub(SystemUser entity) {
		SystemUserStub stub = new SystemUserStub();
		Site defaultAdministrationCentre = entity.getDefaultAdministrationCentre();
		if (defaultAdministrationCentre != null) {
			stub.setDefaultAdministrationCentreId(defaultAdministrationCentre.getId());
		}
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
