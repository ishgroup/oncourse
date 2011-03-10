package ish.oncourse.webservices.updaters.replication;

import java.util.List;

import ish.oncourse.model.College;
import ish.oncourse.model.Tag;
import ish.oncourse.model.Taggable;
import ish.oncourse.model.TaggableTag;
import ish.oncourse.webservices.services.replication.IWillowQueueService;
import ish.oncourse.webservices.v4.stubs.replication.ReplicatedRecord;
import ish.oncourse.webservices.v4.stubs.replication.TaggableTagStub;

public class TaggableTagUpdater extends AbstractWillowUpdater<TaggableTagStub, TaggableTag> {
	
	public TaggableTagUpdater(College college, IWillowQueueService queueService, @SuppressWarnings("rawtypes") IWillowUpdater next) {
		super(college, queueService, next);
	}

	@Override
	protected void updateEntity(TaggableTagStub stub, TaggableTag entity, List<ReplicatedRecord> relationStubs) {
		entity.setAngelId(stub.getAngelId());
		entity.setCollege(getCollege(entity.getObjectContext()));
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		entity.setTag((Tag) updateRelatedEntity(entity.getObjectContext(), stub.getTag(), relationStubs));
		entity.setTaggable((Taggable) updateRelatedEntity(entity.getObjectContext(), stub.getTaggable(), relationStubs));
	}	
}
