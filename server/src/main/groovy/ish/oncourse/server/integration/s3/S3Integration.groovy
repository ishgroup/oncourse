/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.integration.s3

import ish.oncourse.server.integration.Plugin
import ish.oncourse.server.integration.PluginTrait
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@Plugin(type=16)
class S3Integration implements PluginTrait {
    public static final String S3_ACCOUNT = "account"
    public static final String S3_KEY = "key"
    public static final String S3_BUCKET = "bucket"

    String account
    String key
    String bucket

    private static Logger logger = LogManager.logger

    S3Integration(Map args) {
        loadConfig(args)

        this.account = configuration.getIntegrationProperty(S3_ACCOUNT).value
        this.key = configuration.getIntegrationProperty(S3_KEY).value
        this.bucket = configuration.getIntegrationProperty(S3_BUCKET).value
    }
}
