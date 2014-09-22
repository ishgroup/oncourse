package ish.oncourse.services.datalayer;

import ish.math.Money;
import ish.oncourse.services.tag.ITagService;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.PostInjection;
import org.apache.tapestry5.services.Request;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The class creates java value objects(Cart,Product,Price,Category) for data layer of the tag mananager functionality.
 * then the value objects will be converted to correspondent java script objects.
 */
public class DataLayerFactory implements IDataLayerFactory {

	@Inject
	private Request request;
	@Inject
	private ITagService tagService;

	private ProductFactory productFactory;


	@PostInjection
	public void init() {
		productFactory = new ProductFactory();
		productFactory.setTagService(tagService);
		productFactory.setRequest(request);
	}

	public Cart build(List values) {
		Cart cart = new Cart();
		addProducts(values, cart);
		return cart;
	}

	private <V> void addProducts(List<V> values, Cart cart) {
		for (V value : values)
			cart.addProduct(productFactory.build(value));
	}

	public Request getRequest() {
		return request;
	}

	public void setRequest(Request request) {
		this.request = request;
	}

	public ITagService getTagService() {
		return tagService;
	}

	public void setTagService(ITagService tagService) {
		this.tagService = tagService;
	}

	public ProductFactory getProductFactory() {
		return productFactory;
	}

	/**
	 * Enum predefined values for ProductCategory.type field
	 */
	public static enum CategoryType {
		CLASS,
		PRODUCT,
	}

	/**
	 * Value object for java script object Cart
	 */
	public static class Cart {
		public String id = RandomStringUtils.random(10, true, true);
		public String storeName = StringUtils.EMPTY;
		public Date date = new Date();
		public Price price = new Price();
		public List<Product> products = new ArrayList<>();

		private void addProduct(Product product) {
			price.tax = price.tax.add(product.price.tax);
			price.base = price.base.add(product.price.base);
			price.withTax = price.withTax.add(product.price.withTax);
			price.total = price.total.add(product.price.total);
			price.voucherdiscount = price.voucherdiscount.add(product.price.voucherdiscount);
			products.add(product);
		}
	}

	/**
	 * Value object for java script object Price
	 */
	public static class Price {
		public String vouchercode = StringUtils.EMPTY;
		public Money voucherdiscount = Money.ZERO;
		public String currency = "AUD";
		public Money base = Money.ZERO;
		public Money withTax = Money.ZERO;
		public Money tax = Money.ZERO;
		public Money total = Money.ZERO;
	}

	/**
	 * Value object for java script object ProductId
	 */
	public static class ProductId {
		public String id = StringUtils.EMPTY;
		public String name = StringUtils.EMPTY;
		public String url = StringUtils.EMPTY;
		public String sku = StringUtils.EMPTY;
	}

	/**
	 * Value object for java script object ProductCategory
	 */
	public static class ProductCategory {
		public String primary = StringUtils.EMPTY;
		public String type = StringUtils.EMPTY;
	}

	/**
	 * Value object for java script object Product
	 */
	public static class Product {
		public ProductId id = new ProductId();
        public String userId = StringUtils.EMPTY;
		public ProductCategory category = new ProductCategory();
		public Integer quantity = 0;
		public Price price = new Price();
		public List<Product> linkedProducts = new ArrayList<>();
		public List<Product> attributes = new ArrayList<>();
	}
}
