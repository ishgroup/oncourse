package ish.oncourse.webservices.replication.services;

import ish.oncourse.model.BinaryInfo;
import ish.oncourse.model.Document;
import ish.oncourse.model.DocumentVersion;
import ish.oncourse.model.Queueable;
import ish.oncourse.services.filestorage.IFileStorageAssetService;
import ish.oncourse.services.site.IWebSiteService;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AttachmentProcessor {

    private static Logger logger = LogManager.getLogger();

    private IFileStorageAssetService fileStorageAssetService;
	private IWebSiteService webSiteService;

    public AttachmentProcessor(IFileStorageAssetService fileStorageAssetService, IWebSiteService webSiteService) {
        this.fileStorageAssetService = fileStorageAssetService;
		this.webSiteService = webSiteService;
    }

    public Queueable deletedBinaryDataBy(BinaryInfo binaryInfo)
    {
        logger.info("AttachmentProcessor.deletedBinaryDataBy with parameters: binaryInfo {}",binaryInfo);
		
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
		return ObjectSelect.query(DocumentVersion.class)
                .where(DocumentVersion.ANGEL_ID.eq(binaryInfo.getAngelId()))
                .and(DocumentVersion.COLLEGE.eq(webSiteService.getCurrentCollege()))
                .selectOne(binaryInfo.getObjectContext());
	}
}
