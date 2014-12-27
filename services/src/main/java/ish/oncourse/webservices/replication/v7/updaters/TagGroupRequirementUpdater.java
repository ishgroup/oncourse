package ish.oncourse.webservices.replication.v7.updaters;

import ish.oncourse.model.Tag;
import ish.oncourse.model.TagGroupRequirement;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v7.stubs.replication.TagRequirementStub;

public class TagGroupRequirementUpdater extends AbstractWillowUpdater<TagRequirementStub, TagGroupRequirement> {

	@Override
	protected void updateEntity(TagRequirementStub stub, TagGroupRequirement entity, RelationShipCallback callback) {
		entity.setAllowsMultipleTags(stub.isManyTermsAllowed());
		entity.setCreated(stub.getCreated());
		entity.setIsRequired(stub.isRequired());
		entity.setModified(stub.getModified());
		entity.setEntityIdentifier(stub.getEntityName());
		entity.setTag(callback.updateRelationShip(stub.getTagId(), Tag.class));
	}
}
