package ish.oncourse.webservices.replication.v24.updaters;

import ish.common.types.NodeSpecialType;
import ish.common.types.TypesUtil;
import ish.oncourse.model.Tag;
import ish.oncourse.services.IRichtextConverter;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v24.stubs.replication.TagStub;
import org.apache.commons.lang.StringUtils;

public class TagUpdater extends AbstractWillowUpdater<TagStub, Tag>{
	
	private IRichtextConverter textileConverter;

	public TagUpdater(final IRichtextConverter textileConverter) {
		this.textileConverter = textileConverter;
	}

	@Override
	protected void updateEntity(final TagStub stub, final Tag entity, final RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setDetail(StringUtils.trimToNull(stub.getDetailTextile()) != null ? textileConverter.convertCoreText(stub.getDetailTextile()) :
			StringUtils.EMPTY);
		entity.setDetailTextile(stub.getDetailTextile());
		entity.setIsTagGroup(Boolean.TRUE.equals(stub.isTagGroup()));
		entity.setIsWebVisible(Boolean.TRUE.equals(stub.isWebVisible()));
		entity.setModified(stub.getModified());
		entity.setName(stub.getName());
		entity.setNodeType(stub.getNodeType());
		if (stub.getSpecialType() != null) {
			entity.setSpecialType(TypesUtil.getEnumForDatabaseValue(stub.getSpecialType(), NodeSpecialType.class));
		}
		if (stub.getParentId() != null) {
			entity.setParent(callback.updateRelationShip(stub.getParentId(), Tag.class));
		}
		entity.setShortName(stub.getShortName());
		entity.setWeighting(stub.getWeighting());
		entity.setColour(stub.getColour());
	}
}
