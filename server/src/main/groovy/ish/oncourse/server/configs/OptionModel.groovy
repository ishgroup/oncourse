/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.configs

import ish.oncourse.server.cayenne.AutomationBinding

class OptionModel {
    String name;
    String value;
    String dataType;

    OptionModel(AutomationBinding automationBinding){
        name = automationBinding.name
        value = automationBinding.value
        dataType = automationBinding.dataType
    }

    String getName() {
        return name
    }

    String getValue() {
        return value
    }

    String getDataType() {
        return dataType
    }
}
