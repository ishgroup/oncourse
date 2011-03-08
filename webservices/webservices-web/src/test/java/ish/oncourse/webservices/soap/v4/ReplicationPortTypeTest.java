package ish.oncourse.webservices.soap.v4;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import ish.oncourse.webservices.soap.v4.auth.AuthenticationPortType;
import ish.oncourse.webservices.soap.v4.auth.AuthenticationService;
import ish.oncourse.webservices.util.SoapUtil;
import ish.oncourse.webservices.v4.stubs.replication.CourseClassStub;
import ish.oncourse.webservices.v4.stubs.replication.EnrolmentStub;
import ish.oncourse.webservices.v4.stubs.replication.HollowStub;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationRecords;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationStub;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

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

public class ReplicationPortTypeTest extends AbstractWebServiceTest {

	private static final String REPL_WSDL_LOCATION = String.format("http://localhost:%s/services/v4/replication?wsdl", PORT);
	private static final String AUTH_WSDL_LOCATION = String.format("http://localhost:%s/services/v4/auth?wsdl", PORT);

	@BeforeClass
	public static void setupDataSet() throws Exception {
		InputStream st = ReferencePortTypeTest.class.getClassLoader().getResourceAsStream("baseReferenceDataSet.xml");

		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);
		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(DATASOURCES.get(Database.ONCOURSE_REFERENCE).getConnection(), null), dataSet);

		st = ReferencePortTypeTest.class.getClassLoader().getResourceAsStream("baseReplicationDataSet.xml");
		dataSet = new FlatXmlDataSetBuilder().build(st);

		DatabaseOperation.INSERT.execute(new DatabaseConnection(DATASOURCES.get(Database.ONCOURSE).getConnection(), null), dataSet);
	}

	@Test
	public void testGetRecordsSuccess() throws Exception {
		AuthenticationService authService = new AuthenticationService(new URL(AUTH_WSDL_LOCATION));
		AuthenticationPortType authPort = authService.getAuthenticationPort();

		String securityCode = "345ttn44$%9";
		long newCommKey = authPort.authenticate(securityCode, 7059522699886202880l);

		ReplicationService service = new ReplicationService(new URL(REPL_WSDL_LOCATION));
		ReplicationPortType port = service.getReplicationPort();
		
		Client client = ClientProxy.getClient(port);
		client.getOutInterceptors().add(new AddSecurityCodeInterceptor(securityCode, newCommKey));
		
		ReplicationRecords response = port.getRecords();
		
		assertNotNull("Expecting not null response.", response);
		
		List<ReplicationStub> stubs = response.getAttendanceOrBinaryDataOrBinaryInfo();
		
		assertTrue("Expecting on one stub.", stubs.size() == 1);
		
		assertTrue("Expecting enrolment stub.", stubs.get(0) instanceof EnrolmentStub);
		
		EnrolmentStub enrlStub = (EnrolmentStub) stubs.get(0); 
		
		assertTrue("Expecting full CourseClass stub inside enrolment.", enrlStub.getCourseClass() instanceof CourseClassStub);
		
		assertTrue("Expecting hollow stub as a student inside enrolment.", enrlStub.getStudent() instanceof HollowStub);
		
		authPort.logout(newCommKey);
		
	}

	private static class AddSecurityCodeInterceptor extends AbstractSoapInterceptor {
		private String securityCode;
		private Long communicationKey;
		
		private AddSecurityCodeInterceptor(String securityCode, Long communicationKey) {
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
