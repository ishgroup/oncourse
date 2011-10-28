package ish.oncourse.admin.services.ntis;

import java.util.Map;

import javax.xml.ws.BindingProvider;

import org.apache.cxf.ws.security.SecurityConstants;
import org.apache.tapestry5.ioc.ServiceBuilder;
import org.apache.tapestry5.ioc.ServiceResources;

import au.gov.training.services.trainingcomponent.ITrainingComponentService;
import au.gov.training.services.trainingcomponent.TrainingComponentService;

public class TrainingComponentServiceBuilder implements ServiceBuilder<ITrainingComponentService> {

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ITrainingComponentService buildService(ServiceResources resources) {
		
		TrainingComponentService ss = new TrainingComponentService();
		ITrainingComponentService port = ss.getTrainingComponentServiceBasicHttpEndpoint();

		Map ctx = ((BindingProvider) port).getRequestContext();
		ctx.put(SecurityConstants.USERNAME, "WebService.Read");
		ctx.put(SecurityConstants.PASSWORD, "Asdf098");
		ctx.put(SecurityConstants.TIMESTAMP_FUTURE_TTL, "30");
		
		return port;
	}
	
}
