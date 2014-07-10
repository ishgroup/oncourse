package ish.oncourse.webservices.replication.v6.updaters;

import ish.common.types.AttachmentInfoVisibility;
import ish.common.types.TypesUtil;
import ish.oncourse.model.BinaryInfo;
import ish.oncourse.model.Document;
import ish.oncourse.model.DocumentVersion;
import ish.oncourse.webservices.replication.v4.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.v4.updaters.RelationShipCallback;
import ish.oncourse.webservices.v6.stubs.replication.BinaryInfoStub;
import org.apache.cayenne.ObjectContext;

public class BinaryInfoUpdater extends AbstractWillowUpdater<BinaryInfoStub, BinaryInfo> {

	@Override
	protected void updateEntity(BinaryInfoStub stub, BinaryInfo entity, RelationShipCallback callback) {
		entity.setByteSize(stub.getByteSize());
		entity.setCreated(stub.getCreated());
		entity.setWebVisible(TypesUtil.getEnumForDatabaseValue(stub.getWebVisible(), AttachmentInfoVisibility.class));
		entity.setMimeType(stub.getMimeType());
		entity.setModified(stub.getModified());
		entity.setName(stub.getName());
		entity.setPixelHeight(stub.getPixelHeight());
		entity.setPixelWidth(stub.getPixelWidth());
		entity.setFileUUID(stub.getFileUUID());

		// next create Document and DocumentVersion records pair corresponding to received BinaryInfo record
		ObjectContext context = entity.getObjectContext();
		
		Document document = context.newObject(Document.class);
		
		document.setCreated(entity.getCreated());
		document.setModified(entity.getModified());
		document.setCollege(entity.getCollege());
		document.setFileUUID(entity.getFileUUID());
		document.setWebVisibility(entity.getWebVisible());
		document.setIsRemoved(false);
		document.setIsShared(true);
		document.setName(entity.getName());
		document.setAngelId(entity.getAngelId());

		DocumentVersion documentVersion = context.newObject(DocumentVersion.class);
		
		documentVersion.setCreated(entity.getCreated());
		documentVersion.setModified(entity.getModified());
		documentVersion.setCollege(entity.getCollege());
		documentVersion.setByteSize(entity.getByteSize());
		documentVersion.setPixelHeight(entity.getPixelHeight());
		documentVersion.setPixelWidth(entity.getPixelWidth());
		documentVersion.setMimeType(entity.getMimeType());
		documentVersion.setTimestamp(entity.getCreated());
		documentVersion.setAngelId(entity.getAngelId());
		
		documentVersion.setDocument(document);
	}
}
