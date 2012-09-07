package ish.oncourse.services.voucher;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ish.common.types.VoucherStatus;
import ish.math.Money;
import ish.oncourse.model.College;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentInLine;
import ish.oncourse.model.Student;
import ish.oncourse.model.Voucher;
import ish.oncourse.model.VoucherProduct;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

public class VoucherService implements IVoucherService {
	private static final String OBJECT_RELATIONSHIP_SEPARATOR_STRING = ".";
	private static final Logger LOGGER = Logger.getLogger(VoucherService.class);
	private final String VOUCHER_OWNER_RELATION = Voucher.INVOICE_LINE_PROPERTY + OBJECT_RELATIONSHIP_SEPARATOR_STRING + InvoiceLine.INVOICE_PROPERTY 
		+ OBJECT_RELATIONSHIP_SEPARATOR_STRING + Invoice.PAYMENT_IN_LINES_PROPERTY + OBJECT_RELATIONSHIP_SEPARATOR_STRING 
		+ PaymentInLine.PAYMENT_IN_PROPERTY + OBJECT_RELATIONSHIP_SEPARATOR_STRING + PaymentIn.STUDENT_PROPERTY 
		+ OBJECT_RELATIONSHIP_SEPARATOR_STRING + Student.CONTACT_PROPERTY;
	
	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private ICayenneService cayenneService;
	
	public VoucherService() {}
	
	public VoucherService(IWebSiteService webSiteService, ICayenneService cayenneService) {
		this.webSiteService = webSiteService;
		this.cayenneService = cayenneService;
	}
	
	protected IWebSiteService getWebSiteService() {
		return webSiteService;
	}

	@Override
	public List<VoucherProduct> getAvailableVoucherProducts() {
		College currentCollege = getWebSiteService().getCurrentCollege();
		Expression qualifier = ExpressionFactory.matchExp(VoucherProduct.COLLEGE_PROPERTY, currentCollege)
			.andExp(ExpressionFactory.matchExp(VoucherProduct.IS_WEB_VISIBLE_PROPERTY, Boolean.TRUE))
			.andExp(ExpressionFactory.matchExp(VoucherProduct.IS_ON_SALE_PROPERTY, Boolean.TRUE));
		@SuppressWarnings("unchecked")
		List<VoucherProduct> results = cayenneService.sharedContext().performQuery(new SelectQuery(VoucherProduct.class, qualifier));
		List<Ordering> orderings = new ArrayList<Ordering>();
		orderings.add(new Ordering(VoucherProduct.NAME_PROPERTY, SortOrder.ASCENDING));
		orderings.add(new Ordering(VoucherProduct.PRICE_EX_TAX_PROPERTY, SortOrder.DESCENDING));
		Ordering.orderList(results, orderings);
		return results;
	}
	
	@Override
	public Voucher getVoucherByCode(String code) {
		College currentCollege = getWebSiteService().getCurrentCollege();
		Expression qualifier = ExpressionFactory.matchExp(Voucher.CODE_PROPERTY, code)
			.andExp(ExpressionFactory.matchExp(Voucher.COLLEGE_PROPERTY, currentCollege))
			.andExp(ExpressionFactory.greaterOrEqualExp(Voucher.EXPIRY_DATE_PROPERTY, new Date()))
			.andExp(ExpressionFactory.greaterExp(Voucher.REDEMPTION_VALUE_PROPERTY, Money.ZERO)
			.andExp(ExpressionFactory.matchExp(Voucher.STATUS_PROPERTY, VoucherStatus.ACTIVE)));
		@SuppressWarnings("unchecked")
		List<Voucher> results = cayenneService.sharedContext().performQuery(new SelectQuery(Voucher.class, qualifier));
		LOGGER.info(String.format("%s found for code %s for college %s", results.size(), code, currentCollege.getId()));
		if (results.size() > 1) {
			LOGGER.warn(String.format("%s vouchers found for code %s for college %s. Maybe we need to enlarge the code size?", results.size(), code, 
				currentCollege.getId()));
		}
		return !results.isEmpty() ? results.get(0) : null;
	}
	
	@Override
	public List<Voucher> getAvailableVouchersForUser(Contact contact) {
		College currentCollege = getWebSiteService().getCurrentCollege();
		Expression qualifier = ExpressionFactory.matchExp(Voucher.COLLEGE_PROPERTY, currentCollege)
			.andExp(ExpressionFactory.greaterOrEqualExp(Voucher.EXPIRY_DATE_PROPERTY, new Date()))
			.andExp(ExpressionFactory.greaterExp(Voucher.REDEMPTION_VALUE_PROPERTY, Money.ZERO))
			.andExp(ExpressionFactory.matchExp(Voucher.PRODUCT_PROPERTY + OBJECT_RELATIONSHIP_SEPARATOR_STRING 
				+ VoucherProduct.IS_WEB_VISIBLE_PROPERTY, Boolean.TRUE))
			.andExp(ExpressionFactory.matchExp(Voucher.CONTACT_PROPERTY, contact).orExp(ExpressionFactory.matchExp(VOUCHER_OWNER_RELATION, contact)));
		@SuppressWarnings("unchecked")
		List<Voucher> results = cayenneService.sharedContext().performQuery(new SelectQuery(Voucher.class, qualifier));
		return results;
	}
	
	@Override
	public PaymentIn preparePaymentInForVoucherPurchase(VoucherProduct voucherProduct, Money voucherPrice, Contact payer, Contact owner) {
		PaymentIn payment = new PurchaseVoucherBuilder(voucherProduct, voucherPrice, payer, owner).prepareVoucherPurchase();
		return payment;
	}
}
