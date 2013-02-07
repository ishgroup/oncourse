package ish.oncourse.util.payment;

import static ish.oncourse.util.payment.PaymentProcessController.PaymentProcessState.FILL_PAYMENT_DETAILS;
import static org.junit.Assert.*;

import java.io.InputStream;

import javax.sql.DataSource;

import org.apache.tapestry5.services.Session;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ish.oncourse.model.College;
import ish.oncourse.model.PaymentGatewayType;
import ish.oncourse.services.ServiceTestModule;
import ish.oncourse.services.payment.IPaymentService;
import ish.oncourse.services.paymentexpress.DisabledPaymentGatewayService;
import ish.oncourse.services.paymentexpress.IPaymentGatewayServiceBuilder;
import ish.oncourse.services.paymentexpress.PaymentExpressGatewayService;
import ish.oncourse.services.paymentexpress.PaymentGatewayServiceBuilder;
import ish.oncourse.services.paymentexpress.TestPaymentGatewayService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.test.ServiceTest;

public class PaymentProcessControllerBuilderTest extends ServiceTest {
	private ICayenneService cayenneService;
    private IPaymentService paymentService;
    
	@Before
    public void setup() throws Exception {
        initTest("ish.oncourse.webservices.services", "services", ServiceTestModule.class);
        InputStream st = PaymentProcessControllerTest.class.getClassLoader().getResourceAsStream("ish/oncourse/util/payment/PaymentProcessControllerTest.xml");
        FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);
        DataSource onDataSource = getDataSource("jdbc/oncourse");
        DatabaseConnection dbConnection = new DatabaseConnection(onDataSource.getConnection(), null);
        dbConnection.getConfig().setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, false);
        DatabaseOperation.CLEAN_INSERT.execute(dbConnection, dataSet);
        cayenneService = getService(ICayenneService.class);
        paymentService = getService(IPaymentService.class);
    }
	
	@Test
	public void testBuild() {
		final Session session = new MockSession();
		String sessionId = "SESSIONID";
		//test invalid sessionid withot valid session
		PaymentProcessControllerBuilder builder = new PaymentProcessControllerBuilder(new MockParallelExecutor(), null, cayenneService, 
			paymentService, null);
		PaymentProcessController paymentProcessController = builder.build("illegalSessionId");
		assertNull("build of paymentProcessController should return null for case when payment not found", paymentProcessController);
		//test valid sessionid without valid session
		try {
			paymentProcessController = builder.build(sessionId);
			assertFalse("Builder should throw an exception for cases when session is null", true);
		} catch (Throwable t) {
			assertTrue("Builder should throw an Illegal argument exception for cases when session is null", 
				(t instanceof IllegalArgumentException && 
					"PaymentProcessControllerBuilder can't build the PaymentProcessController without valid session!".equals(t.getMessage())));
		}
		
		//test correct PaymentGatewayType calculation for various data 
		//check disabled value
		PreferenceController customPreferenceController = new PreferenceController() {
			@Override
			public synchronized PaymentGatewayType getPaymentGatewayType() {
				return PaymentGatewayType.DISABLED;
			}
		};
		IPaymentGatewayServiceBuilder paymentGatewayServiceBuilder = new PaymentGatewayServiceBuilder(customPreferenceController, cayenneService);
		builder = new PaymentProcessControllerBuilder(new MockParallelExecutor(), paymentGatewayServiceBuilder, cayenneService, paymentService, session);
		assertNotNull("Correctly inited builder should receive not null PaymentGatewayService", builder.receivePaymentGatewayService());
		assertTrue("If PaymentGatewayType is disabled this value should returns properly", 
			builder.receivePaymentGatewayService() instanceof DisabledPaymentGatewayService);
		//check test value
		customPreferenceController = new PreferenceController() {
			@Override
			public synchronized PaymentGatewayType getPaymentGatewayType() {
				return PaymentGatewayType.TEST;
			}
		};
		paymentGatewayServiceBuilder = new PaymentGatewayServiceBuilder(customPreferenceController, cayenneService);
		builder = new PaymentProcessControllerBuilder(new MockParallelExecutor(), paymentGatewayServiceBuilder, cayenneService, paymentService, session);
		assertNotNull("Correctly inited builder should receive not null PaymentGatewayService", builder.receivePaymentGatewayService());
		assertTrue("If PaymentGatewayType set to test this value also should returns properly", 
			builder.receivePaymentGatewayService() instanceof TestPaymentGatewayService);
		//check live value
		customPreferenceController = new PreferenceController() {
			@Override
			public synchronized PaymentGatewayType getPaymentGatewayType() {
				return PaymentGatewayType.PAYMENT_EXPRESS;
			}
		};
		paymentGatewayServiceBuilder = new PaymentGatewayServiceBuilder(customPreferenceController, cayenneService);
		builder = new PaymentProcessControllerBuilder(new MockParallelExecutor(), paymentGatewayServiceBuilder, cayenneService, paymentService, session);
		assertNotNull("Correctly inited builder should receive not null PaymentGatewayService", builder.receivePaymentGatewayService());
		assertTrue("If PaymentGatewayType set to correct live value this value should returns properly", 
			builder.receivePaymentGatewayService() instanceof PaymentExpressGatewayService);
		
		//test real execution emulation
		//with session check
		customPreferenceController = new PreferenceController() {
			@Override
			protected String getValue(String key, boolean isUserPref) {
				if (PAYMENT_GATEWAY_TYPE.equals(key)) {
					if (session.getAttribute(College.REQUESTING_COLLEGE_ATTRIBUTE) == null) {
						return null;
					} else {
						return Long.valueOf(1l).equals(session.getAttribute(College.REQUESTING_COLLEGE_ATTRIBUTE)) ? 
							PaymentGatewayType.PAYMENT_EXPRESS.toString() : null;
					}
				}
				return super.getValue(key, isUserPref);
			}
		};
		paymentGatewayServiceBuilder = new PaymentGatewayServiceBuilder(customPreferenceController, cayenneService);
		builder = new PaymentProcessControllerBuilder(new MockParallelExecutor(), paymentGatewayServiceBuilder, cayenneService, paymentService, session);
		assertNotNull("Correctly inited builder should receive not null PaymentGatewayService", builder.receivePaymentGatewayService());
		assertTrue("If PaymentGatewayType may be only disabled before builder.build(sessionId) call", 
				builder.receivePaymentGatewayService() instanceof DisabledPaymentGatewayService);
		paymentProcessController = builder.build(sessionId);
		assertNotNull("build of paymentProcessController should return not null result for case when payment found", paymentProcessController);
		//update parallel executor because unable to finally init them for test on startup
		paymentProcessController.setParallelExecutor(new MockParallelExecutor(paymentProcessController));
		assertNotNull("Correctly inited builder should receive not null PaymentGatewayService", builder.receivePaymentGatewayService());
		assertTrue("If PaymentGatewayType evaluated for live value after session check this value should returns properly", 
			builder.receivePaymentGatewayService() instanceof PaymentExpressGatewayService);
		Assert.assertNotNull("paymentProcessController.getPaymentIn()", paymentProcessController.getPaymentIn());
        assertEquals("paymentProcessController.getCurrentState()", FILL_PAYMENT_DETAILS, paymentProcessController.getCurrentState());
        
        //test incorrect preference emulation
        customPreferenceController = new PreferenceController() {
			@Override
			protected String getValue(String key, boolean isUserPref) {
				if (PAYMENT_GATEWAY_TYPE.equals(key)) {
					if (session.getAttribute(College.REQUESTING_COLLEGE_ATTRIBUTE) == null) {
						return null;
					} else {
						return Long.valueOf(-1).equals(session.getAttribute(College.REQUESTING_COLLEGE_ATTRIBUTE)) ? 
							PaymentGatewayType.PAYMENT_EXPRESS.toString() : null;
					}
				}
				return super.getValue(key, isUserPref);
			}
		};
		paymentGatewayServiceBuilder = new PaymentGatewayServiceBuilder(customPreferenceController, cayenneService);
		builder = new PaymentProcessControllerBuilder(new MockParallelExecutor(), paymentGatewayServiceBuilder, cayenneService, paymentService, session);
		assertNotNull("Correctly inited builder should receive not null PaymentGatewayService", builder.receivePaymentGatewayService());
		assertTrue("If PaymentGatewayType may be only disabled before builder.build(sessionId) call", 
				builder.receivePaymentGatewayService() instanceof DisabledPaymentGatewayService);
		try {
			paymentProcessController = builder.build(sessionId);
			assertFalse("illegal state exception should throws when college have not preference allow to use payment express", true);
		} catch (Throwable t) {
			assertTrue("Builder should throw an Illegal state exception for cases when college have not preference allow to use payment express", 
				(t instanceof IllegalStateException && "Unable to process payments for this college.".equals(t.getMessage())));
		}
	}

}
