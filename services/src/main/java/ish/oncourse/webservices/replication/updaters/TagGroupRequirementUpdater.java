package ish.oncourse.webservices.replication.updaters;

import ish.oncourse.model.Tag;
import ish.oncourse.model.TagGroupRequirement;
import ish.oncourse.webservices.v4.stubs.replication.TagRequirementStub;

public class TagGroupRequirementUpdater extends AbstractWillowUpdater<TagRequirementStub, TagGroupRequirement> {

	@Override
	protected void updateEntity(TagRequirementStub stub, TagGroupRequirement entity, RelationShipCallback callback) {
		entity.setAllowsMultipleTags(stub.isManyTermsAllowed());
		entity.setCreated(stub.getCreated());
		entity.setIsRequired(stub.isRequired());
		entity.setModified(stub.getModified());
		entity.setTag(callback.updateRelationShip(stub.getTagId(), Tag.class));
	}
}
