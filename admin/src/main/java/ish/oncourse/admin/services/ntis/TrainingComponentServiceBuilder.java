package ish.oncourse.admin.services.ntis;

import java.util.Map;

import javax.xml.ws.BindingProvider;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.cxf.ws.security.SecurityConstants;
import org.apache.tapestry5.ioc.ServiceBuilder;
import org.apache.tapestry5.ioc.ServiceResources;

import au.gov.training.services.trainingcomponent.ITrainingComponentService;
import au.gov.training.services.trainingcomponent.TrainingComponentService;

public class TrainingComponentServiceBuilder implements ServiceBuilder<ITrainingComponentService> {
	
	private static final long NTIS_TIMEOUT = 1000l * 60 * 5;
	
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ITrainingComponentService buildService(ServiceResources resources) {
		
		TrainingComponentService ss = new TrainingComponentService();
		ITrainingComponentService port = ss.getTrainingComponentServiceBasicHttpEndpoint();

		Map ctx = ((BindingProvider) port).getRequestContext();
		
		ctx.put(SecurityConstants.USERNAME, "WebService.Read");
		ctx.put(SecurityConstants.PASSWORD, "Asdf098");
		ctx.put(SecurityConstants.TIMESTAMP_FUTURE_TTL, "30");
		
		Client client = ClientProxy.getClient(port);
		
		client.getInInterceptors().add(new LoggingInInterceptor());
        client.getOutInterceptors().add(new LoggingOutInterceptor());
        client.getOutFaultInterceptors().add(new LoggingOutInterceptor());
		
		HTTPConduit conduit = (HTTPConduit) client.getConduit();
		
		HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
		httpClientPolicy.setAllowChunking(false);
		httpClientPolicy.setConnectionTimeout(NTIS_TIMEOUT);
		httpClientPolicy.setReceiveTimeout(NTIS_TIMEOUT);
		
		conduit.setClient(httpClientPolicy);
		
		return port;
	}
	
}
