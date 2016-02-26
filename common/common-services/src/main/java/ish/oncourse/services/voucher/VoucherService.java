package ish.oncourse.services.voucher;

import ish.common.types.PaymentSource;
import ish.common.types.ProductStatus;
import ish.math.Money;
import ish.oncourse.model.*;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.util.ProductUtil;
import ish.util.SecurityUtil;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.*;

public class VoucherService implements IVoucherService {
	private static final String OBJECT_RELATIONSHIP_SEPARATOR_STRING = ".";
	private static final Logger logger = LogManager.getLogger();
	private final String VOUCHER_OWNER_RELATION = Voucher.INVOICE_LINE_PROPERTY + OBJECT_RELATIONSHIP_SEPARATOR_STRING + InvoiceLine.INVOICE_PROPERTY
			+ OBJECT_RELATIONSHIP_SEPARATOR_STRING + Invoice.PAYMENT_IN_LINES_PROPERTY + OBJECT_RELATIONSHIP_SEPARATOR_STRING
			+ PaymentInLine.PAYMENT_IN_PROPERTY + OBJECT_RELATIONSHIP_SEPARATOR_STRING + PaymentIn.STUDENT_PROPERTY
			+ OBJECT_RELATIONSHIP_SEPARATOR_STRING + Student.CONTACT_PROPERTY;

	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private ICayenneService cayenneService;

	public VoucherService() {
	}

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
	public Integer getProductCount() {
		//TODO it is a workaround for this problem
		//EJBQuery:
		//select count(p) from Product p where (p.college = college) and (p.isWebVisible = true) and (p.isOnSale = true)
		//Result SQL:
		//SELECT COUNT(*) AS sc0 FROM Product t0 WHERE t0.type = ? OR t0.type = ? OR t0.type = ? OR t0.type = ? AND t0.collegeId = ? AND t0.isWebVisible = ? AND t0.isOnSale = ? [bind: 1:1, 2:1, 3:2, 4:3, 5:10875, 6:'true', 7:'true']
		return ((Number) SQLSelect.dataRowQuery("select count(id) from Product where collegeId = #bind($collegeId) and " +
						"isOnSale = #bind($isOnSale) and isWebVisible = #bind($isWebVisible)")
				.params("collegeId", webSiteService.getCurrentCollege().getId())
				.params("isOnSale", true)
				.params("isWebVisible", true)
				.selectOne(cayenneService.sharedContext()).values().iterator().next()).intValue();
		//TODO the code should be uncommented when the cayenne EJBQLQuery handling for tables which keep inheritances entities is fixed
		//return cayenneService.sharedContext().performQuery(new EJBQLQuery("select count(p) from Product p where " + getAvailableProductsQualifier().toEJBQL("p")))
		//.get(0)).intValue();
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
		return !results.isEmpty() ? results.get(0) : null;
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
		return !results.isEmpty() ? results.get(0) : null;
	}

	private Expression getAvailableProductsQualifier() {
		return Product.COLLEGE.eq(webSiteService.getCurrentCollege())
				.andExp(Product.IS_WEB_VISIBLE.eq(Boolean.TRUE))
				.andExp(Product.IS_ON_SALE.eq(Boolean.TRUE));
	}

	@Override
	public List<Product> getAvailableProducts(Integer startDefault, Integer rowsDefault) {
		ObjectSelect<Product> query = ObjectSelect.query(Product.class, getAvailableProductsQualifier());
		if (startDefault != null && rowsDefault != null) {
			query.offset(startDefault);
			query.limit(rowsDefault);
		}
		query.addOrderBy(Product.TYPE.desc(), Product.NAME.asc(), Product.PRICE_EX_TAX.desc());
		return query.select(cayenneService.sharedContext());
	}

	@Override
	public Voucher getVoucherByCode(String code) {

        College currentCollege = takeWebSiteService().getCurrentCollege();
		Expression exp = ExpressionFactory.matchExp(Voucher.COLLEGE_PROPERTY, currentCollege);
        exp = exp.andExp(ExpressionFactory.matchExp(Voucher.CODE_PROPERTY, code));
        exp = exp.andExp(ExpressionFactory.matchExp(Voucher.STATUS_PROPERTY, ProductStatus.ACTIVE));
		exp = exp.andExp(ExpressionFactory.greaterExp(Voucher.EXPIRY_DATE_PROPERTY,new Date()).orExp(ExpressionFactory.matchExp(Voucher.EXPIRY_DATE_PROPERTY, null)));


        List<Voucher> results = cayenneService.sharedContext().performQuery(new SelectQuery(Voucher.class, exp));
		logger.info("{} found for code {} for college {}", results.size(), code, currentCollege.getId());
		if (results.size() > 1) {
			logger.warn("{} vouchers found for code {} for college {}. Maybe we need to enlarge the code size?", results.size(), code, currentCollege.getId());
		}
		return results.isEmpty() ? null : results.get(0);
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

	public Voucher createVoucher(VoucherProduct voucherProduct, Contact contact) {
		Voucher voucher = voucherProduct.getObjectContext().newObject(Voucher.class);
		voucher.setCode(SecurityUtil.generateRandomPassword(Voucher.VOUCHER_CODE_LENGTH));
		voucher.setCollege(voucherProduct.getCollege());

		voucher.setContact(contact);
        voucher.setSource(PaymentSource.SOURCE_WEB);
        voucher.setStatus(ProductStatus.NEW);
        voucher.setProduct(voucherProduct);
        voucher.setRedeemedCoursesCount(0);

        voucher.setExpiryDate(ProductUtil.calculateExpiryDate(new Date(), voucherProduct.getExpiryType(), voucherProduct.getExpiryDays()));
        voucher.setRedemptionValue(voucherProduct.getValue());
		voucher.setValueOnPurchase(voucherProduct.getValue());
		return voucher;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Product> loadByIds(List<Long> ids) {
		if (ids == null || ids.isEmpty()) {
			return Collections.emptyList();
		}
		SelectQuery q = new SelectQuery(Product.class, ExpressionFactory.inDbExp(Product.ID_PK_COLUMN, ids));
		return cayenneService.sharedContext().performQuery(q);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Product> loadByIds(Object... ids) {
		if (ids.length == 0) {
			return Collections.emptyList();
		}
		List<Object> params = Arrays.asList(ids);
		SelectQuery q = new SelectQuery(Product.class, ExpressionFactory.inDbExp(VoucherProduct.ID_PK_COLUMN, params));
		return cayenneService.sharedContext().performQuery(q);
	}

	@Override
	public List<ProductItem> loadProductItemsByIds(List<Long> ids) {
		if (ids == null || ids.isEmpty()) {
			return Collections.emptyList();
		}
		SelectQuery q = new SelectQuery(ProductItem.class, ExpressionFactory.inDbExp(ProductItem.ID_PK_COLUMN, ids));
		return cayenneService.sharedContext().performQuery(q);
	}

    @Override
    public boolean isVoucherWithoutPrice(VoucherProduct product)
    {
        return product.getRedemptionCourses().isEmpty() && product.getPriceExTax() == null;
    }
}
