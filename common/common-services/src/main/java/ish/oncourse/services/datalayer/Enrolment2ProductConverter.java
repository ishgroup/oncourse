package ish.oncourse.services.datalayer;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;

import static ish.oncourse.services.datalayer.DataLayerFactory.*;

public class Enrolment2ProductConverter extends AbstractProductConverter<Enrolment> {

	private AbstractProductConverter<CourseClass> courseClassConverter;

	@Override
	public void convert() {
		courseClassConverter = getProductFactory().getConverterBy(getValue().getCourseClass());
		super.convert();
        getProduct().userId = getValue().getStudent().getContact().getId().toString();
	}

	@Override
	protected ProductId getProductId() {

		return courseClassConverter.getProductId();
	}

	@Override
	protected ProductCategory getCategory() {
		return courseClassConverter.getCategory();
	}

	@Override
	protected Price getPrice() {
		return getProductFactory().getPriceBy(getValue().getInvoiceLines().get(0));
	}
}