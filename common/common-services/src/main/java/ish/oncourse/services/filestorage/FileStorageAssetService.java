package ish.oncourse.services.filestorage;

import ish.oncourse.model.DocumentVersion;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.File;
import java.util.List;

@Deprecated
public class FileStorageAssetService implements IFileStorageAssetService {

    private static final Logger logger = LogManager.getLogger();

    private FileStorageService fileStorageService;

    public FileStorageAssetService() {
        init();
    }

    /**
     * The method puts the binary data to the file storage
     * @param data - binary data, not null
     * @param documentVersion - binary info, not null
     */
    public void put(byte[] data, DocumentVersion documentVersion) {
        logger.debug("Start FileStorageAssetService.put with parameters: data.length: {}, documentVersion: {}",
		        data != null ? data.length : 0, documentVersion);
		
        String relatedPath = getPathBy(data, documentVersion);
        /**
         * delete old file if new path is not the same as old
         */
        if (documentVersion.getFilePath() != null && !relatedPath.equals(documentVersion.getFilePath())) {
            delete(documentVersion);
        }
        if (!getFileStorageService().contains(relatedPath)) {
            getFileStorageService().put(data, relatedPath);
        }
        documentVersion.setFilePath(relatedPath);
        logger.debug("Finish FileStorageAssetService.put with parameters: data.length: {}, documentVersion: {}",
		        data != null ? data.length : 0, documentVersion);
    }

    /**
     * The method gets binary data by the BinaryInfo, if there is not data for the BinaryInfo, IllegalArgumentException will be throw.
     * @param documentVersion - BinaryInfo not null and property filePath should be not null
     * @return - binary data for the BinaryInfo.
     */
    public byte[] get(DocumentVersion documentVersion) {
        return getFileStorageService().get(documentVersion.getFilePath());
    }

    /**
     * The method deletes binary data for this BinaryInfo only when this BinaryData is not used anymore.
     * @param documentVersion
     */
    public void delete(DocumentVersion documentVersion) {

        Expression exp = ExpressionFactory.matchDbExp(DocumentVersion.FILE_PATH_PROPERTY, documentVersion.getFilePath());
        SelectQuery selectQuery = new SelectQuery(DocumentVersion.class, exp);
        selectQuery.setPageSize(1);

        @SuppressWarnings("unchecked")
		List<DocumentVersion> list = documentVersion.getObjectContext().performQuery(selectQuery);
        if (list.size() == 1) {
            getFileStorageService().delete(documentVersion.getFilePath());
        }
    }

    /**
     * The method builds path string from this data (md5) and BinaryInfo (collegeId)
     * @param data - binary data for the BinaryInfo
     * @param documentVersion
     * @return
     */
    String getPathBy(byte[] data, DocumentVersion documentVersion) {
        String md5 = DigestUtils.md5Hex(data);
        return String.format("%d/%s", documentVersion.getCollege().getId(), md5);
    }

    public FileStorageService getFileStorageService() {
        return fileStorageService;
    }

    public boolean contains(DocumentVersion documentVersion)
    {
        return getFileStorageService().contains(documentVersion.getFilePath());
    }

    public void init() {

        try {
            String fileStorageRoot = (String)lookup(FileStorageService.ENV_NAME_fileStorageRoot);
            String syncScriptPath = (String)lookup(FileStorageReplicationService.ENV_NAME_syncScript);

            FileStorageReplicationService replicationService = new FileStorageReplicationService();
            replicationService.setSyncScriptPath(syncScriptPath);

            FileStorageService fileStorageService = new FileStorageService();
            fileStorageService.setRootDir(fileStorageRoot != null ? new File(fileStorageRoot): null);
            fileStorageService.setReplicationService(replicationService);

            this.fileStorageService = fileStorageService;

        } catch (Exception e) {
            logger.error(e);
            throw new IllegalArgumentException(e);
        }
    }

    private Object lookup(String path) throws NamingException {
        InitialContext context = new InitialContext();
        Context ctx = null;
        try {
            ctx = (Context) context.lookup("java:comp/env");
        } catch (NamingException e) {
            ctx = context;
            logger.warn(e);
        }
        return ctx.lookup(path);
    }
}

