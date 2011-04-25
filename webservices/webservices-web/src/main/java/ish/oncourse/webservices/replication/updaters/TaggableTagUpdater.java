package ish.oncourse.webservices.replication.updaters;

import ish.oncourse.model.Tag;
import ish.oncourse.model.Taggable;
import ish.oncourse.model.TaggableTag;
import ish.oncourse.webservices.v4.stubs.replication.TaggableTagStub;

public class TaggableTagUpdater extends AbstractWillowUpdater<TaggableTagStub, TaggableTag> {

	@Override
	protected void updateEntity(TaggableTagStub stub, TaggableTag entity, RelationShipCallback callback) {
		entity.setAngelId(stub.getAngelId());
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		entity.setTag(callback.updateRelationShip(stub.getTagId(), Tag.class));
		entity.setTaggable(callback.updateRelationShip(stub.getTaggableId(), Taggable.class));
	}
}
