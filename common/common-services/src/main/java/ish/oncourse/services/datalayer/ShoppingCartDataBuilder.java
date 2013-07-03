package ish.oncourse.services.datalayer;

import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Tag;
import ish.oncourse.services.html.IPlainTextExtractor;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.util.HTMLUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.services.Request;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCartDataBuilder {

	private Request request;
	private ITagService tagService;
	private IPlainTextExtractor plainTextExtractor;
	private ITextileConverter textileConverter;
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
		assert request != null;
		assert tagService != null;
		assert plainTextExtractor != null;
		assert textileConverter != null;
		assert courseClasses != null;


		cart = new Cart();
		cart.id = "CART_ID";
		for (CourseClass courseClass : courseClasses) {
			Product product = getProduct(courseClass);
			addProduct(product);
		}
	}

	private void addProduct(Product product) {
		cart.price.tax += product.price.tax;
		cart.price.base += product.price.base;
		cart.price.withTax += product.price.withTax;
		cart.price.total += product.price.total;
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
		price.base = courseClass.getFeeExGst().doubleValue();
		price.withTax = courseClass.getFeeIncGst().doubleValue();
		price.tax = courseClass.getFeeGst().doubleValue();
		price.total = price.withTax;
		return price;
	}

	private ProductCategory getCategory(CourseClass courseClass) {
		Tag tag = getTagBy(courseClass);
		ProductCategory category = new ProductCategory();
		category.type = CourseClass.class.getSimpleName();
		category.primary = getCategoryBy(tag);
		return category;
	}

	private ProductId getProductId(CourseClass courseClass) {
		ProductId productId = new ProductId();
		productId.id = courseClass.getCourse().getCode();
		productId.name = courseClass.getCourse().getName();
		productId.description = HTMLUtils.cutDescription(textileConverter, plainTextExtractor, courseClass.getCourse().getDetail(), 150);
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

	public IPlainTextExtractor getPlainTextExtractor() {
		return plainTextExtractor;
	}

	public void setPlainTextExtractor(IPlainTextExtractor plainTextExtractor) {
		this.plainTextExtractor = plainTextExtractor;
	}

	public ITextileConverter getTextileConverter() {
		return textileConverter;
	}

	public void setTextileConverter(ITextileConverter textileConverter) {
		this.textileConverter = textileConverter;
	}

	public static class Cart {
		public String id = StringUtils.EMPTY;
		public Price price = new Price();
		public List<Product> products = new ArrayList<>();
	}

	public static class Price {
		public String vouchercode = StringUtils.EMPTY;
		public String voucherdiscount = StringUtils.EMPTY;
		public String currency = "AUD";
		public Double base = 0.0;
		public Double withTax = 0.0;
		public Double tax = 0.0;
		public Double total = 0.0;
	}

	public static class ProductId {
		public String id = StringUtils.EMPTY;
		public String name = StringUtils.EMPTY;
		public String description = StringUtils.EMPTY;
		public String url = StringUtils.EMPTY;
		public String sku = StringUtils.EMPTY;
	}

	public static class ProductCategory {
		public String primary = StringUtils.EMPTY;
		public String type = StringUtils.EMPTY;
	}

	public static class Product {
		public ProductId id = new ProductId();
		public ProductCategory category = new ProductCategory();
		public Integer quantity = 0;
		public Price price = new Price();
		public List<Product> linkedProducts = new ArrayList<>();
		public List<Product> attributes = new ArrayList<>();
	}
}
