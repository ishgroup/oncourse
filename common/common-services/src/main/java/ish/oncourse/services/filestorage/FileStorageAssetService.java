package ish.oncourse.services.filestorage;

import ish.oncourse.model.BinaryInfo;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.File;
import java.util.List;

public class FileStorageAssetService implements IFileStorageAssetService{

    private static final Logger LOGGER = Logger.getLogger(FileStorageAssetService.class);

    private FileStorageService fileStorageService;

    public FileStorageAssetService() {
        init();
    }

    /**
     * The method puts the binary data to the file storage
     * @param data - binary data, not null
     * @param binaryInfo - binary info, not null
     */
    public void put(byte[] data, BinaryInfo binaryInfo) {
        LOGGER.debug(String.format("Start FileStorageAssetService.put with parameters: data.length: %s, binaryInfo: %s",
                data != null ?data.length:0, binaryInfo));
        String relatedPath = getPathBy(data, binaryInfo);
        /**
         * delete old file if new path is not the same as old
         */
        if (binaryInfo.getFilePath() != null && !relatedPath.equals(binaryInfo.getFilePath()))
        {
            delete(binaryInfo);
        }
        if (!getFileStorageService().contains(relatedPath)) {
            getFileStorageService().put(data, relatedPath);
        }
        binaryInfo.setFilePath(relatedPath);
        LOGGER.debug(String.format("Finish FileStorageAssetService.put with parameters: data.length: %s, binaryInfo: %s",
                data != null ?data.length:0, binaryInfo));
    }

    /**
     * The method gets binary data by the BinaryInfo, if there is not data for the BinaryInfo, IllegalArgumentException will be throw.
     * @param binaryInfo - BinaryInfo not null and property filePath should be not null
     * @return - binary data for the BinaryInfo.
     */
    public byte[] get(BinaryInfo binaryInfo) {
        return getFileStorageService().get(binaryInfo.getFilePath());
    }

    /**
     * The method deletes binary data for this BinaryInfo only when this BinaryData is not used anymore.
     * @param binaryInfo
     */
    public void delete(BinaryInfo binaryInfo) {

        Expression exp = ExpressionFactory.matchDbExp(BinaryInfo.FILE_PATH_PROPERTY, binaryInfo.getFilePath());
        SelectQuery selectQuery = new SelectQuery(BinaryInfo.class, exp);
        selectQuery.setPageSize(1);

        @SuppressWarnings("unchecked")
		List<BinaryInfo> list = binaryInfo.getObjectContext().performQuery(selectQuery);
        if (list.size() == 1) {
            getFileStorageService().delete(binaryInfo.getFilePath());
        }
    }

    /**
     * The method builds path string from this data (md5) and BinaryInfo (collegeId)
     * @param data - binary data for the BinaryInfo
     * @param binaryInfo
     * @return
     */
    String getPathBy(byte[] data, BinaryInfo binaryInfo) {
        String md5 = DigestUtils.md5Hex(data);
        return String.format("%d/%s", binaryInfo.getCollege().getId(), md5);
    }

    public FileStorageService getFileStorageService() {
        return fileStorageService;
    }

    public boolean contains(BinaryInfo binaryInfo)
    {
        return getFileStorageService().contains(binaryInfo.getFilePath());
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
            LOGGER.error(e);
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
            LOGGER.warn(e);
        }
        return ctx.lookup(path);
    }
}

