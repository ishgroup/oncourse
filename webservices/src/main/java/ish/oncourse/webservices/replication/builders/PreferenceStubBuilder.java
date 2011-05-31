package ish.oncourse.webservices.replication.builders;

import ish.oncourse.model.Preference;
import ish.oncourse.webservices.v4.stubs.replication.PreferenceStub;

public class PreferenceStubBuilder extends AbstractWillowStubBuilder<Preference, PreferenceStub> {

	@Override
	protected PreferenceStub createFullStub(Preference entity) {
		PreferenceStub stub = new PreferenceStub();
		stub.setCreated(entity.getCreated());
		stub.setModified(entity.getModified());
		stub.setExplanation(entity.getExplanation());
		stub.setName(entity.getName());
		stub.setValue(entity.getValue());
		stub.setSqlType(entity.getSqlType());
		stub.setValueString(entity.getValueString());
		return stub;
	}
}
