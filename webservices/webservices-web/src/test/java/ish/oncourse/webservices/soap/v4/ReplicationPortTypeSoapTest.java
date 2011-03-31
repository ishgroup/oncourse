package ish.oncourse.webservices.soap.v4;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import ish.oncourse.webservices.AbstractWebServiceTest;
import ish.oncourse.webservices.soap.v4.auth.AuthenticationPortType;
import ish.oncourse.webservices.soap.v4.auth.AuthenticationService;
import ish.oncourse.webservices.util.SoapUtil;
import ish.oncourse.webservices.v4.stubs.replication.CourseClassStub;
import ish.oncourse.webservices.v4.stubs.replication.EnrolmentStub;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationRecords;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationStub;
import ish.oncourse.webservices.v4.stubs.replication.TransactionGroup;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import javax.sql.DataSource;
import javax.xml.ws.BindingProvider;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.binding.soap.interceptor.SoapPreProtocolOutInterceptor;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.BeforeClass;
import org.junit.Test;

public class ReplicationPortTypeSoapTest extends AbstractWebServiceTest {

	@BeforeClass
	public static void setupDataSet() throws Exception {

		InputStream st = ReplicationPortTypeSoapTest.class.getClassLoader().getResourceAsStream(
				"ish/oncourse/webservices/soap/v4/referenceDataSet.xml");

		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);

		DataSource refDataSource = getDataSource("jdbc/oncourse_reference");

		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(refDataSource.getConnection(), null), dataSet);

		st = ReplicationPortTypeSoapTest.class.getClassLoader().getResourceAsStream(
				"ish/oncourse/webservices/soap/v4/replicationDataSet.xml");
		dataSet = new FlatXmlDataSetBuilder().build(st);

		DataSource onDataSource = getDataSource("jdbc/oncourse");
		DatabaseOperation.INSERT.execute(new DatabaseConnection(onDataSource.getConnection(), null), dataSet);
	}

	@Test
	public void testGetRecordsSuccess() throws Exception {

		AuthenticationPortType authPort = getAuthPort();
		Long newKey = authPort.authenticate("345ttn44$%9", 7059522699886202880l);

		ReplicationPortType replPort = getReplicationPort();

		Client client = ClientProxy.getClient(replPort);
		client.getOutInterceptors().add(new AddSecurityCodeInterceptor("345ttn44$%9", newKey));

		ReplicationRecords result = replPort.getRecords();

		assertNotNull(result);

		List<TransactionGroup> groups = result.getGroups();
		;
		List<ReplicationStub> stubs = groups.get(0).getAttendanceOrBinaryDataOrBinaryInfo();

		int size = stubs.size();

		assertTrue("Expecting only two stub.", stubs.size() == 2);

		boolean hasEnrolment = false, hasCourseClass = false;

		for (ReplicationStub st : stubs) {
			if (st instanceof EnrolmentStub) {
				hasEnrolment = true;
			}
			if (st instanceof CourseClassStub) {
				hasCourseClass = true;
			}
		}

		assertTrue("Expecting enrolment stub.", hasEnrolment);
		assertTrue("Expecting courseClass stub.", hasCourseClass);

		authPort.logout(newKey);
	}

	private static ReplicationPortType getReplicationPort() {
		URL url = ReferencePortType.class.getClassLoader().getResource("wsdl/v4_replication.wsdl");
		ReplicationService service = new ReplicationService(url);

		ReplicationPortType port = service.getReplicationPort();

		Client cl = ClientProxy.getClient(port);

		HTTPConduit http = (HTTPConduit) cl.getConduit();

		HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
		httpClientPolicy.setConnectionTimeout(0);
		httpClientPolicy.setReceiveTimeout(0);

		http.setClient(httpClientPolicy);

		BindingProvider provider = (BindingProvider) port;
		provider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				String.format("http://localhost:%s/services/v4/replication", PORT));

		return port;
	}

	private static AuthenticationPortType getAuthPort() {

		AuthenticationService authService = new AuthenticationService(AuthenticationPortType.class.getClassLoader().getResource(
				"wsdl/v4_auth.wsdl"));

		AuthenticationPortType authPort = authService.getAuthenticationPort();

		Client cl = ClientProxy.getClient(authPort);

		HTTPConduit http = (HTTPConduit) cl.getConduit();

		HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
		httpClientPolicy.setConnectionTimeout(0);
		httpClientPolicy.setReceiveTimeout(0);

		http.setClient(httpClientPolicy);

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
