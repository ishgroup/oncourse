package ish.oncourse.services.filestorage;

import ish.oncourse.model.BinaryInfo;
import org.apache.log4j.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import java.io.File;

/**
 * The temporary class is wrapper for FileStorageAssetService to exclude all exceptions.
 *
 * @deprecated  TODO TempFileStorageAssetService should be replaced on FileStorageAssetService after we will stop saving BinaryData to the database.
 */
public class TempFileStorageAssetService implements IFileStorageAssetService {

    private static final Logger LOGGER = Logger.getLogger(TempFileStorageAssetService.class);

    private FileStorageAssetService fileStorageAssetService;

    public TempFileStorageAssetService() {
        try {
            fileStorageAssetService = new FileStorageAssetService(lookup());
        } catch (Throwable e) {
            LOGGER.error(e);
        }
    }

    @Override
    public void put(byte[] data, BinaryInfo binaryInfo) {
        try {
            fileStorageAssetService.put(data, binaryInfo);
        } catch (Throwable e) {
            LOGGER.error(e);
        }
    }

    @Override
    public byte[] get(BinaryInfo binaryInfo) {
        try {
            return fileStorageAssetService.get(binaryInfo);
        } catch (Throwable e) {
            LOGGER.error(e);
        }
        return null;
    }

    @Override
    public void delete(BinaryInfo binaryInfo) {
        try {
            fileStorageAssetService.delete(binaryInfo);
        } catch (Throwable e) {
            LOGGER.error(e);
        }
    }

    private FileStorageService lookup() throws Exception {

        String fileStorageRoot = null;
        try {
            fileStorageRoot = (String)lookup(FileStorageService.ENV_NAME_fileStorageRoot);
        } catch (Exception e) {
            LOGGER.error(e);
        }
        String syncScriptPath = null;
        try {
            syncScriptPath = (String)lookup(FileStorageReplicationService.ENV_NAME_syncScript);
        } catch (Exception e) {
            LOGGER.error(e);
        }

        FileStorageReplicationService replicationService = new FileStorageReplicationService();
        replicationService.setSyncScriptPath(syncScriptPath);

        FileStorageService fileStorageService = new FileStorageService();
        fileStorageService.setRootDir(fileStorageRoot != null ? new File(fileStorageRoot): null);
        fileStorageService.setReplicationService(replicationService);

        return fileStorageService;
    }

    private Object lookup(String path) throws Exception
    {
        Context ctx = (Context)new InitialContext().lookup("java:comp/env");
        return ctx.lookup(path);
    }

    FileStorageAssetService getFileStorageAssetService()
    {
        return fileStorageAssetService;
    }
}
