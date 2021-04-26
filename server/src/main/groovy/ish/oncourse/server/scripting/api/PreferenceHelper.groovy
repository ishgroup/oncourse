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

package ish.oncourse.server.scripting.api

import groovy.transform.CompileDynamic
import ish.oncourse.server.IPreferenceController
/**
 * Preference API helper allowing to express preference query in groovy as a chain of method calls.
 *
 * E.g.:
 *   pref.web.url
 *   pref.college.name
 *   pref.college.abn
 */

@CompileDynamic
class PreferenceHelper {

	IPreferenceController preferenceController

	Closure<Object> handleProperty = { String property ->
		def prefKey = delegate.getMetaKey() ? "${delegate.getMetaKey()}.${property}" : property

		def result = preferenceController.getPreference(prefKey, false)

		if (result) {
			result = result.valueString
		} else {
			result = NullProxy.getInstance()
		}

		result.metaClass.getMetaKey = { -> prefKey }
		result.metaClass.getProperty = handleProperty

		return result
	}

	PreferenceHelper(IPreferenceController preferenceController) {
		this.preferenceController = preferenceController
	}

	def pref() {
		def result = NullProxy.getInstance()
		result.metaClass.getMetaKey = { -> null }
		result.metaClass.getProperty = handleProperty

		return result
	}
}
