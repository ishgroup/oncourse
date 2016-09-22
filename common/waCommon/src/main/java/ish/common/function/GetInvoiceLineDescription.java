package ish.common.function;

import ish.common.payable.EnrolmentInterface;
import ish.oncourse.cayenne.ProductItemInterface;

import java.text.SimpleDateFormat;

public class GetInvoiceLineDescription {

	private static final String DATE_FORMAT = "dd-mm-yyyy h:mm a z";

	private EnrolmentInterface enrolment;

	private ProductItemInterface productItem;

	private GetInvoiceLineDescription() {

	}

	public static GetInvoiceLineDescription valueOf(EnrolmentInterface enrolment) {
		GetInvoiceLineDescription getInvoiceLineDescription = new GetInvoiceLineDescription();
		getInvoiceLineDescription.enrolment = enrolment;
		return getInvoiceLineDescription;
	}

	public static GetInvoiceLineDescription valueOf(ProductItemInterface productItem) {
		GetInvoiceLineDescription getInvoiceLineDescription = new GetInvoiceLineDescription();
		getInvoiceLineDescription.productItem = productItem;
		return getInvoiceLineDescription;
	}

	public String get() {
		StringBuilder description = new StringBuilder();

		if (enrolment != null) {
			description.append(enrolment.getCourseClass().getCourse().getName());
			if (enrolment.getCourseClass().getStartDateTime() != null) {
				description.append(" starting on ");

				SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
				format.setTimeZone(enrolment.getCourseClass().getTimeZone());
				description.append(format.format(enrolment.getCourseClass().getStartDateTime()));
			}
		} else if (productItem != null) {
			if (productItem.getContact() != null) {
				description.append(productItem.getContact().getName(true));
				description.append(" (");
			}
			description.append(productItem.getProduct().getSku());
			description.append(" ");
			description.append(productItem.getProduct().getName());
			if (productItem.getContact() != null) {
				description.append(")");
			}

		}

		return description.toString();
	}


}