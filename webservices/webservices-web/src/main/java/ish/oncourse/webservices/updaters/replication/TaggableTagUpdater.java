package ish.oncourse.webservices.updaters.replication;

import ish.oncourse.model.Tag;
import ish.oncourse.model.Taggable;
import ish.oncourse.model.TaggableTag;
import ish.oncourse.webservices.v4.stubs.replication.ReplicatedRecord;
import ish.oncourse.webservices.v4.stubs.replication.TaggableTagStub;

import java.util.List;

public class TaggableTagUpdater extends AbstractWillowUpdater<TaggableTagStub, TaggableTag> {

	@Override
	protected void updateEntity(TaggableTagStub stub, TaggableTag entity, List<ReplicatedRecord> result) {
		entity.setAngelId(stub.getAngelId());
		entity.setCollege(college);
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		entity.setTag(updateRelationShip(stub.getTagId(), Tag.class, result));
		entity.setTaggable(updateRelationShip(stub.getTaggableId(), Taggable.class, result));
	}
}
