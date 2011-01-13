package ish.oncourse.enrol.utils;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import ish.oncourse.enrol.services.payment.DisabledPaymentGatewayService;
import ish.oncourse.enrol.services.payment.PaymentExpressGatewayService;
import ish.oncourse.enrol.services.payment.TestPaymentGatewayService;
import ish.oncourse.model.College;
import ish.oncourse.model.PaymentGatewayType;
import ish.oncourse.services.site.IWebSiteService;

import org.apache.tapestry5.ioc.ServiceResources;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test for the {@link PaymentGatewayServiceBuilder}.
 * 
 * @author ksenia
 * 
 */
public class PaymentGatewayServiceBuilderTest {
	
	/**
	 * Instance to test.
	 */
	private static PaymentGatewayServiceBuilder builder;
	
	/**
	 * Mock of the {@link IWebSiteService}.
	 */
	private static IWebSiteService webSiteService;
	
	/**
	 * Mock of the {@link ServiceResources}.
	 */
	private static ServiceResources resources;
	
	/**
	 * Instance to imitate the college which payment gateway is "OFF".
	 */
	private static College collegeWithDisabledGateway;
	
	/**
	 * Instance to imitate the college which payment gateway is "TEST".
	 */
	private static College collegeWithTestGateway;
	
	/**
	 * Instance to imitate the college which payment gateway is
	 * "ON"(PaymentExpress).
	 */
	private static College collegeWithPaymentExpressGateway;
	
	/**
	 * Initializes parameters for the whole test.
	 */
	@BeforeClass
	public static void init() {
		builder = new PaymentGatewayServiceBuilder();
		resources = mock(ServiceResources.class);
		webSiteService = mock(IWebSiteService.class);
		collegeWithDisabledGateway = new College();

		collegeWithTestGateway = new College();
		collegeWithTestGateway.setPaymentGatewayType(PaymentGatewayType.TEST);

		collegeWithPaymentExpressGateway = new College();
		collegeWithPaymentExpressGateway.setPaymentGatewayType(PaymentGatewayType.PAYMENT_EXPRESS);

		when(resources.getService(IWebSiteService.class)).thenReturn(webSiteService);
		when(webSiteService.getCurrentCollege()).thenReturn(collegeWithDisabledGateway,
				collegeWithDisabledGateway, collegeWithTestGateway,
				collegeWithPaymentExpressGateway);
	}

	/**
	 * Emulates the situation when the payment gateway for the college is
	 * disabled(either {@link PaymentGatewayType#DISABLED} or null). The
	 * {@link #builder} should build the instance of
	 * {@link DisabledPaymentGatewayService}.
	 */
	@Test
	public void testCollegeWithDisabledGateway() {
		assertTrue(builder.buildService(resources) instanceof DisabledPaymentGatewayService);
		collegeWithDisabledGateway.setPaymentGatewayType(PaymentGatewayType.DISABLED);
		assertTrue(builder.buildService(resources) instanceof DisabledPaymentGatewayService);
	}

	/**
	 * Emulates the situation when the payment gateway for the college is test(
	 * {@link PaymentGatewayType#TEST}). The {@link #builder} should build the
	 * instance of {@link TestPaymentGatewayService}.
	 */
	@Test
	public void testCollegeWithTestGateway() {
		assertTrue(builder.buildService(resources) instanceof TestPaymentGatewayService);
	}

	/**
	 * Emulates the situation when the payment gateway for the college is on -
	 * PaymentExpress({@link PaymentGatewayType#PAYMENT_EXPRESS}). The
	 * {@link #builder} should build the instance of
	 * {@link TestPaymentGatewayService}.
	 */
	@Test
	public void testCollegeWithPaymentExpressGateway() {
		assertTrue(builder.buildService(resources) instanceof PaymentExpressGatewayService);
	}

}
