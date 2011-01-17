package ish.oncourse.webservices.soap;

import java.io.InputStream;
import java.net.URL;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.hsqldb.HsqldbConnection;
import org.dbunit.operation.DatabaseOperation;

public class ReferencePortTypeTest extends AbstractWebServiceTest {
	
	public void testGetMaximumVersion() throws Exception {
		InputStream st = getClass().getClassLoader().getResourceAsStream("baseReferenceDataSet.xml");

		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);
		DatabaseOperation.CLEAN_INSERT.execute(new HsqldbConnection(DATASOURCES.get(Database.ONCOURSE).getConnection(), null), dataSet);

		JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
		Client client = dcf.createClient(new URL(String.format("http://localhost:%s/services/auth?wsdl", String.valueOf(PORT))));
	}
	
	public void testGetRecords() throws Exception {
		
	}
}
