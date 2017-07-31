package ish.oncourse.webservices.replication.v16.builders;

import ish.oncourse.model.SessionModule;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v16.stubs.replication.SessionModuleStub;

public class SessionModuleStubBuilder extends AbstractWillowStubBuilder<SessionModule, SessionModuleStub> {

	@Override
	protected SessionModuleStub createFullStub(SessionModule entity) {
		SessionModuleStub stub = new SessionModuleStub();
		stub.setCreated(entity.getCreated());
		stub.setModified(entity.getModified());
		stub.setModuleId(entity.getModule().getId());
		stub.setSessionId(entity.getSession().getId());
		return stub;
	}

}
