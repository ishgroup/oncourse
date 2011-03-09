package ish.oncourse.webservices.soap.v4;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import ish.oncourse.webservices.util.SoapUtil;
import ish.oncourse.webservices.v4.stubs.reference.ReferenceResult;

import java.io.InputStream;

import javax.xml.ws.BindingProvider;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.binding.soap.interceptor.SoapPreProtocolOutInterceptor;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.BeforeClass;
import org.junit.Test;

public class ReferencePortTypeTest extends AbstractWebServiceTest {

	private static ReferencePortType getReferencePort() {
		ReferenceService service = new ReferenceService(ReferencePortType.class.getClassLoader().getResource("wsdl/v4_reference.wsdl"));
		ReferencePortType referencePort = service.getReferencePort();
		
		BindingProvider provider = (BindingProvider) referencePort;
		provider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, String.format("http://localhost:%s/services/v4/reference", PORT));
		
		return referencePort;
	}
	
	@BeforeClass
	public static void setupDataSet() throws Exception {
		InputStream st = ReferencePortTypeTest.class.getClassLoader().getResourceAsStream("baseReferenceDataSet.xml");
		
		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);
		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(DATASOURCES.get(Database.ONCOURSE_REFERENCE).getConnection(), null),
				dataSet);
		
		st = ReferencePortTypeTest.class.getClassLoader().getResourceAsStream("baseCollegeDataSet.xml");
		dataSet = new FlatXmlDataSetBuilder().build(st);
		
		DatabaseOperation.INSERT.execute(new DatabaseConnection(DATASOURCES.get(Database.ONCOURSE).getConnection(), null),
				dataSet);
	}
	
	@Test
	public void testGetMaximumVersion() throws Exception {
		ReferencePortType referencePort = getReferencePort();
		
		Client client = ClientProxy.getClient(referencePort);
		client.getOutInterceptors().add(new AddSecurityCodeInterceptor());

		Long version = referencePort.getMaximumVersion();

		assertNotNull(version);

		assertTrue("Check that max version is 2.", version.equals(2L));
	}

	@Test
	public void testGetRecords() throws Exception {
		ReferencePortType referencePort = getReferencePort();
		
		Client client = ClientProxy.getClient(referencePort);
		client.getOutInterceptors().add(new AddSecurityCodeInterceptor());
		
		ReferenceResult result = referencePort.getRecords(1L);
		
		assertNotNull(result);
		int size = result.getCountryOrLanguageOrModule().size();
		assertTrue("Test results length. Expect 8 here.",  size == 8);
	}

	private static class AddSecurityCodeInterceptor extends AbstractSoapInterceptor {
		
		private AddSecurityCodeInterceptor() {
			super(Phase.WRITE);
			addAfter(SoapPreProtocolOutInterceptor.class.getName());
		}
		
		public void handleMessage(SoapMessage message) throws Fault {
			SoapUtil.addSecurityCode(message, "345ttn44$%9");
		}
	}
}
