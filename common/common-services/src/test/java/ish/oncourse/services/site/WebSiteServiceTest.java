package ish.oncourse.services.site;

import ish.oncourse.model.WebHostName;
import ish.oncourse.services.ServiceModule;
import ish.oncourse.services.lifecycle.QueueableLifecycleListenerTest;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.ServiceTest;
import org.apache.tapestry5.services.Request;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WebSiteServiceTest extends ServiceTest {
	
	private ICayenneService cayenneService;
	
	@Before
	public void setup() throws Exception {
		initTest("ish.oncourse.services", "service", ServiceModule.class);

		InputStream st = QueueableLifecycleListenerTest.class.getClassLoader().getResourceAsStream(
				"ish/oncourse/services/site/siteDataSet.xml");

		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);
		DataSource dataSource = getDataSource("jdbc/oncourse");
		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(dataSource.getConnection(), null), dataSet);
		
		this.cayenneService = getService(ICayenneService.class);
	}
	
	@Test
	public void testCurrentDomain() throws Exception {
		Request request = mock(Request.class);
		when(request.getServerName()).thenReturn("scc.staging1.oncourse.net.au");
		WebSiteService service = new WebSiteService(request, cayenneService);
		WebHostName host = service.getCurrentDomain();
		assertNull("Checking that there's no host found for tecknical site.", host);
		assertEquals("Checking site key.", "scc", service.getCurrentWebSite().getSiteKey());
		
		Request request2 = mock(Request.class);
		when(request2.getServerName()).thenReturn("tae.test.oncourse.net.au");
		WebSiteService service2 = new WebSiteService(request2, cayenneService);
		WebHostName host2 = service2.getCurrentDomain();
		assertNull("Checking that there's no host found for tecknical site.", host2);
		assertEquals("Checking site key.", "tae", service2.getCurrentWebSite().getSiteKey());
		
		Request request3 = mock(Request.class);
		when(request3.getServerName()).thenReturn("tasm.education.com.au");
		WebSiteService service3 = new WebSiteService(request3, cayenneService);
		WebHostName host3 = service3.getCurrentDomain();
		assertNotNull("Checking that host found.", host3);
		assertEquals("Checking site key.", "tae", host3.getWebSite().getSiteKey());
		
		Request request4 = mock(Request.class);
		when(request4.getServerName()).thenReturn("notfound.education.com.au");
		WebSiteService service4 = new WebSiteService(request4, cayenneService);
		
		assertNull("Expecting null website", service4.getCurrentWebSite());
	}
}
