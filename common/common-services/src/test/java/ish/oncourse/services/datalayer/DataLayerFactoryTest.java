package ish.oncourse.services.datalayer;

import ish.math.Money;
import ish.oncourse.model.*;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.util.HTMLUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.tapestry5.services.Request;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static ish.oncourse.services.datalayer.DataLayerFactory.Cart;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * User: artem
 * Date: 9/18/13
 * Time: 2:42 PM
 */
public class DataLayerFactoryTest {


	private final Builder builder = new Builder(this);
	private Request request = mock(Request.class);
	private ITagService tagService = mock(ITagService.class);
	/**
	 * Instance to test.
	 */
	private DataLayerFactory dataLayerFactory = new DataLayerFactory();

	private List<CourseClass> getClasses() {
		ArrayList<CourseClass> courseClasses = new ArrayList<>();
		courseClasses.add(builder.createCourseClass(1L));
		courseClasses.add(builder.createCourseClass(2L));
		return courseClasses;
	}

	@Before
	public void initMethod() {
		dataLayerFactory = new DataLayerFactory();
		dataLayerFactory.setRequest(request);
		dataLayerFactory.setTagService(tagService);
		dataLayerFactory.init();
	}

	@Test
	public void testCourseClass() {

		List<CourseClass> classes = getClasses();
		Cart cart = dataLayerFactory.build(classes);
		assertNotNull(cart);

		for (CourseClass courseClass : classes) {
			DataLayerFactory.Product product = getProductBy(cart.products, courseClass);
			assertNotNull(product.category.primary);
			assertEquals(courseClass.getFeeExGst(), product.price.base);
			assertEquals(courseClass.getFeeIncGst(), product.price.withTax);
			assertEquals(courseClass.getFeeGst(), product.price.tax);
		}

	}

	@Test
	public void testProducts() {

		List<Product> products = getProducts();
		Cart cart = dataLayerFactory.build(products);
		assertNotNull(cart);

		for (Product product : products) {
			DataLayerFactory.Product cproduct = getProductBy(cart.products, product);
			assertNotNull(cproduct.category.primary);
			assertEquals(product.getPriceExTax(), cproduct.price.base);
			assertEquals(product.getPriceIncTax(), cproduct.price.withTax);
			assertEquals(product.getTaxAmount(), cproduct.price.tax);
		}

	}

	@Test
	public void testEnrolments() {

		List<Enrolment> enrolments = getEnrolments();
		Cart cart = dataLayerFactory.build(enrolments);
		assertNotNull(cart);

		for (Enrolment enrolment : enrolments) {
			DataLayerFactory.Product cproduct = getProductBy(cart.products, enrolment);
			assertNotNull(cproduct.category.primary);
			assertEquals(enrolment.getInvoiceLines().get(0).getFinalPriceToPayExTax(), cproduct.price.base);
			assertEquals(enrolment.getInvoiceLines().get(0).getFinalPriceToPayIncTax(), cproduct.price.withTax);
			assertEquals(enrolment.getInvoiceLines().get(0).getTotalTax(), cproduct.price.tax);
			assertEquals(enrolment.getInvoiceLines().get(0).getDiscountTotalIncTax(), cproduct.price.voucherdiscount);
		}

	}

	@Test
	public void testProductItems() {

		List<ProductItem> productItems = getProductItems();
		Cart cart = dataLayerFactory.build(productItems);
		assertNotNull(cart);

		for (ProductItem productItem : productItems) {
			DataLayerFactory.Product cproduct = getProductBy(cart.products, productItem);
			assertNotNull(cproduct.category.primary);
			assertEquals(productItem.getInvoiceLine().getFinalPriceToPayExTax(), cproduct.price.base);
			assertEquals(productItem.getInvoiceLine().getFinalPriceToPayIncTax(), cproduct.price.withTax);
			assertEquals(productItem.getInvoiceLine().getTotalTax(), cproduct.price.tax);
			assertEquals(productItem.getInvoiceLine().getDiscountTotalIncTax(), cproduct.price.voucherdiscount);
		}

	}

