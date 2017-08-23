/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.usi.tapestry;

import ish.oncourse.webservices.usi.TestUSIServiceEndpoint;
import ish.oncourse.webservices.usi.USIService;
import org.apache.tapestry5.ioc.ServiceBuilder;
import org.apache.tapestry5.ioc.ServiceResources;

/**
 * User: akoiro
 * Date: 23/8/17
 */
public class USIServiceBuilder implements ServiceBuilder<USIService> {
	@Override
	public USIService buildService(ServiceResources resources) {
		if (TestUSIServiceEndpoint.useTestUSIEndpoint()) {
			return USIService.valueOf(new TestUSIServiceEndpoint());
		} else {
			au.gov.usi._2015.ws.servicepolicy.USIService service = new au.gov.usi._2015.ws.servicepolicy.USIService();
			return USIService.valueOf(service.getWS2007FederationHttpBindingIUSIService());
		}

	}
}
