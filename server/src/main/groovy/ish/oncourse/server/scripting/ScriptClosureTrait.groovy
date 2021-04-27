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

package ish.oncourse.server.scripting

import ish.oncourse.server.integration.PluginTrait

trait ScriptClosureTrait<T> {

    /**
     * The closure can be optionally linked to a specific Integration configuration
     */
    String name
    def name(String name) {
        this.name = name
    }
    
    /**
     * Execute the closure with the configuration from the passed integration
     *
     * @param integration
     */
    abstract Object execute(T integration)
}
