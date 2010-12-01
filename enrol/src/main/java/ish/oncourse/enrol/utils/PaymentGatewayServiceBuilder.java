package ish.oncourse.enrol.utils;

import ish.oncourse.enrol.services.payment.IPaymentGatewayService;
import ish.oncourse.enrol.services.payment.TestPaymentGatewayService;
import ish.oncourse.model.College;
import ish.oncourse.services.site.IWebSiteService;

import org.apache.tapestry5.ioc.ServiceBuilder;
import org.apache.tapestry5.ioc.ServiceResources;

public class PaymentGatewayServiceBuilder implements ServiceBuilder<IPaymentGatewayService>{
	
	public IPaymentGatewayService buildService(ServiceResources resources) {
		IWebSiteService service = resources.getService(IWebSiteService.class);
		College currentCollege = service.getCurrentCollege();
		//TODO define payment gateway implementation depending on college
		return new TestPaymentGatewayService();
	}

}
