/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.integration

import ish.common.types.IntegrationType

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * Annotate your plugin with this annotation 
 * Plugin must implement PluginTrait
 */

@Retention(RetentionPolicy.RUNTIME)
@interface Plugin {
    /**
    * The type of the integration, stored in the DB. Each intgration must have a unique type
    */
    IntegrationType type()

    /**
    * If this is set to true, then only one integration of this type can be created
    */
    boolean oneOnly() default false
}
