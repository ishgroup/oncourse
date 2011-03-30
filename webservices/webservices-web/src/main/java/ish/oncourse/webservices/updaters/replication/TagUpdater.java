package ish.oncourse.webservices.updaters.replication;

import ish.oncourse.model.Tag;
import ish.oncourse.webservices.v4.stubs.replication.ReplicatedRecord;
import ish.oncourse.webservices.v4.stubs.replication.TagStub;

import java.util.List;

public class TagUpdater extends AbstractWillowUpdater<TagStub, Tag>{

	@SuppressWarnings("unchecked")
	@Override
	protected void updateEntity(TagStub stub, Tag entity, List<ReplicatedRecord> result) {
		entity.setAngelId(stub.getAngelId());
		entity.setCollege(college);
		entity.setCreated(stub.getCreated());
		entity.setDetail(stub.getDetail());
		entity.setDetailTextile(stub.getDetailTextile());
		entity.setIsTagGroup(stub.isTagGroup());
		entity.setIsWebVisible(stub.isWebVisible());
		entity.setModified(stub.getModified());
		entity.setName(stub.getName());
		entity.setNodeType(stub.getNodeType());
		entity.setParent((Tag) updateRelationShip(stub.getParentId(), "Tag", result));
		entity.setShortName(stub.getShortName());
		entity.setWeighting(stub.getWeighting());
	}
}
