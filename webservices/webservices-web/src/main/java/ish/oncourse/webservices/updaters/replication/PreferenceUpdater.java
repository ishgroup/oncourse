package ish.oncourse.webservices.updaters.replication;

import ish.oncourse.model.College;
import ish.oncourse.model.Preference;
import ish.oncourse.webservices.services.replication.IWillowQueueService;
import ish.oncourse.webservices.v4.stubs.replication.PreferenceStub;
import ish.oncourse.webservices.v4.stubs.replication.ReplicatedRecord;

import java.util.List;

public class PreferenceUpdater extends AbstractWillowUpdater<PreferenceStub, Preference> {
	
	public PreferenceUpdater(College college, IWillowQueueService queueService, @SuppressWarnings("rawtypes") IWillowUpdater next) {
		super(college, queueService, next);
	}
	
	@Override
	protected void updateEntity(PreferenceStub stub, Preference entity, List<ReplicatedRecord> relationStubs) {
		entity.setAngelId(stub.getAngelId());
		entity.setCollege(getCollege(entity.getObjectContext()));
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		entity.setName(stub.getName());
		entity.setExplanation(stub.getExplanation());
		entity.setSqlType(stub.getSqlType());
		entity.setValue(stub.getValue());
		entity.setValueString(stub.getValueString());
	}
}
