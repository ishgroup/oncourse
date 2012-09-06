package ish.oncourse.webservices.replication.services;

import ish.oncourse.model.BinaryInfo;
import ish.oncourse.model.Queueable;
import ish.oncourse.services.filestorage.IFileStorageAssetService;
import ish.oncourse.webservices.replication.v4.updaters.RelationShipCallback;
import ish.oncourse.webservices.util.GenericBinaryDataStub;
import org.apache.log4j.Logger;

public class AttachmentProcessor {

    private static Logger logger = Logger.getLogger(AttachmentProcessor.class);

    private IFileStorageAssetService fileStorageAssetService;

    public AttachmentProcessor(IFileStorageAssetService fileStorageAssetService) {
        this.fileStorageAssetService = fileStorageAssetService;
    }

    public Long getBinaryInfoId(GenericBinaryDataStub currentStub)
    {
        return currentStub.getBinaryInfoId();
    }

    public Queueable processBinaryDataStub(GenericBinaryDataStub currentStub, RelationShipCallback callback) {

        logger.info(String.format("AttachmentProcessor.processBinaryDataStub with parameters: stub = %s", currentStub));

        BinaryInfo binaryInfo = callback.updateRelationShip(getBinaryInfoId(currentStub), BinaryInfo.class);
        logger.info(String.format("AttachmentProcessor.processBinaryDataStub fileStorageAssetService.put for binaryDataStub %s and binaryInfo %s", currentStub, binaryInfo));
        fileStorageAssetService.put(currentStub.getContent(), binaryInfo);
        return null;
    }

    public Queueable deletedBinaryDataBy(BinaryInfo binaryInfo)
    {
        logger.info(String.format("AttachmentProcessor.deletedBinaryDataBy with parameters: binaryInfo=%s",binaryInfo));
        fileStorageAssetService.delete(binaryInfo);
        return null;
    }
}
