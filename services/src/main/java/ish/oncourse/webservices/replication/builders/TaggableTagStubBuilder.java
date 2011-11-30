package ish.oncourse.webservices.replication.builders;

import ish.oncourse.model.Tag;
import ish.oncourse.model.Taggable;
import ish.oncourse.model.TaggableTag;
import ish.oncourse.webservices.v4.stubs.replication.TagRelationStub;

public class TaggableTagStubBuilder extends AbstractWillowStubBuilder<TaggableTag, TagRelationStub> {
	
	@Override
	protected TagRelationStub createFullStub(TaggableTag entity) {
		TagRelationStub stub = new TagRelationStub();
		
		Tag tag = entity.getTag();
		stub.setTagId(tag.getId());
		
		Taggable taggable = entity.getTaggable();
		stub.setEntityAngelId(taggable.getEntityAngelId());
		stub.setEntityWillowId(taggable.getEntityWillowId());
		stub.setEntityName(taggable.getEntityIdentifier());
		stub.setCreated(entity.getCreated());
		stub.setModified(entity.getModified());

		return stub;
	}
}
