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

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Use this for static void method.
 *
 * accept ish.oncourse.server.cayenne.IntegrationConfiguration and List<IntegrationProperty>
 * Is called on saving integration settings through UI
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@interface OnSave {

}
