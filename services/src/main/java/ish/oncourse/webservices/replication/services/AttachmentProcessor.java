package ish.oncourse.webservices.replication.services;

import ish.oncourse.model.BinaryInfo;
import ish.oncourse.model.Document;
import ish.oncourse.model.DocumentVersion;
import ish.oncourse.model.Queueable;
import ish.oncourse.services.filestorage.IFileStorageAssetService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.util.GenericBinaryDataStub;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.log4j.Logger;

public class AttachmentProcessor {

    private static Logger logger = Logger.getLogger(AttachmentProcessor.class);

    private IFileStorageAssetService fileStorageAssetService;
	private IWebSiteService webSiteService;

    public AttachmentProcessor(IFileStorageAssetService fileStorageAssetService, IWebSiteService webSiteService) {
        this.fileStorageAssetService = fileStorageAssetService;
		this.webSiteService = webSiteService;
    }

    public Long getBinaryInfoId(GenericBinaryDataStub currentStub)
    {
        return currentStub.getBinaryInfoId();
    }

    public Queueable processBinaryDataStub(GenericBinaryDataStub currentStub, RelationShipCallback callback) {

        logger.info(String.format("AttachmentProcessor.processBinaryDataStub with parameters: stub = %s", currentStub));
		
		// before trying to get uncommitted DocumentVersion record instance we need to force BinaryInfoUpdater execution
		// so that correspondent DocumentVersion record will be created by the moment we will need to get hold of it
		callback.updateRelationShip(getBinaryInfoId(currentStub), BinaryInfo.class);
		
		DocumentVersion documentVersion = callback.updateRelationShip(getBinaryInfoId(currentStub), DocumentVersion.class);
		logger.info(String.format("AttachmentProcessor.processBinaryDataStub fileStorageAssetService.put for binaryDataStub %s and binaryInfo %s", currentStub, documentVersion));
		
        fileStorageAssetService.put(currentStub.getContent(), documentVersion);
        return null;
    }

    public Queueable deletedBinaryDataBy(BinaryInfo binaryInfo)
    {
        logger.info(String.format("AttachmentProcessor.deletedBinaryDataBy with parameters: binaryInfo=%s",binaryInfo));
		
		DocumentVersion documentVersion = getDocumentVersionByBinaryInfo(binaryInfo);
		
        fileStorageAssetService.delete(documentVersion);

		
		// remove Document and DocumentVersion records for BinaryInfo
		ObjectContext context = binaryInfo.getObjectContext();

		Document document = documentVersion.getDocument();
		
		context.deleteObjects(documentVersion);
		context.deleteObjects(document);
		
        return null;
    }

	public DocumentVersion getDocumentVersionByBinaryInfo(BinaryInfo binaryInfo) {
		SelectQuery query = new SelectQuery(DocumentVersion.class,
				ExpressionFactory.matchExp(DocumentVersion.ANGEL_ID_PROPERTY, binaryInfo.getAngelId())
						.andExp(ExpressionFactory.matchExp(DocumentVersion.COLLEGE_PROPERTY, webSiteService.getCurrentCollege())));

		return (DocumentVersion) Cayenne.objectForQuery(binaryInfo.getObjectContext(), query);
	}
}
