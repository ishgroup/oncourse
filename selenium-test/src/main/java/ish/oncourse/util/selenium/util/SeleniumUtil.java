/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.util.selenium.util;

import java.time.Duration;

/**
 * Contains methods for controlling and manipulating the process of test execution.
 */
public class SeleniumUtil {

    /**
     * In common cases used to stop test execution and wait until GUI is loaded.
     * @param duration —  how long to stop execution in any unit of time (will be converted)
     */
    public static void threadWait(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
