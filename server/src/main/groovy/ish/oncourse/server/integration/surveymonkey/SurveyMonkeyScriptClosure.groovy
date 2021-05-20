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

package ish.oncourse.server.integration.surveymonkey

import groovy.transform.CompileDynamic
import ish.oncourse.API
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.scripting.ScriptClosure
import ish.oncourse.server.scripting.ScriptClosureTrait
import liquibase.pro.packaged.O

/**
 * This integration allows you to push records into SurveyMonkey in order to add them to a survey. Create one integration
 * per survey you want to send and then use this scripting block:
 *
 * surveyMonkey {
 *     contact myContact
 * }
 *
 * Use the name option if you have more than one SurveyMonkey integration and you want to push to only one.
 * ```
 * surveyMonkey {
 *     name "name of integration"
 *     contact myContact
 * }
 **/
@CompileDynamic
@API
@ScriptClosure(key = "surveyMonkey", integration = SurveyMonkeyIntegration)
class SurveyMonkeyScriptClosure implements ScriptClosureTrait<SurveyMonkeyIntegration> {

    String email
    String firstName
    String lastName


	def email(String email) {
		this.email = email
	}

	def firstName(String firstName) {
		this.firstName = firstName
	}

	def lastName(String lastName) {
		this.lastName = lastName
	}

	def contact(Contact contact) {
		this.email = contact.email
		this.firstName = contact.firstName
		this.lastName = contact.lastName
	}

	@Override
	Object execute(SurveyMonkeyIntegration integration) {
		integration.sendSurvey(email, firstName, lastName)
		return null
	}

}
