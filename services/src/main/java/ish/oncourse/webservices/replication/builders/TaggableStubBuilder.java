package ish.oncourse.webservices.replication.builders;

import java.util.List;

import ish.oncourse.model.Tag;
import ish.oncourse.model.Taggable;
import ish.oncourse.model.TaggableTag;
import ish.oncourse.webservices.v4.stubs.replication.TagRelationStub;

public class TaggableStubBuilder extends AbstractWillowStubBuilder<Taggable, TagRelationStub> {
	
	@Override
	protected TagRelationStub createFullStub(Taggable taggable) {
		TagRelationStub stub = new TagRelationStub();
		
		stub.setEntityAngelId(taggable.getEntityAngelId());
		stub.setEntityWillowId(taggable.getEntityWillowId());
		stub.setEntityName(taggable.getEntityIdentifier());
		stub.setCreated(taggable.getCreated());
		stub.setModified(taggable.getModified());
		
		List<TaggableTag> list = taggable.getTaggableTags();
		
		if (!list.isEmpty()) {
			TaggableTag tg = list.get(0);
			Tag tag = tg.getTag();
			stub.setTagId(tag.getId());
		}

		return stub;
	}
}