	private DataLayerFactory.Product getProductBy(List<DataLayerFactory.Product> products, ProductItem productItem) {
		for (DataLayerFactory.Product cProduct : products) {
			if (cProduct.id.sku.equals(productItem.getProduct().getSku()))
				return cProduct;
		}
		throw new IllegalArgumentException();
	}

	private List<Enrolment> getEnrolments() {

		ArrayList<Enrolment> enrolments = new ArrayList<>();
		enrolments.add(builder.createEnrolment(1L));
		enrolments.add(builder.createEnrolment(2L));
		return enrolments;

	}

	private List<ProductItem> getProductItems() {

		ArrayList<ProductItem> result = new ArrayList<>();
		result.add(builder.createProductItem(1L));
		result.add(builder.createProductItem(2L));
		return result;

	}

	private List<Product> getProducts() {
		ArrayList<Product> products = new ArrayList<>();
		products.add(builder.createProduct(1L));
		products.add(builder.createProduct(2L));
		return products;
	}

	private DataLayerFactory.Product getProductBy(List<DataLayerFactory.Product> products, CourseClass courseClass) {
		for (DataLayerFactory.Product product : products) {
			if (product.id.sku.equals(courseClass.getCourse().getCode()))
				return product;
		}
		throw new IllegalArgumentException();
	}

	private DataLayerFactory.Product getProductBy(List<DataLayerFactory.Product> products, Product product) {
		for (DataLayerFactory.Product cProduct : products) {
			if (cProduct.id.sku.equals(product.getSku()))
				return cProduct;
		}
		throw new IllegalArgumentException();
	}

	private DataLayerFactory.Product getProductBy(List<DataLayerFactory.Product> products, Enrolment enrolment) {
		for (DataLayerFactory.Product cProduct : products) {
			if (cProduct.id.sku.equals(enrolment.getCourseClass().getCourse().getCode()))
				return cProduct;
		}
		throw new IllegalArgumentException();
	}

	public class Builder {
		private final DataLayerFactoryTest dataLayerFactoryTest;

		public Builder(DataLayerFactoryTest dataLayerFactoryTest) {
			this.dataLayerFactoryTest = dataLayerFactoryTest;
		}

		CourseClass createCourseClass(Long id) {
			CourseClass courseClass = Mockito.mock(CourseClass.class);

			Course course = Mockito.mock(Course.class);
			Mockito.when(courseClass.getCourse()).thenReturn(course);
			Mockito.when(courseClass.getCourse().getId()).thenReturn(id);


			String name = id.toString() + "-CC";
			Mockito.when(courseClass.getCourse().getCode()).thenReturn(id.toString() + "-CC");
			Mockito.when(courseClass.getCourse().getName()).thenReturn(id.toString() + "-CC");

			Money money = Money.valueOf(new BigDecimal(RandomUtils.nextInt(100)));
			Mockito.when(courseClass.getFeeExGst()).thenReturn(money);

			money = Money.valueOf(new BigDecimal(RandomUtils.nextInt(100)));
			Mockito.when(courseClass.getFeeIncGst()).thenReturn(money);


			money = Money.valueOf(new BigDecimal(RandomUtils.nextInt(100)));
			Mockito.when(courseClass.getFeeGst()).thenReturn(money);
			Mockito.when(HTMLUtils.getCanonicalLinkPathFor(courseClass.getCourse(), request)).thenReturn(name);
			Mockito.when(courseClass.getUniqueIdentifier()).thenReturn(name);

			ArrayList<Tag> tags = new ArrayList<Tag>();
			tags.add(createTag(id));
			Mockito.when(tagService.getTagsForEntity(Course.class.getSimpleName(),
					courseClass.getCourse().getId())).thenReturn(tags);

			return courseClass;
		}

