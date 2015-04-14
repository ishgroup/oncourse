package ish.oncourse.services.filestorage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Deprecated
public class FileStorageReplicationService {
    private static final Logger logger = LogManager.getLogger();
    public final static String ENV_NAME_syncScript = "asset.syncScript";
    private String syncScriptPath;


    public void replicate(File file) {

		if (syncScriptPath == null) {
			logger.error("syncScriptPath is not defined");
			return;
		}

		logger.debug("Start FileStorageReplicationService.replicate with parameters syncScriptPath: {}, file: {}", syncScriptPath, file.getAbsolutePath());
        ProcessBuilder processBuilder = new ProcessBuilder(syncScriptPath, file.getAbsolutePath());
        try {
            processBuilder.start();
        } catch (Throwable e) {
            logger.error("Cannot execute script {} with parameter {}", syncScriptPath, file.getAbsolutePath(), e);
        }
	    logger.debug("Finish FileStorageReplicationService.replicate with parameters syncScriptPath: {}, file: {}", syncScriptPath, file.getAbsolutePath());
    }

    public String getSyncScriptPath() {
        return syncScriptPath;
    }

    public void setSyncScriptPath(String syncScriptPath) {
        this.syncScriptPath = syncScriptPath;
    }

}
