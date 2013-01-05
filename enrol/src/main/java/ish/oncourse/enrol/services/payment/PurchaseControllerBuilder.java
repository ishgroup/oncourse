package ish.oncourse.enrol.services.payment;

import ish.oncourse.enrol.checkout.ActionAddDiscount;
import ish.oncourse.enrol.checkout.ActionRemoveDiscount;
import ish.oncourse.enrol.checkout.PurchaseController;
import ish.oncourse.enrol.checkout.PurchaseModel;
import ish.oncourse.enrol.pages.Checkout;
import ish.oncourse.enrol.services.concessions.IConcessionsService;
import ish.oncourse.enrol.services.invoice.IInvoiceProcessingService;
import ish.oncourse.enrol.services.student.IStudentService;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Discount;
import ish.oncourse.model.Product;
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
import org.apache.tapestry5.ioc.services.ParallelExecutor;

import java.util.List;

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

    @Inject
    private ParallelExecutor parallelExecutor;


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
        purchaseController.setParallelExecutor(parallelExecutor);
		return purchaseController;
	}


	@Override
	public PurchaseModel build() {
		List<Long> orderedClassesIds = cookiesService.getCookieCollectionValue(CourseClass.SHORTLIST_COOKIE_KEY, Long.class);
		List<Long> productIds = cookiesService.getCookieCollectionValue(Product.SHORTLIST_COOKIE_KEY, Long.class);
		List<CourseClass> courseClasses = courseClassService.loadByIds(orderedClassesIds);
		List<Product> products = voucherService.loadByIds(productIds);
		List<Discount> discounts = discountService.getPromotions();

		PurchaseModel model = new PurchaseModel();
		model.setObjectContext(cayenneService.newContext());
		model.setClasses(model.localizeObjects(courseClasses));
		model.setProducts(model.localizeObjects(products));
		model.setCollege(model.localizeObject(webSiteService.getCurrentCollege()));
		model.setWebSite(model.localizeObject(webSiteService.getCurrentWebSite()));
		model.setDiscounts(discounts);
		return model;
	}

    @Override
    public void updatePurchaseItems(PurchaseController purchaseController) {
        boolean result = updateCourseClasses(purchaseController);
        //todo
        List<Long> productIds = cookiesService.getCookieCollectionValue(Product.SHORTLIST_COOKIE_KEY, Long.class);

		updateDiscounts(purchaseController);
    }

	private void updateDiscounts(PurchaseController purchaseController) {
		List<Discount> discounts = discountService.getPromotions();
        List<Discount> discountsModel =  purchaseController.getModel().getDiscounts();

        //remove  discounts
        for (Discount discountModel : discountsModel) {
            boolean contains =false;
            for (Discount discount : discounts) {
                if (discountModel.getId().equals(discount.getId()))
                {
                    contains = true;
                    break;
                }
            }
            if (!contains)
            {
                ActionRemoveDiscount actionAddDiscount = PurchaseController.Action.removeDiscount.createAction(purchaseController);
                actionAddDiscount.setDiscount(discountModel);
                actionAddDiscount.action();
            }
        }

        //add new discounts
        for (Discount discount : discounts) {
            if (!purchaseController.getModel().containsDiscount(discount))
            {
                ActionAddDiscount actionAddDiscount = PurchaseController.Action.addDiscount.createAction(purchaseController);
                actionAddDiscount.setDiscount(discount);
                actionAddDiscount.action();
            }
		}

    }

	private boolean updateCourseClasses(PurchaseController purchaseController) {
        boolean result = false;
        List<Long> orderedClassesIds = cookiesService.getCookieCollectionValue(CourseClass.SHORTLIST_COOKIE_KEY, Long.class);
        for (Long classId: orderedClassesIds) {
            boolean value = purchaseController.getModel().containsClassWith(classId);
            if (!value)
            {
                CourseClass courseClass = purchaseController.getModel().localizeObject(
                        courseClassService.loadByIds(classId).get(0));
                purchaseController.getModel().addClass(courseClass);
                PurchaseController.ActionParameter parameter = new PurchaseController.ActionParameter(PurchaseController.Action.addCourseClass);
                parameter.setValue(courseClass);
                purchaseController.performAction(parameter);
                result = true;
            }
        }
        return result;
    }
}
