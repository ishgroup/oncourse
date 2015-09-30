/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.reference;

import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.LinkedList;

public class V5ReferenceService extends ReferenceService {
	/**
	 * Initialize all services for providing way to enumerate through.
	 *
	 * @return
	 */
	@Inject
	public V5ReferenceService(ICountryService countryService, ILanguageService languageService, IQualificationService qualificationService,
							  IModuleService moduleService, ITrainingPackageService trainingPackageService) {
		allServices = new LinkedList<>();
		allServices.add(countryService);
		allServices.add(languageService);
		allServices.add(qualificationService);
		allServices.add(moduleService);
		allServices.add(trainingPackageService);
	}
}
