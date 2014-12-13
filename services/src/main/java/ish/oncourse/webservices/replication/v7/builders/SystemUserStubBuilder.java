package ish.oncourse.webservices.replication.v7.builders;

import ish.oncourse.model.Site;
import ish.oncourse.model.SystemUser;
import ish.oncourse.webservices.replication.v4.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v7.stubs.replication.SystemUserStub;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;

import java.util.List;

public class SystemUserStubBuilder extends AbstractWillowStubBuilder<SystemUser, SystemUserStub> {

	@Override
	protected SystemUserStub createFullStub(SystemUser entity) {
		SystemUserStub stub = new SystemUserStub();
		stub.setDefaultAdministrationCentreId(getSiteWillowId(entity));
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

	//temporary workaround for properly replication  SystemUsers on angel side:
	//set Site.willowId instead of angelId.
	//Currently willow SystemUser entity stores Site.angelId - it is not relationship at all. Will be fixed in future. 
	private Long getSiteWillowId(SystemUser entity) {
		SelectQuery query = new SelectQuery(Site.class,
				ExpressionFactory.matchExp(Site.ANGEL_ID_PROPERTY, entity.getDefaultAdministrationCentreId())
						.andExp(ExpressionFactory.matchExp(Site.COLLEGE_PROPERTY, entity.getCollege())));

		List<Site> sites = entity.getObjectContext().performQuery(query);

		return sites.isEmpty() || sites.size() > 1 ? null : sites.get(0).getId();
	}
}
