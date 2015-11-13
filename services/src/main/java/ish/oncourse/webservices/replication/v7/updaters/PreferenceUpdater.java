package ish.oncourse.webservices.replication.v7.updaters;

import ish.oncourse.model.Preference;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v7.stubs.replication.PreferenceStub;
import org.apache.cayenne.PersistenceState;

public class PreferenceUpdater extends AbstractWillowUpdater<PreferenceStub, Preference> {

	@Override
	protected void updateEntity(PreferenceStub stub, Preference entity, RelationShipCallback callback) {
		if (entity.getId() == null) {
			deleteConcurrentPreferences(stub.getName(), entity.getCollege());
		}
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		entity.setName(stub.getName());
		entity.setValue(stub.getValue());
		entity.setValueString(stub.getValueString());
	}
}
