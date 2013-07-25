package ish.oncourse.services.datalayer;

import ish.math.Money;
import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Tag;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.util.HTMLUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.services.Request;

import java.util.ArrayList;
import java.util.List;

/**
 * The class creates java value objects(Cart,Product,Price,Category) for data layer of the tag mananager functionality.
 * then the value objects will be converted to correspondent java script objects.
 */
public class ShoppingCartDataBuilder {

	private Request request;
	private ITagService tagService;
	private List<CourseClass> courseClasses;
	private Cart cart;

	public List<CourseClass> getCourseClasses() {
		return courseClasses;
	}

	public void setCourseClasses(List<CourseClass> courseClasses) {
		this.courseClasses = courseClasses;
	}

	private Tag getTagBy(CourseClass courseClass) {
		List<Tag> tagsForEntity = tagService.getTagsForEntity(Course.class.getSimpleName(),
				courseClass.getCourse().getId());
		if (tagsForEntity.size() > 0)
			return tagsForEntity.get(0);
		else
			return null;
	}

	private String getCategoryBy(Tag tag) {
		if (tag == null)
			return StringUtils.EMPTY;
		else
			return tag.getName();
	}

	public void build() {
		/**
		 * The field should be set before using this instance.
		 */
		assert request != null;
		assert tagService != null;
		assert courseClasses != null;


		cart = new Cart();
		for (CourseClass courseClass : courseClasses) {
			Product product = getProduct(courseClass);
			addProduct(product);
		}
	}

	private void addProduct(Product product) {
		cart.price.tax = cart.price.tax.add(product.price.tax);
		cart.price.base = cart.price.base.add(product.price.base);
		cart.price.withTax = cart.price.withTax.add(product.price.withTax);
		cart.price.total = cart.price.total.add(product.price.total);
		cart.products.add(product);
	}

	private Product getProduct(CourseClass courseClass) {
		Product product = new Product();
		product.id = getProductId(courseClass);
		product.category = getCategory(courseClass);
		product.quantity = 1;
		product.price = getPrice(courseClass);
		return product;
	}

	private Price getPrice(CourseClass courseClass) {
		Price price = new Price();
		price.base = courseClass.getFeeExGst();
		price.withTax = courseClass.getFeeIncGst();
		price.tax = courseClass.getFeeGst();
		price.total = price.withTax;
		return price;
	}

	private ProductCategory getCategory(CourseClass courseClass) {
		Tag tag = getTagBy(courseClass);
		ProductCategory category = new ProductCategory();
		category.type = CategoryType.CLASS.name().toLowerCase();
		category.primary = getCategoryBy(tag);
		return category;
	}

	private ProductId getProductId(CourseClass courseClass) {
		ProductId productId = new ProductId();
		productId.id = courseClass.getCourse().getCode();
		productId.name = courseClass.getCourse().getName();
		productId.url = HTMLUtils.getCanonicalLinkPathFor(courseClass.getCourse(), request);
		productId.sku = courseClass.getUniqueIdentifier();
		return productId;
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

	public Cart getCart() {
		return cart;
	}

	/**
	 * Value object for java script object Cart
	 */
	public static class Cart {
		public Price price = new Price();
		public List<Product> products = new ArrayList<>();
	}

	/**
	 * Value object for java script object Price
	 */
	public static class Price {
		public String vouchercode = StringUtils.EMPTY;
		public String voucherdiscount = StringUtils.EMPTY;
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
		public ProductCategory category = new ProductCategory();
		public Integer quantity = 0;
		public Price price = new Price();
		public List<Product> linkedProducts = new ArrayList<>();
		public List<Product> attributes = new ArrayList<>();
	}


	/**
	 * Enum predefined values for ProductCategory.type field
	 */
	public static enum CategoryType
	{
		CLASS
	}
}
