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

package ish.oncourse.server.cayenne

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import ish.oncourse.server.cayenne.glue._IntegrationConfiguration

class IntegrationConfiguration extends _IntegrationConfiguration {


	IntegrationProperty getIntegrationProperty(String keyCode) {
		List<IntegrationProperty> props = getIntegrationProperties()
		if (props != null) {
			return IntegrationProperty.KEY_CODE.eq(keyCode).first(props)
		}
		return null
	}

	@CompileStatic(TypeCheckingMode.SKIP)
	Object getProperty(String keyCode) {
		getIntegrationProperty(keyCode)?: metaClass.getProperty(this, keyCode)
	}

	void setProperty(String keyCode, String value) {
		IntegrationProperty prop = getIntegrationProperty(keyCode)
		if (!prop) {
			prop = context.newObject(IntegrationProperty)
			prop.integrationConfiguration = this
			prop.keyCode = keyCode
		}
		
		prop.setValue(value)
	}
}



