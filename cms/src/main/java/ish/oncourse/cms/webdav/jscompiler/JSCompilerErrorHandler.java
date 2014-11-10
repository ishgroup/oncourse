/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.cms.webdav.jscompiler;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class JSCompilerErrorHandler {

    private List<String> errors = new ArrayList<>();


    public void logError(Logger logger, Exception e) {

        errors.add(String.format("Unexpected exception: %s", ExceptionUtils.getStackTrace(e)));
        logger.error(e.getMessage(), e);
    }

    public void logError(Logger logger, String message) {
        errors.add(message);
        logger.error(message);
    }

    public List<String> getErrors() {
        return errors;
    }

}
