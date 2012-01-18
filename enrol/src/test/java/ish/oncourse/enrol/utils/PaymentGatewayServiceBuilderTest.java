package ish.oncourse.enrol.utils;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import ish.oncourse.model.PaymentGatewayType;
import ish.oncourse.services.paymentexpress.DisabledPaymentGatewayService;
import ish.oncourse.services.paymentexpress.PaymentExpressGatewayService;
import ish.oncourse.services.paymentexpress.PaymentGatewayServiceBuilder;
import ish.oncourse.services.paymentexpress.TestPaymentGatewayService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.PreferenceController;

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
	 * Mock of the {@link PreferenceController}.
	 */
	private static PreferenceController preferenceController;
	
	/**
	 * Mock of the {@link ServiceResources}.
	 */
	private static ServiceResources resources;
	
	/**
	 * Initializes parameters for the whole test.
	 */
	@BeforeClass
	public static void init() {
		
		resources = mock(ServiceResources.class);
		preferenceController = mock(PreferenceController.class);
		
		builder = new PaymentGatewayServiceBuilder(preferenceController, mock(ICayenneService.class));

		when(resources.getService(PreferenceController.class)).thenReturn(preferenceController);
		when(preferenceController.getPaymentGatewayType()).thenReturn(
				PaymentGatewayType.DISABLED, PaymentGatewayType.TEST,
				PaymentGatewayType.PAYMENT_EXPRESS);
	}

	/**
	 * Emulates the situation when the payment gateway for the college is
	 * disabled(either {@link PaymentGatewayType#DISABLED} or null). The
	 * {@link #builder} should build the instance of
	 * {@link DisabledPaymentGatewayService}.
	 */
	@Test
	public void testCollegeWithDisabledGateway() {
		assertTrue(builder.buildService() instanceof DisabledPaymentGatewayService);
	}

	/**
	 * Emulates the situation when the payment gateway for the college is test(
	 * {@link PaymentGatewayType#TEST}). The {@link #builder} should build the
	 * instance of {@link TestPaymentGatewayService}.
	 */
	@Test
	public void testCollegeWithTestGateway() {
		assertTrue(builder.buildService() instanceof TestPaymentGatewayService);
	}

	/**
	 * Emulates the situation when the payment gateway for the college is on -
	 * PaymentExpress({@link PaymentGatewayType#PAYMENT_EXPRESS}). The
	 * {@link #builder} should build the instance of
	 * {@link TestPaymentGatewayService}.
	 */
	@Test
	public void testCollegeWithPaymentExpressGateway() {
		assertTrue(builder.buildService() instanceof PaymentExpressGatewayService);
	}

}
