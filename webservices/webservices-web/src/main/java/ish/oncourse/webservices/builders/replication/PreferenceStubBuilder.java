package ish.oncourse.webservices.builders.replication;

import ish.oncourse.model.Preference;
import ish.oncourse.model.QueuedKey;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.webservices.services.replication.IWillowQueueService;
import ish.oncourse.webservices.v4.stubs.replication.PreferenceStub;

import java.util.Map;

public class PreferenceStubBuilder extends AbstractWillowStubBuilder<Preference, PreferenceStub> {
	
	public PreferenceStubBuilder(Map<QueuedKey, QueuedRecord> queue, IWillowQueueService queueService, IWillowStubBuilder next) {
		super(queue, queueService, next);
	}
	
	@Override
	protected PreferenceStub createFullStub(Preference entity) {
		PreferenceStub stub = new PreferenceStub();
		stub.setAngelId(entity.getAngelId());
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
