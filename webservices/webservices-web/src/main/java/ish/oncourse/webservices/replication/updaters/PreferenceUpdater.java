package ish.oncourse.webservices.replication.updaters;

import ish.oncourse.model.Preference;
import ish.oncourse.webservices.v4.stubs.replication.PreferenceStub;

public class PreferenceUpdater extends AbstractWillowUpdater<PreferenceStub, Preference> {

	@Override
	protected void updateEntity(PreferenceStub stub, Preference entity, RelationShipCallback callback) {
		entity.setAngelId(stub.getAngelId());
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		entity.setName(stub.getName());
		entity.setExplanation(stub.getExplanation());
		entity.setSqlType(stub.getSqlType());
		entity.setValue(stub.getValue());
		entity.setValueString(stub.getValueString());
	}
}
