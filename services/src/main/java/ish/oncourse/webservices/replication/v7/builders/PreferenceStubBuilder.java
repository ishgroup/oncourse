package ish.oncourse.webservices.replication.v7.builders;

import ish.oncourse.model.Preference;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v7.stubs.replication.PreferenceStub;

public class PreferenceStubBuilder extends AbstractWillowStubBuilder<Preference, PreferenceStub> {

	@Override
	protected PreferenceStub createFullStub(Preference entity) {
		PreferenceStub stub = new PreferenceStub();
		stub.setCreated(entity.getCreated());
		stub.setModified(entity.getModified());
		stub.setName(entity.getName());
		stub.setValue(entity.getValue());
		stub.setValueString(entity.getValueString());
		return stub;
	}
}
