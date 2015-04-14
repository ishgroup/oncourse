package ish.oncourse.services.filestorage;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

@Deprecated
public class FileStorageService {

    private static final Logger logger = LogManager.getLogger();

    public final static String ENV_NAME_fileStorageRoot = "asset.fileStorageRoot";

    private File rootDir;

    private FileStorageReplicationService replicationService;


    /**
     * @param relatedPath cannot be null
     */
    public boolean contains(String relatedPath) {
        File targetFile = new File(getRootDir(),relatedPath);
        return targetFile.exists();
    }

    /**
     *
     * @param data cannot be null
     * @param relatedPath cannot be null
     */
    public void put(byte[] data, String relatedPath)
    {
        File targetFile = new File(getRootDir(),relatedPath);
        logger.debug("Start FileStorageService.put with parameters data.length: {}, relatedPath: {}.", data != null ? data.length : 0, targetFile.getAbsolutePath());
        if (!targetFile.getParentFile().exists()) {
            boolean result = targetFile.getParentFile().mkdirs();
            if (!result)
                throw new IllegalArgumentException(String.format("Cannot create dir %s", targetFile.getParentFile().getAbsolutePath()));
        }
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(targetFile);
            IOUtils.write(data, outputStream);
            getReplicationService().replicate(targetFile);
        } catch (IOException e) {
            throw new IllegalArgumentException(String.format("Cannot put the data to target path %s.",targetFile.getAbsolutePath()),e);
        } finally {
            if (outputStream != null)
            IOUtils.closeQuietly(outputStream);
            logger.debug("Finish FileStorageService.put with parameters data.length: {}, relatedPath: {}.", data != null ? data.length : 0, targetFile.getAbsolutePath());
        }
    }


    /**
     * @param relatedPath cannot be null
     */
    public byte[] get(String relatedPath)
    {
        File sourceFile = new File(getRootDir(),relatedPath);
        FileInputStream inputStream;
        try {
            inputStream = new FileInputStream(sourceFile);
            return IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            throw new IllegalArgumentException(String.format("Cannot get the data from source path %s",sourceFile.getAbsolutePath()),e);
        }
    }

    /**
     * @param relatedPath cannot be null
     */
    public void delete(String relatedPath)
    {
        Exception exception = null;
        boolean result = false;
        File sourceFile = new File(getRootDir(),relatedPath);
        try {
            result = sourceFile.delete();
            getReplicationService().replicate(sourceFile);
        } catch (Exception e) {
            exception = e;
        }
        if (!result)
        {
            logger.warn("File {} cannot be deleted", sourceFile.getAbsolutePath(), exception);
        }
    }

    public void setRootDir(File rootDir) {
        this.rootDir = rootDir;
    }

    public File getRootDir()
    {
        return rootDir;
    }

    public void setReplicationService(FileStorageReplicationService replicationService) {
        this.replicationService = replicationService;
    }

    public FileStorageReplicationService getReplicationService() {
        return replicationService;
    }
}
