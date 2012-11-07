package ish.oncourse.enrol.services.payment;

import ish.oncourse.enrol.checkout.PurchaseController;
import ish.oncourse.enrol.checkout.PurchaseModel;
import ish.oncourse.enrol.pages.Checkout;
import ish.oncourse.enrol.services.concessions.IConcessionsService;
import ish.oncourse.enrol.services.invoice.IInvoiceProcessingService;
import ish.oncourse.enrol.services.student.IStudentService;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.discount.IDiscountService;
import ish.oncourse.services.paymentexpress.IPaymentGatewayServiceBuilder;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.voucher.IVoucherService;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.internal.util.MessagesImpl;

public class PurchaseControllerBuilder implements IPurchaseControllerBuilder {
	@Inject
	private IInvoiceProcessingService invoiceProcessingService;

	@Inject
	private IDiscountService discountService;

	@Inject
	private IVoucherService voucherService;

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private IStudentService studentService;

	@Inject
	private ICookiesService cookiesService;

	@Inject
	private ICourseClassService courseClassService;

	@Inject
	private PreferenceController preferenceController;

	@Inject
	private IConcessionsService concessionsService;

	@Inject
	private IPaymentGatewayServiceBuilder paymentGatewayServiceBuilder;

	@Inject
	private ITagService tagService;

	@Override
	public PurchaseController build(PurchaseModel model) {
		PurchaseController purchaseController = new PurchaseController();
		purchaseController.setModel(model);
		purchaseController.setDiscountService(discountService);
		purchaseController.setInvoiceProcessingService(invoiceProcessingService);
		purchaseController.setVoucherService(voucherService);
		purchaseController.setConcessionsService(concessionsService);
		purchaseController.setStudentService(studentService);
		purchaseController.setPreferenceController(preferenceController);
		purchaseController.setMessages(MessagesImpl.forClass(Checkout.class));
		purchaseController.setCayenneService(cayenneService);
		purchaseController.setPaymentGatewayServiceBuilder(paymentGatewayServiceBuilder);
		purchaseController.setWebSiteService(webSiteService);
		purchaseController.setTagService(tagService);
		return purchaseController;
	}
}
