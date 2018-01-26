/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.site;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.*;

public class ScriptExecutor {

    private List<String> command;
    private static final Logger logger = LogManager.getLogger();
    private static final int EDIT_FILE_SCRIPT_WAIT_TIMEOUT = 15;
    
    public ScriptExecutor(List<String> command) {
        this.command = command;
    }

    public void execute() {
        ExecutorService executorService = Executors.newCachedThreadPool();
        String stringCommand = StringUtils.join(command.toArray(), " ");

        logger.warn("Starting script '{}'", stringCommand);

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        InputStream out = null;
        long time = System.currentTimeMillis();
        try {
            final Process process = processBuilder.start();
            out = process.getInputStream();
            Future<Integer> scriptCallFuture = executorService.submit((Callable<Integer>) process::waitFor);
            scriptCallFuture.get(EDIT_FILE_SCRIPT_WAIT_TIMEOUT, TimeUnit.SECONDS);
            
            time = Math.round((System.currentTimeMillis() - time) / 1000.0d);
            logger.warn("Script '{}' is finished. Time: '{}' sec", stringCommand, time);
            
            logger.warn("Script output: '{}'", IOUtils.toString(out, Charset.defaultCharset()));
            out.close();
        } catch (IOException ignore) {
            
        } catch (Exception e)  {
            logger.error("Error executing script '{}'", stringCommand, e);
        } finally {
            executorService.shutdown();
        }
        
    }
    
}
