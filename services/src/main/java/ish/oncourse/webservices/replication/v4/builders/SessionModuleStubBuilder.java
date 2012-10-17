package ish.oncourse.webservices.replication.v4.builders;

import ish.oncourse.model.SessionModule;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationStub;

public class SessionModuleStubBuilder extends AbstractWillowStubBuilder<SessionModule, ReplicationStub> {

	@Override
	protected ReplicationStub createFullStub(SessionModule entity) {
		//do nothing because we need to cleanup the generated queued record but angel side not able to parse this stub
		//ReplicationStub used because this is a V5 feature and have no any V4 stub to pass 
		//so we need to have this builder for cases when something may create or update this entity
		return null;
	}

}