		Enrolment createEnrolment(Long id) {
			CourseClass courseClass = createCourseClass(id);

            Contact contact = mock(Contact.class);
            when(contact.getId()).thenReturn(1L);
            Student student = mock(Student.class);
            when(student.getContact()).thenReturn(contact);

			Enrolment enrolment = Mockito.mock(Enrolment.class);
            when(enrolment.getStudent()).thenReturn(student);
			InvoiceLine invoiceLine = Mockito.mock(InvoiceLine.class);
			Money money = Money.valueOf(new BigDecimal(RandomUtils.nextInt(100)));
			Mockito.when(invoiceLine.getFinalPriceToPayExTax()).thenReturn(money);
			money = Money.valueOf(new BigDecimal(RandomUtils.nextInt(100)));
			Mockito.when(invoiceLine.getFinalPriceToPayIncTax()).thenReturn(money);
			money = Money.valueOf(new BigDecimal(RandomUtils.nextInt(100)));
			Mockito.when(invoiceLine.getTotalTax()).thenReturn(money);

			money = Money.valueOf(new BigDecimal(RandomUtils.nextInt(100)));
			Mockito.when(invoiceLine.getDiscountTotalIncTax()).thenReturn(money);

			ArrayList<InvoiceLine> lines = new ArrayList<InvoiceLine>();
			lines.add(invoiceLine);
			Mockito.when(enrolment.getInvoiceLines()).thenReturn(lines);
			Mockito.when(enrolment.getCourseClass()).thenReturn(courseClass);
			return enrolment;

		}

		ProductItem createProductItem(Long id) {
			Product product = createProduct(id);

            Contact contact = mock(Contact.class);
            when(contact.getId()).thenReturn(1L);
            Student student = mock(Student.class);
            when(student.getContact()).thenReturn(contact);


            ProductItem productItem = Mockito.mock(ProductItem.class);
            when(productItem.getContact()).thenReturn(contact);
			InvoiceLine invoiceLine = Mockito.mock(InvoiceLine.class);
			Money money = Money.valueOf(new BigDecimal(RandomUtils.nextInt(100)));
			Mockito.when(invoiceLine.getFinalPriceToPayExTax()).thenReturn(money);
			money = Money.valueOf(new BigDecimal(RandomUtils.nextInt(100)));
			Mockito.when(invoiceLine.getFinalPriceToPayIncTax()).thenReturn(money);
			money = Money.valueOf(new BigDecimal(RandomUtils.nextInt(100)));
			Mockito.when(invoiceLine.getTotalTax()).thenReturn(money);

			money = Money.valueOf(new BigDecimal(RandomUtils.nextInt(100)));
			Mockito.when(invoiceLine.getDiscountTotalIncTax()).thenReturn(money);

			Mockito.when(productItem.getInvoiceLine()).thenReturn(invoiceLine);
			Mockito.when(productItem.getProduct()).thenReturn(product);
			return productItem;

		}


		Product createProduct(Long id) {
			Product product = Mockito.mock(Product.class);

			Mockito.when(product.getId()).thenReturn(id);


			String name = id.toString() + "-P";
			Mockito.when(product.getSku()).thenReturn(id.toString() + "-P");
			Mockito.when(product.getName()).thenReturn(id.toString() + "-P");

			Money money = Money.valueOf(new BigDecimal(RandomUtils.nextInt(100)));
			Mockito.when(product.getPriceExTax()).thenReturn(money);

			money = Money.valueOf(new BigDecimal(RandomUtils.nextInt(100)));
			Mockito.when(product.getPriceIncTax()).thenReturn(money);


			money = Money.valueOf(new BigDecimal(RandomUtils.nextInt(100)));
			Mockito.when(product.getTaxAmount()).thenReturn(money);
			Mockito.when(HTMLUtils.getCanonicalLinkPathFor(product, request)).thenReturn(name);
			return product;
		}

		private Tag createTag(Long id) {
			Tag tag = mock(Tag.class);
			String name = "tag" + id.toString();
			when(tag.getName()).thenReturn(name);
			when(tag.getId()).thenReturn(id);
			return tag;
		}


	}
}
