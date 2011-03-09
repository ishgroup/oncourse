package ish.oncourse.webservices.soap.v4;

import ish.oncourse.webservices.soap.v4.auth.AuthenticationPortType;
import ish.oncourse.webservices.soap.v4.auth.AuthenticationService;
import ish.oncourse.webservices.util.SoapUtil;

import java.net.URL;

import javax.xml.ws.BindingProvider;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.binding.soap.interceptor.SoapPreProtocolOutInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;

public abstract class AbstractReplicationTest extends AbstractWebServiceTest {
	
	protected static ReplicationPortType getReplicationPort() {
		URL url = ReferencePortType.class.getClassLoader().getResource("wsdl/v4_replication.wsdl");
		ReplicationService service = new ReplicationService(url);

		ReplicationPortType port = service.getReplicationPort();

		BindingProvider provider = (BindingProvider) port;
		provider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				String.format("http://localhost:%s/services/v4/replication", PORT));

		return port;
	}
	
	protected static AuthenticationPortType getAuthPort() {

		AuthenticationService authService = new AuthenticationService(AuthenticationPortType.class.getClassLoader().getResource(
				"wsdl/v4_auth.wsdl"));

		AuthenticationPortType authPort = authService.getAuthenticationPort();

		BindingProvider provider = (BindingProvider) authPort;
		provider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				String.format("http://localhost:%s/services/v4/auth", PORT));

		return authPort;
	}
	
	protected static class AddSecurityCodeInterceptor extends AbstractSoapInterceptor {
		private String securityCode;
		private Long communicationKey;

		protected AddSecurityCodeInterceptor(String securityCode, Long communicationKey) {
			super(Phase.WRITE);
			addAfter(SoapPreProtocolOutInterceptor.class.getName());
			this.securityCode = securityCode;
			this.communicationKey = communicationKey;
		}

		public void handleMessage(SoapMessage message) throws Fault {
			SoapUtil.addSecurityCode(message, securityCode);
			SoapUtil.addCommunicationKey(message, communicationKey);
		}
	}
}
