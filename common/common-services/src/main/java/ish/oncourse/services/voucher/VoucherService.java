package ish.oncourse.services.voucher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import ish.oncourse.model.Product;
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
	
	protected IWebSiteService takeWebSiteService() {
		return webSiteService;
	}

	@Override
	public List<Product> getAvailableProducts() {
		return getAvailableProducts(null, null);
	}
	
	@Override
	public Product loadAvailableVoucherProductBySKU(String sku) {
		College currentCollege = takeWebSiteService().getCurrentCollege();
		Expression qualifier = ExpressionFactory.matchExp(Product.COLLEGE_PROPERTY, currentCollege)
			.andExp(ExpressionFactory.matchExp(Product.IS_WEB_VISIBLE_PROPERTY, Boolean.TRUE))
			.andExp(ExpressionFactory.matchExp(Product.IS_ON_SALE_PROPERTY, Boolean.TRUE))
			.andExp(ExpressionFactory.matchExp(Product.SKU_PROPERTY, sku));
		SelectQuery query = new SelectQuery(Product.class, qualifier);
		@SuppressWarnings("unchecked")
		List<Product> results = cayenneService.sharedContext().performQuery(query);
		return !results.isEmpty()? results.get(0) : null;
	}
	
	@Override
	public Product loadAvailableVoucherProductById(Long id) {
		College currentCollege = takeWebSiteService().getCurrentCollege();
		Expression qualifier = ExpressionFactory.matchExp(Product.COLLEGE_PROPERTY, currentCollege)
			.andExp(ExpressionFactory.matchExp(Product.IS_WEB_VISIBLE_PROPERTY, Boolean.TRUE))
			.andExp(ExpressionFactory.matchExp(Product.IS_ON_SALE_PROPERTY, Boolean.TRUE))
			.andExp(ExpressionFactory.matchDbExp(Product.ID_PK_COLUMN, id));
		SelectQuery query = new SelectQuery(Product.class, qualifier);
		@SuppressWarnings("unchecked")
		List<Product> results = cayenneService.sharedContext().performQuery(query);
		return !results.isEmpty()? results.get(0) : null;
	}
	
	@Override
	public List<Product> getAvailableProducts(Integer startDefault, Integer rowsDefault) {
		College currentCollege = takeWebSiteService().getCurrentCollege();
		Expression qualifier = ExpressionFactory.matchExp(Product.COLLEGE_PROPERTY, currentCollege)
			.andExp(ExpressionFactory.matchExp(Product.IS_WEB_VISIBLE_PROPERTY, Boolean.TRUE))
			.andExp(ExpressionFactory.matchExp(Product.IS_ON_SALE_PROPERTY, Boolean.TRUE));
		SelectQuery query = new SelectQuery(Product.class, qualifier);
		if (startDefault != null && rowsDefault != null) {
			query.setFetchOffset(startDefault);
			query.setFetchLimit(rowsDefault);
		}
		@SuppressWarnings("unchecked")
		List<Product> results = cayenneService.sharedContext().performQuery(query);
		List<Ordering> orderings = new ArrayList<Ordering>();
		orderings.add(new Ordering(Product.TYPE_PROPERTY, SortOrder.DESCENDING));
		orderings.add(new Ordering(Product.NAME_PROPERTY, SortOrder.ASCENDING));
		orderings.add(new Ordering(Product.PRICE_EX_TAX_PROPERTY, SortOrder.DESCENDING));
		Ordering.orderList(results, orderings);
		return results;
	}
	
	@Override
	public Voucher getVoucherByCode(String code) {
		College currentCollege = takeWebSiteService().getCurrentCollege();
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
		College currentCollege = takeWebSiteService().getCurrentCollege();
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
	
	/**
     * Add necessary prefetches and assign cache group for course query;
     *
     * @param q course query
     */
    @SuppressWarnings("unused")
	private static void appyCourseClassCacheSettings(SelectQuery q) {

        // TODO: uncomment when after upgrading to newer cayenne where
        // https://issues.apache.org/jira/browse/CAY-1585 is fixed.

        /**
         * q.setCacheStrategy(QueryCacheStrategy.LOCAL_CACHE);
         * q.setCacheGroups(CacheGroup.COURSES.name());
         **/

        q.addPrefetch(VoucherProduct.REDEMPTION_COURSES_PROPERTY);
        //q.addPrefetch(VoucherProduct.PRICE_EX_TAX_PROPERTY);
        q.addPrefetch(VoucherProduct.PRODUCT_ITEMS_PROPERTY);
    }
	
    @Override
	@SuppressWarnings("unchecked")
	public List<Product> loadByIds(Object... ids) {
		if (ids.length == 0) {
            return Collections.emptyList();
        }
        List<Object> params = Arrays.asList(ids);
        SelectQuery q = new SelectQuery(Product.class, ExpressionFactory.inDbExp(VoucherProduct.ID_PK_COLUMN, params));
        //appyCourseClassCacheSettings(q);
        return cayenneService.sharedContext().performQuery(q);
	}
}
