package ish.oncourse.enrol.services.payment;

import ish.oncourse.enrol.checkout.ActionAddDiscount;
import ish.oncourse.enrol.checkout.PurchaseController;
import ish.oncourse.enrol.checkout.PurchaseModelValidator;
import ish.oncourse.enrol.checkout.ValidationResult;
import ish.oncourse.enrol.checkout.model.PurchaseModel;
import ish.oncourse.enrol.pages.Checkout;
import ish.oncourse.enrol.services.concessions.IConcessionsService;
import ish.oncourse.enrol.services.invoice.IInvoiceProcessingService;
import ish.oncourse.enrol.services.student.IStudentService;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Discount;
import ish.oncourse.model.Product;
import ish.oncourse.services.application.IApplicationService;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.datalayer.IDataLayerFactory;
import ish.oncourse.services.discount.IDiscountService;
import ish.oncourse.services.payment.IPaymentService;
import ish.oncourse.services.paymentexpress.INewPaymentGatewayServiceBuilder;
import ish.oncourse.services.paymentexpress.IPaymentGatewayServiceBuilder;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.voucher.IVoucherService;
import ish.oncourse.services.voucher.VoucherRedemptionHelper;
import org.apache.cayenne.query.SelectById;
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
	private INewPaymentGatewayServiceBuilder paymentGatewayServiceBuilder;

	@Inject
	private ITagService tagService;

    @Inject
    private ParallelExecutor parallelExecutor;

	@Inject
	private IPaymentService paymentService;

    @Inject
    private IDataLayerFactory dataLayerFactory;

	@Inject
	private IApplicationService iApplicationService;

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

        ValidationResult validationResult = new ValidationResult();
        validationResult.setMessages(MessagesImpl.forClass(Checkout.class));
		purchaseController.setValidationResult(validationResult);

		purchaseController.setCayenneService(cayenneService);
		purchaseController.setPaymentGatewayServiceBuilder(paymentGatewayServiceBuilder);
		purchaseController.setWebSiteService(webSiteService);
		purchaseController.setTagService(tagService);
        purchaseController.setParallelExecutor(parallelExecutor);
		purchaseController.setPaymentService(paymentService);
        purchaseController.setDataLayerFactory(dataLayerFactory);
		purchaseController.setApplicationService(iApplicationService);

        purchaseController.setVoucherRedemptionHelper(new VoucherRedemptionHelper(model.getObjectContext(), model.getCollege()));

        PurchaseModelValidator purchaseModelValidator = new PurchaseModelValidator();
        purchaseModelValidator.setPurchaseController(purchaseController);
        purchaseController.setModelValidator(purchaseModelValidator);
		

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
		model.setDiscounts(model.localizeObjects(discounts));
		model.setAllowToUsePrevOwing(false);
		return model;
	}

    @Override
    public void updatePurchaseItems(PurchaseController purchaseController) {
        updateCourseClasses(purchaseController);
        updateProducts(purchaseController);
		updateDiscounts(purchaseController);
    }

	private void updateDiscounts(PurchaseController purchaseController) {
		List<Discount> discounts = discountService.getPromotions();

        //add new discounts
        for (Discount discount : discounts) {
            ActionAddDiscount actionAddDiscount = new ActionAddDiscount();
            actionAddDiscount.setController(purchaseController);
            actionAddDiscount.setDiscount(discount);
			actionAddDiscount.setPublishErrors(false);
            actionAddDiscount.action();
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

    private boolean updateProducts(PurchaseController purchaseController) {
        boolean result = false;
        List<Long> productIds = cookiesService.getCookieCollectionValue(Product.SHORTLIST_COOKIE_KEY, Long.class);
        for (Long productId: productIds) {
            boolean value = purchaseController.getModel().containsProduct(productId);
            if (!value)
            {
                Product product = SelectById.query(Product.class, productId).selectOne(purchaseController.getModel().getObjectContext());

                PurchaseController.ActionParameter parameter = new PurchaseController.ActionParameter(PurchaseController.Action.addProduct);
                parameter.setValue(product);
                purchaseController.performAction(parameter);
                result = true;
            }
        }
        return result;
    }
}
