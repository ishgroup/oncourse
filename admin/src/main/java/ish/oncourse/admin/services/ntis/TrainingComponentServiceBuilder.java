package ish.oncourse.admin.services.ntis;

import au.gov.training.services.trainingcomponent.ITrainingComponentService;
import au.gov.training.services.trainingcomponent.TrainingComponentService;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.cxf.ws.security.SecurityConstants;
import org.apache.tapestry5.ioc.ServiceBuilder;
import org.apache.tapestry5.ioc.ServiceResources;

import javax.xml.ws.BindingProvider;
import java.util.Map;

public class TrainingComponentServiceBuilder implements ServiceBuilder<ITrainingComponentService> {

	private static final long NTIS_TIMEOUT = 1000l * 60 * 5;
	private static final String NTIS_SERVICE_URL_PARAM = "ntis.url";

	private ITrainingComponentService port;

	@Override
	public ITrainingComponentService buildService(ServiceResources resources) {

		if (port == null) {

			TrainingComponentService ss = new TrainingComponentService(TrainingComponentService.class.getClassLoader().getResource("wsdl/production/TrainingComponentService.wsdl"));
			port = ss.getTrainingComponentServiceWsHttpEndpoint();
			
			Map<String, Object> ctx = ((BindingProvider) port).getRequestContext();
			
			ctx.put(SecurityConstants.USERNAME, "ws.ish");
			ctx.put(SecurityConstants.PASSWORD, "LyejRogul");
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

			String ntisURL = System.getProperty(NTIS_SERVICE_URL_PARAM);

			if (ntisURL != null) {
				BindingProvider provider = (BindingProvider) port;
				provider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, ntisURL);
			}
		}

		return port;
	}
}
