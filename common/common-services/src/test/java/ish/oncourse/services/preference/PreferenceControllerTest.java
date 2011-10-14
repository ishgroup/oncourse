package ish.oncourse.services.preference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import ish.oncourse.services.ServiceTestModule;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.test.ServiceTest;

import java.io.InputStream;

import javax.sql.DataSource;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PreferenceControllerTest extends ServiceTest {
	
	private PreferenceController prefController;
	
	@Before
	public void setup() throws Exception {
		initTest("ish.oncourse.services", "", ServiceTestModule.class);
		
		InputStream st = PreferenceControllerTest.class.getClassLoader().getResourceAsStream(
				"ish/oncourse/services/preference/preferenceDataSet.xml");

		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);
		DataSource dataSource = getDataSource("jdbc/oncourse");
		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(dataSource.getConnection(), null), dataSet);
		
		ICayenneService cayenneService = getService(ICayenneService.class);
		IWebSiteService webSiteService = getService(IWebSiteService.class);
		
		prefController = new PreferenceController(cayenneService, webSiteService);
	}
	
	@Test
	public void testGetValue() throws Exception {
		boolean smsLicense = prefController.getLicenseSms();
		assertTrue("Check sms license.", smsLicense);
		String smtpHost = prefController.getEmailSMTPHost();
		assertEquals("Check smtp host.", smtpHost, "smtp.text.ish.com");
	}
	
	@Test
	public void testSetValue() throws Exception {
		
		prefController.setAvetmissPostcode("12345");
		assertEquals("Check avetmiss post code", prefController.getAvetmissPostcode(), "12345");
		
		prefController.setLicenseEmail(true);
		assertTrue("Check license email.", prefController.getLicenseEmail());
	}
	
	@After
	public void tearDown() throws Exception {
		prefController = null;
	}
}
