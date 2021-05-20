/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish

import ish.oncourse.common.ResourcesUtil
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class TestUtil {

    private static final Logger logger = LogManager.getLogger()

    static File getResourceAsFile(final String relativePath) {
        logger.entry(relativePath)

        URL resourceURL = ClassLoader.getSystemClassLoader().getResource(relativePath)
        try {
            return new File(resourceURL.toURI())
        } catch (URISyntaxException e) {
            logger.warn("resource not found, searched in paths:\n {}", ResourcesUtil.getClasspathString())
        }
        return null
    }
}
