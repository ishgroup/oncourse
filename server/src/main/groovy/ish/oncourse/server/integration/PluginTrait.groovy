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

import io.bootique.di.Injector
import ish.oncourse.aql.AqlService
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.cayenne.IntegrationConfiguration
import ish.oncourse.server.cayenne.SystemUser
import ish.oncourse.server.license.LicenseService
import ish.oncourse.server.scripting.api.EmailService
import ish.oncourse.server.scripting.api.TemplateService
import ish.oncourse.server.services.ISystemUserService
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect

/**
 * A class implementing this trait will be loaded as a plugin. Typically it will be constructed in one of two ways:
 * integrationClass.newInstance(name: nameOfIntegration,
 *                     cayenneService: cayenneService,
 *                     emailService: emailService,
 *                     systemUserService: systemUserService,
 *                     preferenceController: preferenceController,
 *                     templateService: templateService,
 *                     antlrAqlService: antlrAqlService)
 *
 * or
 *
 * integrationClass.newInstance(configuration: integrationConfiguration,
 *                     cayenneService: cayenneService,
 *                     emailService: emailService,
 *                     systemUserService: systemUserService,
 *                     preferenceController: preferenceController,
 *                     templateService: templateService,
 *                     antlrAqlService: antlrAqlService)
 */
trait PluginTrait {

	ICayenneService cayenneService
	EmailService emailService

	//cannot be used for get current user in on_demand triggers. Use loggedInUser instead
	ISystemUserService systemUserService
	PreferenceController preferenceController
	TemplateService templateService
	LicenseService licenseService
	Injector injector
	IntegrationConfiguration configuration
	ObjectContext context
	AqlService antlrAqlService

	SystemUser loggedInUser

	/**
	 * Name of the integration configuration for this plugin
	 */
	String name

	/**
	 * id of the integration type
	 */
	int type

	void loadConfig(Map args) {
		loggedInUser = args.get("loggedInUser") as SystemUser
		configuration = args.get("configuration") as IntegrationConfiguration
		name = args.get("name") as String
		
		injector = args.get("injector") as Injector
		
		if (injector) {
			cayenneService = injector.getInstance(ICayenneService)
			emailService = injector.getInstance(EmailService)
			systemUserService = injector.getInstance(ISystemUserService)
			templateService = injector.getInstance(TemplateService)
			preferenceController = injector.getInstance(PreferenceController)
			licenseService = injector.getInstance(LicenseService)
			antlrAqlService = injector.getInstance(AqlService)
		}

				// check to see whether this plugin with initialised with a configuration
		if (configuration) {
			context = configuration.context
		} else if (name) {
			type = this.class.getAnnotation(Plugin).type()

			context = cayenneService.newContext
			configuration = ObjectSelect.query(IntegrationConfiguration)
					.where(IntegrationConfiguration.NAME.eq(name))
					.and(IntegrationConfiguration.TYPE.eq(type))
					.selectOne(context)
		}
	}
}
