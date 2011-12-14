package ish.oncourse.webservices.replication.updaters;

import org.apache.commons.lang.StringUtils;

import ish.common.types.NodeSpecialType;
import ish.common.types.TypesUtil;
import ish.oncourse.model.Tag;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.webservices.v4.stubs.replication.TagStub;

public class TagUpdater extends AbstractWillowUpdater<TagStub, Tag>{
	
	private ITextileConverter textileConverter;

	public TagUpdater(final ITextileConverter textileConverter) {
		this.textileConverter = textileConverter;
	}

	@Override
	protected void updateEntity(final TagStub stub, final Tag entity, final RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		if (StringUtils.trimToNull(stub.getDetailTextile()) != null) {
			entity.setDetail(textileConverter.convertCoreTextile(stub.getDetailTextile()));
		}
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
	}
}
