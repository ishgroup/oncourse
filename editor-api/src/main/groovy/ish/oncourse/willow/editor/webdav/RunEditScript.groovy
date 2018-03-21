package ish.oncourse.willow.editor.webdav

import ish.oncourse.configuration.Configuration
import ish.oncourse.willow.editor.services.access.UserService
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.nio.charset.Charset
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

import static ish.oncourse.willow.editor.EditorProperty.EDIT_SCRIPT_PATH

class RunEditScript {
    private static final Logger logger = LogManager.logger
    private static final int EDIT_FILE_SCRIPT_WAIT_TIMEOUT = 15

    private File file
    private UserService userService
    private ExecutorService executorService

    void run() {
        String scriptPath = Configuration.getValue(EDIT_SCRIPT_PATH)

        if (StringUtils.trimToNull(scriptPath) == null) {
            logger.error('Edit file script is not defined! Resources haven\'t been updated!')
            return
        }

        List<String> scriptCommand = []

        String filePath = file.absolutePath

        scriptCommand << scriptPath
        scriptCommand << '-p'
        scriptCommand << "\"$filePath\"".toString()

        String userEmail = userService.userEmail

        if (userEmail) {
            scriptCommand << '-e'
            scriptCommand << userEmail
        }

        ProcessBuilder processBuilder = new ProcessBuilder(scriptCommand)
        InputStream out
        try {
            logger.debug("Starting script '{}' for file '{}'", scriptPath, filePath)
            long time = System.currentTimeMillis()
            final Process process = processBuilder.start()
            out = process.getInputStream()

            Future<Integer> scriptCallFuture = executorService.submit({process.waitFor()} as Callable)
            scriptCallFuture.get(EDIT_FILE_SCRIPT_WAIT_TIMEOUT, TimeUnit.SECONDS)
            time = Math.round((System.currentTimeMillis() - time) / 1000.0d)
            logger.info("Edit file script '{}' for file '{}' is finished. Time: '{}' sec. Script output: '{}'", scriptPath, filePath, time, IOUtils.toString(out, Charset.defaultCharset()))
            out.close()
        } catch (Exception e) {
            logger.error("Error executing script '{}' for file '{}'", scriptPath, filePath, e)
        }
    }

    static RunEditScript valueOf(File file,
                                        UserService userService,
                                        ExecutorService executorService) {
        RunEditScript runEditScript = new RunEditScript()
        runEditScript.file = file
        runEditScript.userService = userService
        runEditScript.executorService = executorService
        return runEditScript
    }

}
