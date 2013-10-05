package ish.oncourse.services.datalayer;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.ProductItem;
import ish.oncourse.services.tag.ITagService;
import org.apache.tapestry5.services.Request;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class ProductFactory {

	private Map<Class,Class<? extends AbstractProductConverter>> converterClasses = new HashMap<>();
	{
		converterClasses.put(Enrolment.class, Enrolment2ProductConverter.class);
		converterClasses.put(CourseClass.class, CourseClass2ProductConverter.class);
		converterClasses.put(ProductItem.class, ProductItem2ProductConverter.class);
		converterClasses.put(ish.oncourse.model.Product.class, Product2ProductConverter.class);
	}

	private Request request;
	private ITagService tagService;


	private Class<? extends AbstractProductConverter> get(Class valueClass)
	{
		Set<Class> keys = converterClasses.keySet();
		for (Class next : keys) {
			if (next.isAssignableFrom(valueClass))
				return converterClasses.get(next);
		}
		throw new IllegalArgumentException();
	}


	public <V> DataLayerFactory.Product build(V value) {
		AbstractProductConverter<V> converter = getConverterBy(value);
		converter.convert();
		return converter.getProduct();
	}

	public <V> AbstractProductConverter<V> getConverterBy(V value) {
		Class aClass = get(value.getClass());
		try {
			AbstractProductConverter<V> converter = (AbstractProductConverter<V>) aClass.newInstance();
			converter.setProductFactory(this);
			converter.setValue(value);
			return converter;
		} catch (Throwable e) {
			throw new IllegalArgumentException(e);
		}
	}

	DataLayerFactory.Price getPriceBy(InvoiceLine invoiceLine)
	{
		DataLayerFactory.Price price = new DataLayerFactory.Price();
		price.base = invoiceLine.getFinalPriceToPayExTax();
		price.withTax = invoiceLine.getFinalPriceToPayIncTax();
		price.tax = invoiceLine.getTotalTax();
		price.voucherdiscount = invoiceLine.getDiscountTotalIncTax();
		price.total = price.withTax;
		return price;
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
}
