package ish.oncourse.webservices.services.site;

import ish.oncourse.model.College;
import ish.oncourse.model.PaymentTransaction;
import ish.oncourse.model.WebHostName;
import ish.oncourse.model.WebSite;
import ish.oncourse.services.payment.IPaymentService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.webservices.util.SoapUtil;

import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.springframework.beans.factory.annotation.Autowired;

public class WebSiteServiceOverride implements IWebSiteService {
	
	@Inject
	@Autowired
	private Request request;
	
	@Inject
	@Autowired
	private IPaymentService paymentService;

	@Override
	public WebSite getCurrentWebSite() {
		if (getCurrentCollege() == null) {
			return null;
		}
		else {
			List<WebSite> webSites = getCurrentCollege().getWebSites();
			return (webSites.size() > 0) ? webSites.get(0) : null;
		}
	}

	@Override
	public College getCurrentCollege() {
		College college = null;
		
		if (request.getAttribute(SoapUtil.REQUESTING_COLLEGE) != null) {
			college = (College) request.getAttribute(SoapUtil.REQUESTING_COLLEGE);
		}
		else {
			String referenceId = request.getParameter(PaymentTransaction.REFERENCE_ID_PARAM);
			if (referenceId != null) {
				PaymentTransaction t = paymentService.paymentTransactionByReferenceId(referenceId);
				if (t != null) {
					college = t.getPayment().getCollege();
				}
			}
		}
		
		return college;
	}

	@Override
	public WebHostName getCurrentDomain() {
		return (getCurrentWebSite() != null) ? getCurrentWebSite().getToWebHostName() : null;
	}
}
