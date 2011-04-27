package ish.oncourse.webservices.replication.updaters;

import ish.oncourse.model.Tag;
import ish.oncourse.webservices.v4.stubs.replication.TagStub;

public class TagUpdater extends AbstractWillowUpdater<TagStub, Tag>{

	@Override
	protected void updateEntity(TagStub stub, Tag entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setDetail(stub.getDetail());
		entity.setDetailTextile(stub.getDetailTextile());
		entity.setIsTagGroup(stub.isTagGroup());
		entity.setIsWebVisible(stub.isWebVisible());
		entity.setModified(stub.getModified());
		entity.setName(stub.getName());
		entity.setNodeType(stub.getNodeType());
		entity.setParent(callback.updateRelationShip(stub.getParentId(), Tag.class));
		entity.setShortName(stub.getShortName());
		entity.setWeighting(stub.getWeighting());
	}
}
