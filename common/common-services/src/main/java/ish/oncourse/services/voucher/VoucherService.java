package ish.oncourse.services.voucher;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ish.common.types.VoucherStatus;
import ish.math.Money;
import ish.oncourse.model.College;
import ish.oncourse.model.Contact;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.Voucher;
import ish.oncourse.model.VoucherProduct;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.util.ProductUtil;
import ish.util.SecurityUtil;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

public class VoucherService implements IVoucherService {
private static final Logger LOGGER = Logger.getLogger(VoucherService.class);
	
	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private ICayenneService cayenneService;
	
	@Override
	public List<VoucherProduct> getAvailableVoucherProducts() {
		final College currentCollege = webSiteService.getCurrentCollege();
		final Expression qualifier = ExpressionFactory.matchExp(VoucherProduct.COLLEGE_PROPERTY, currentCollege)
			.andExp(ExpressionFactory.matchExp(VoucherProduct.IS_WEB_VISIBLE_PROPERTY, Boolean.TRUE))
			.andExp(ExpressionFactory.matchExp(VoucherProduct.IS_ON_SALE_PROPERTY, Boolean.TRUE));
		@SuppressWarnings("unchecked")
		final List<VoucherProduct> results = cayenneService.sharedContext().performQuery(new SelectQuery(VoucherProduct.class, qualifier));
		final List<Ordering> orderings = new ArrayList<Ordering>();
		orderings.add(new Ordering(VoucherProduct.NAME_PROPERTY, SortOrder.ASCENDING));
		orderings.add(new Ordering(VoucherProduct.PRICE_EX_TAX_PROPERTY, SortOrder.DESCENDING));
		Ordering.orderList(results, orderings);
		return results;
	}
	
	@Override
	public Voucher getVoucherByCode(final String code) {
		final College currentCollege = webSiteService.getCurrentCollege();
		final Expression qualifier = ExpressionFactory.matchExp(Voucher.CODE_PROPERTY, code)
			.andExp(ExpressionFactory.matchExp(Voucher.COLLEGE_PROPERTY, currentCollege))
			.andExp(ExpressionFactory.greaterOrEqualExp(Voucher.EXPIRY_DATE_PROPERTY, new Date()))
			.andExp(ExpressionFactory.greaterExp(Voucher.REDEMPTION_VALUE_PROPERTY, Money.ZERO));
		@SuppressWarnings("unchecked")
		List<Voucher> results = cayenneService.sharedContext().performQuery(new SelectQuery(Voucher.class, qualifier));
		LOGGER.info(String.format("%s found for code %s for college %s", results.size(), code, currentCollege.getId()));
		if (results.size() > 1) {
			LOGGER.error(String.format("%s vouchers found for code %s for college %s. Maybe we need to enlarge the code size?", results.size(), code, 
				currentCollege.getId()));
		}
		return !results.isEmpty() ? results.get(0) : null;
	}
	
	@Override
	public List<Voucher> getAvailableVoucherProductsForUser(final Contact contact) {
		final College currentCollege = webSiteService.getCurrentCollege();
		final Expression qualifier = ExpressionFactory.matchExp(Voucher.CONTACT_PROPERTY, contact)
			.andExp(ExpressionFactory.matchExp(Voucher.COLLEGE_PROPERTY, currentCollege))
			.andExp(ExpressionFactory.greaterOrEqualExp(Voucher.EXPIRY_DATE_PROPERTY, new Date()))
			.andExp(ExpressionFactory.greaterExp(Voucher.REDEMPTION_VALUE_PROPERTY, Money.ZERO))
			.andExp(ExpressionFactory.greaterExp(Voucher.PRODUCT_PROPERTY + "." + VoucherProduct.IS_WEB_VISIBLE_PROPERTY, Boolean.TRUE));
		@SuppressWarnings("unchecked")
		List<Voucher> results = cayenneService.sharedContext().performQuery(new SelectQuery(Voucher.class, qualifier));
		return results;
	}
	
	public Voucher purchaseVoucher(final PaymentIn payment, final InvoiceLine invoiceLine, final VoucherProduct voucherProduct) {
		final Voucher voucher = cayenneService.newContext().newObject(Voucher.class);
		voucher.setCode(SecurityUtil.generateRandomPassword(Voucher.VOUCHER_CODE_LENGTH));
		voucher.setCollege(voucherProduct.getCollege());
		voucher.setContact(payment.getStudent().getContact());
		voucher.setCreated(new Date());
		voucher.setExpiryDate(ProductUtil.calculateExpiryDate(new Date(), voucherProduct.getExpiryType(), voucherProduct.getExpiryDays()));
		voucher.setInvoiceLine(invoiceLine);
		voucher.setRedemptionValue(Money.valueOf(payment.getAmount()));
		voucher.setSource(payment.getSource());
		voucher.setStatus(VoucherStatus.ACTIVE);
		voucher.setProduct(voucherProduct);
		voucher.setRedeemedCoursesCount(0);
		//TODO: implement me
		return voucher;
	}
}
