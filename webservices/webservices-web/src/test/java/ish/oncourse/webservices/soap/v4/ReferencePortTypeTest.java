package ish.oncourse.webservices.soap.v4;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.net.URL;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.hsqldb.HsqldbConnection;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Test;

public class ReferencePortTypeTest extends AbstractWebServiceTest {
	
	private static final String WSDL_LOCATION = String.format("http://localhost:%s/services/v4/reference?wsdl", PORT);
	
	@Test
	public void testGetMaximumVersion() throws Exception {
		InputStream st = getClass().getClassLoader().getResourceAsStream("baseReferenceDataSet.xml");

		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);
		DatabaseOperation.CLEAN_INSERT.execute(new HsqldbConnection(DATASOURCES.get(Database.ONCOURSE_REFERENCE).getConnection(), null), dataSet);

		JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
		Client client = dcf.createClient(new URL(WSDL_LOCATION));
		Object[] result = client.invoke("getMaximumVersion");
		
		assertNotNull(result);
		assertTrue("Test result size.", result.length == 1);
		
		Long version = (Long) result[0];
		
		assertTrue("Check that max version is 2.", version.equals(2L));
	}
}
