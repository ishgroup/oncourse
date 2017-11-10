package ish.oncourse.willow.editor.webdav

import ish.oncourse.willow.editor.services.access.AuthenticationService
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

import static ish.oncourse.willow.editor.EditorProperty.EDIT_SCRIPT_PATH

class RunEditScript {
    private static final Logger logger = LogManager.logger
    private static final int EDIT_FILE_SCRIPT_WAIT_TIMEOUT = 15

    private File file
    private AuthenticationService authenticationService
    private ExecutorService executorService

    void run() {
        String scriptPath = EDIT_SCRIPT_PATH.value

        if (StringUtils.trimToNull(scriptPath) == null) {
            logger.error('Edit file script is not defined! Resources haven\'t been updated!')
            return
        }

        List<String> scriptCommand = []

        String filePath = file.absolutePath

        scriptCommand << scriptPath
        scriptCommand << '-p'
        scriptCommand << "\"$filePath\""

        String userEmail = authenticationService.userEmail

        if (userEmail) {
            scriptCommand << '-e'
            scriptCommand << userEmail
        }

        ProcessBuilder processBuilder = new ProcessBuilder(scriptCommand)

        try {
            logger.debug("Starting script '{}' for file '{}'", scriptPath, filePath)
            long time = System.currentTimeMillis()
            final Process process = processBuilder.start()

            Future<Integer> scriptCallFuture = executorService.submit({process.waitFor()} as Callable)
            scriptCallFuture.get(EDIT_FILE_SCRIPT_WAIT_TIMEOUT, TimeUnit.SECONDS)
            time = Math.round((System.currentTimeMillis() - time) / 1000.0)
            logger.debug("script '{}' for file '{}' is finished. Time: '{}' sec", scriptPath, filePath, time)
        } catch (Exception e) {
            logger.error("Error executing script '{}' for file '{}'", scriptPath, filePath, e)
        }
    }

    static RunEditScript valueOf(File file,
                                        AuthenticationService authenticationService,
                                        ExecutorService executorService) {
        RunEditScript runEditScript = new RunEditScript()
        runEditScript.file = file
        runEditScript.authenticationService = authenticationService
        runEditScript.executorService = executorService
        return runEditScript
    }

}
