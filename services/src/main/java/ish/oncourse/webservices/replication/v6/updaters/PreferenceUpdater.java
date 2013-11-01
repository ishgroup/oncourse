package ish.oncourse.webservices.replication.v6.updaters;

import ish.oncourse.model.Preference;
import ish.oncourse.webservices.replication.v4.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.v4.updaters.RelationShipCallback;
import ish.oncourse.webservices.v6.stubs.replication.PreferenceStub;

public class PreferenceUpdater extends AbstractWillowUpdater<PreferenceStub, Preference> {

	@Override
	protected void updateEntity(PreferenceStub stub, Preference entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		entity.setName(stub.getName());
		entity.setValue(stub.getValue());
		entity.setValueString(stub.getValueString());
	}
}
