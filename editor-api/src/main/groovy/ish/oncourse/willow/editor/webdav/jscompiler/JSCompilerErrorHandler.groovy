package ish.oncourse.willow.editor.webdav.jscompiler

import org.apache.commons.lang3.exception.ExceptionUtils
import org.apache.logging.log4j.Logger

class JSCompilerErrorHandler {

    private List<String> errors = []
    
    void logError(Logger logger, Exception e) {
        errors << "Unexpected exception: ${ExceptionUtils.getStackTrace(e)}".toString()
        logger.error(e.message, e)
    }

    void logError(Logger logger, String message) {
        errors << message
        logger.error(message)
    }

    List<String> getErrors() {
        return errors
    }

}
