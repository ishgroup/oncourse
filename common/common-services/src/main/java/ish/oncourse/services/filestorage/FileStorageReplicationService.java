package ish.oncourse.services.filestorage;

import org.apache.log4j.Logger;

import java.io.File;

public class FileStorageReplicationService {
    private static final Logger LOGGER = Logger.getLogger(FileStorageReplicationService.class);
    public final static String ENV_NAME_syncScript = "asset.syncScript";
    private String syncScriptPath;


    public void replicate(File file) {

		if (syncScriptPath == null)
		{
			LOGGER.error("syncScriptPath is not defined");
			return;
		}

		LOGGER.debug(String.format("Start FileStorageReplicationService.replicate with parameters: syncScriptPath=%s, file=%s", syncScriptPath, file.getAbsolutePath()));
        ProcessBuilder processBuilder = new ProcessBuilder(syncScriptPath, file.getAbsolutePath());
        try {
            processBuilder.start();
        } catch (Throwable e) {
            LOGGER.error(String.format("Cannot execute script %s with parameter %s", syncScriptPath, file.getAbsolutePath()),e);
        }
        LOGGER.debug(String.format("Finish FileStorageReplicationService.replicate with parameters: syncScriptPath=%s, file=%s", syncScriptPath, file.getAbsolutePath()));
    }

    public String getSyncScriptPath() {
        return syncScriptPath;
    }

    public void setSyncScriptPath(String syncScriptPath) {
        this.syncScriptPath = syncScriptPath;
    }

}
