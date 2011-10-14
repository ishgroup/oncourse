package ish.oncourse.services.paymentexpress;

import ish.oncourse.model.College;
import ish.oncourse.services.ServiceModule;
import ish.oncourse.services.site.IWebSiteService;

import org.apache.tapestry5.ioc.annotations.Inject;

public class PaymentGatewayServiceBuilder implements IPaymentGatewayServiceBuilder {

	private IWebSiteService webSiteService;

	@Inject
	public PaymentGatewayServiceBuilder(IWebSiteService webSiteService) {
		super();
		this.webSiteService = webSiteService;
	}

	/**
	 * Defines the appropriate payment gateway service in dependence on the
	 * college.paymentGatewayType property. {@inheritDoc}
	 * 
	 */
	public IPaymentGatewayService buildService() {
		College currentCollege = webSiteService.getCurrentCollege();
		return buildService(currentCollege);
	}

	private IPaymentGatewayService buildService(College currentCollege) {

		boolean isInTestMode = "true".equalsIgnoreCase(System.getProperty(ServiceModule.APP_TEST_MODE));

		if (isInTestMode) {
			return new TestPaymentGatewayService();
		}

		if (!currentCollege.isPaymentGatewayEnabled()) {
			return new DisabledPaymentGatewayService();
		}

		switch (currentCollege.getPaymentGatewayType()) {
		case PAYMENT_EXPRESS:
			return new PaymentExpressGatewayService();
		case TEST:
			return new TestPaymentGatewayService();
		default:
			return new TestPaymentGatewayService();
		}
	}
}
