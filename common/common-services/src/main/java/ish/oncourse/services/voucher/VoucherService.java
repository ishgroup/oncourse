package ish.oncourse.services.voucher;

import ish.common.types.PaymentSource;
import ish.common.types.ProductStatus;
import ish.oncourse.model.*;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.util.ProductUtil;
import ish.util.SecurityUtil;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.QueryCacheStrategy;
import org.apache.cayenne.query.SQLSelect;
import org.apache.cayenne.query.SelectQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static ish.oncourse.model.auto._ProductItem.COLLEGE;
import static ish.oncourse.model.auto._ProductItem.EXPIRY_DATE;

public class VoucherService implements IVoucherService {
    private static final Logger logger = LogManager.getLogger();

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

    @Override
    public Integer getProductCount() {
        return ((Number) SQLSelect.dataRowQuery("select count(id) from Product where collegeId = #bind($collegeId) and " +
                "isOnSale = #bind($isOnSale) and isWebVisible = #bind($isWebVisible)")
                .params("collegeId", webSiteService.getCurrentCollege().getId())
                .params("isOnSale", true)
                .params("isWebVisible", true)
                .cacheStrategy(QueryCacheStrategy.LOCAL_CACHE, Product.class.getSimpleName())
                .selectOne(cayenneService.sharedContext()).values().iterator().next()).intValue();
    }

    @Override
    public Product getProductBySKU(String sku) {
        College college = webSiteService.getCurrentCollege();
        return ObjectSelect.query(Product.class,
                COLLEGE.eq(college)
                        .andExp(getAvailableProductsQualifier())
                        .andExp(Product.SKU.eq(sku)))
                .cacheStrategy(QueryCacheStrategy.LOCAL_CACHE, Product.class.getSimpleName())
                .selectFirst(cayenneService.sharedContext());

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
        query.cacheStrategy(QueryCacheStrategy.LOCAL_CACHE, Product.class.getSimpleName());
        query.orderBy(Product.TYPE.desc(), Product.NAME.asc(), Product.PRICE_EX_TAX.desc());
        return query.select(cayenneService.sharedContext());
    }

    @Override
    public Voucher getVoucherByCode(String code) {
        College college = webSiteService.getCurrentCollege();

        return ObjectSelect.query(Voucher.class,
                COLLEGE.eq(college)
                        .andExp(Voucher.CODE.eq(code))
                        .andExp(Voucher.STATUS.eq(ProductStatus.ACTIVE))
                        .andExp(EXPIRY_DATE.gt(new Date()).orExp(EXPIRY_DATE.isNull())))
                .cacheStrategy(QueryCacheStrategy.LOCAL_CACHE, Product.class.getSimpleName())
                .selectFirst(cayenneService.sharedContext());
    }

    public Voucher createVoucher(VoucherProduct voucherProduct) {
        Voucher voucher = voucherProduct.getObjectContext().newObject(Voucher.class);
        voucher.setCode(SecurityUtil.generateRandomPassword(Voucher.VOUCHER_CODE_LENGTH));
        voucher.setCollege(voucherProduct.getCollege());

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
    public List<Product> loadByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        SelectQuery q = new SelectQuery(Product.class, ExpressionFactory.inDbExp(Product.ID_PK_COLUMN, ids));
        return cayenneService.sharedContext().performQuery(q);
    }

    @Override
    public List<Product> loadByIds(Object... ids) {
        if (ids.length == 0) {
            return Collections.emptyList();
        }
        return ObjectSelect.query(Product.class, ExpressionFactory.inDbExp(VoucherProduct.ID_PK_COLUMN, ids)).select(cayenneService.sharedContext());
    }

    @Override
    public List<ProductItem> loadProductItemsByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return ObjectSelect.query(ProductItem.class, ExpressionFactory.inDbExp(Voucher.ID_PK_COLUMN, ids)).select(cayenneService.sharedContext());
    }

    @Override
    public boolean isVoucherWithoutPrice(VoucherProduct product) {
        return product.getRedemptionCourses().isEmpty() && product.getPriceExTax() == null;
    }
}
