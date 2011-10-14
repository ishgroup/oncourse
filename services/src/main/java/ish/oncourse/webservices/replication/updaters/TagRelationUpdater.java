package ish.oncourse.webservices.replication.updaters;

import ish.oncourse.model.Tag;
import ish.oncourse.model.Taggable;
import ish.oncourse.model.TaggableTag;
import ish.oncourse.webservices.v4.stubs.replication.TagRelationStub;

public class TagRelationUpdater extends AbstractWillowUpdater<TagRelationStub, Taggable> {

	@Override
	protected void updateEntity(TagRelationStub stub, Taggable entity, RelationShipCallback callback) {
		
		entity.setEntityAngelId(stub.getEntityAngelId());
		entity.setEntityIdentifier(stub.getEntityName());
		entity.setEntityWillowId(stub.getEntityWillowId());
		entity.setModified(stub.getModified());
		
		TaggableTag taggableTag = entity.getObjectContext().newObject(TaggableTag.class);
		Tag tag = callback.updateRelationShip(stub.getTagId(), Tag.class);
		
		taggableTag.setTag(tag);
		taggableTag.setCollege(tag.getCollege());
		
		entity.addToTaggableTags(taggableTag);
	}
}
