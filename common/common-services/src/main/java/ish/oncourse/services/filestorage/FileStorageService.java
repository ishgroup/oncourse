package ish.oncourse.services.filestorage;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileStorageService {

    private static final Logger LOGGER = Logger.getLogger(FileStorageService.class);

    public final static String ENV_NAME_fileStorageRoot = "asset.fileStorageRoot";

    private File rootDir;

    private FileStorageReplicationService replicationService;

    public boolean contains(String relatedPath)
    {
        File targetFile = new File(getRootDir(),relatedPath);
        return targetFile.exists();
    }

    public void put(byte[] data, String relatedPath)
    {
        File targetFile = new File(getRootDir(),relatedPath);
        LOGGER.debug(String.format("Start FileStorageService.put with parameters: data.length = %s, relatedPath = %s.",data != null ? data.length: 0, targetFile.getAbsolutePath()));
        if (!targetFile.getParentFile().exists())
        {
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
            LOGGER.debug(String.format("Finish FileStorageService.put with parameters: data.length = %s, relatedPath = %s.",data != null ? data.length: 0, targetFile.getAbsolutePath()));
        }
    }


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
            LOGGER.warn(String.format("File %s cannot be deleted", sourceFile.getAbsolutePath()), exception);
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
