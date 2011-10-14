package ish.oncourse.webservices.replication.updaters;

import ish.oncourse.model.Tag;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.webservices.v4.stubs.replication.TagStub;

public class TagUpdater extends AbstractWillowUpdater<TagStub, Tag>{
	
	private ITextileConverter textileConverter;

	public TagUpdater(ITextileConverter textileConverter) {
		this.textileConverter = textileConverter;
	}

	@Override
	protected void updateEntity(TagStub stub, Tag entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		if(stub.getDetailTextile()!=null){
			entity.setDetail(textileConverter.convertCoreTextile(stub.getDetailTextile()));
		}
		entity.setDetailTextile(stub.getDetailTextile());
		entity.setIsTagGroup(stub.isTagGroup());
		entity.setIsWebVisible(stub.isWebVisible());
		entity.setModified(stub.getModified());
		entity.setName(stub.getName());
		entity.setNodeType(stub.getNodeType());
		
		if (stub.getParentId() != null) {
			entity.setParent(callback.updateRelationShip(stub.getParentId(), Tag.class));
		}
		
		entity.setShortName(stub.getShortName());
		entity.setWeighting(stub.getWeighting());
	}
}
