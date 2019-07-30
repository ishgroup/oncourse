package ish.oncourse.util.payment;

import ish.oncourse.model.College;
import ish.oncourse.model.Preference;
import ish.oncourse.services.ServiceTestModule;
import ish.oncourse.services.cache.NoopQueryCache;
import ish.oncourse.services.payment.IPaymentService;
import ish.oncourse.services.paymentexpress.INewPaymentGatewayServiceBuilder;
import ish.oncourse.services.paymentexpress.NewPaymentExpressGatewayService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.LoadDataSet;
import ish.oncourse.test.tapestry.ServiceTest;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.tapestry5.internal.test.TestableRequest;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Session;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static ish.oncourse.util.payment.PaymentProcessController.PaymentProcessState.FILL_PAYMENT_DETAILS;
import static org.junit.Assert.*;

public class PaymentProcessControllerBuilderTest extends ServiceTest {
	private ICayenneService cayenneService;
    private IPaymentService paymentService;
    private INewPaymentGatewayServiceBuilder paymentGatewayServiceBuilder;
    private Request request;
    
	@Before
    public void setup() throws Exception {
        initTest("ish.oncourse.webservices.services", "services", new NoopQueryCache(), ServiceTestModule.class);
		new LoadDataSet().dataSetFile("ish/oncourse/util/payment/PaymentProcessControllerTest.xml").load(testContext.getDS());
        cayenneService = getService(ICayenneService.class);
        paymentService = getService(IPaymentService.class);
        paymentGatewayServiceBuilder = getService(INewPaymentGatewayServiceBuilder.class);
        request = getService(TestableRequest.class);
        assertNotNull("TestableRequest should be binded", request);
    }
	
	@Test
	public void testBuild() {
		String sessionId = "SESSIONID";
		//test invalid sessionid withot valid session
		PaymentProcessControllerBuilder builder = new PaymentProcessControllerBuilder(new MockParallelExecutor(), paymentGatewayServiceBuilder, cayenneService, 
			paymentService, request.getSession(false));
        PaymentInModel paymentInModel = PaymentInModelFromSessionIdBuilder.valueOf("illegalSessionId", cayenneService.newContext()).build().getModel();

		assertNull("paymentIn is null for case when payment not found", paymentInModel.getPaymentIn());

		//test valid sessionid without valid session
		try {
			paymentInModel = PaymentInModelFromSessionIdBuilder.valueOf(sessionId, cayenneService.newContext()).build().getModel();
            builder.build(paymentInModel);
			fail("Builder should throw an exception for cases when session is null");
		} catch (Throwable t) {
			assertTrue("Builder should throw an Illegal argument exception for cases when session is null", 
				(t instanceof IllegalArgumentException && 
					"PaymentProcessControllerBuilder can't build the PaymentProcessController without valid session!".equals(t.getMessage())));
		}
		//create session
		final Session session = request.getSession(true);
		//test real execution emulation

		PaymentProcessController[] paymentProcessControllers = new PaymentProcessController[1];
		builder = new PaymentProcessControllerBuilder(new MockParallelExecutor(paymentProcessControllers[0]), paymentGatewayServiceBuilder, cayenneService, paymentService, session);
		assertNotNull("Correctly initiated builder should receive not null PaymentGatewayService", builder.receivePaymentGatewayService());
		paymentInModel = PaymentInModelFromSessionIdBuilder.valueOf(sessionId, cayenneService.newContext()).build().getModel();
        paymentProcessControllers[0] = builder.build(paymentInModel);
		PaymentProcessController paymentProcessController = paymentProcessControllers[0];

		assertNotNull("build of paymentProcessController should return not null result for case when payment found", paymentProcessController);
		//update parallel executor because unable to finally init them for test on startup
		assertNotNull("Correctly inited builder should receive not null PaymentGatewayService", builder.receivePaymentGatewayService());
		assertTrue("If PaymentGatewayType evaluated for live value after session check this value should returns properly", 
			builder.receivePaymentGatewayService() instanceof NewPaymentExpressGatewayService);
		Assert.assertNotNull("paymentProcessController.getPaymentIn()", paymentProcessController.getPaymentIn());
        assertEquals("paymentProcessController.getCurrentState()", FILL_PAYMENT_DETAILS, paymentProcessController.getCurrentState());
        
        //cleanup previous session data
        session.setAttribute(College.REQUESTING_COLLEGE_ATTRIBUTE, null);
        //test incorrect preference emulation
        builder = new PaymentProcessControllerBuilder(new MockParallelExecutor(), paymentGatewayServiceBuilder, cayenneService, paymentService, session);
		assertNotNull("Correctly initiated builder should receive not null PaymentGatewayService", builder.receivePaymentGatewayService());
		ObjectContext content = cayenneService.newNonReplicatingContext();

		Preference preference =  ObjectSelect.query(Preference.class, ExpressionFactory.matchDbExp(Preference.ID_PK_COLUMN, 1L)).selectOne(content);
		assertNotNull("Preference should not null", preference);
		content.deleteObjects(preference);
		content.commitChanges();
		paymentInModel = PaymentInModelFromSessionIdBuilder.valueOf(sessionId, cayenneService.newContext()).build().getModel();

		try {
			builder.build(paymentInModel);
			fail("illegal state exception should throws when college have not preference allow to use payment express");
		} catch (Throwable t) {
			assertTrue("Builder should throw an Illegal state exception for cases when college have not preference allow to use payment express", 
				(t instanceof IllegalStateException && "Unable to process payments for this college.".equals(t.getMessage())));
		}
	}

}
