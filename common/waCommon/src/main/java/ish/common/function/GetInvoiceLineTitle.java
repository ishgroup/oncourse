package ish.common.function;

import ish.common.payable.EnrolmentInterface;
import ish.oncourse.cayenne.ProductItemInterface;

/**
 * Created by anarut on 9/22/16.
 */
public class GetInvoiceLineTitle {

	private EnrolmentInterface enrolment;

	private ProductItemInterface productItem;

	private GetInvoiceLineTitle() {

	}

	public static GetInvoiceLineTitle valueOf(EnrolmentInterface enrolment) {
		GetInvoiceLineTitle getInvoiceLineTitle = new GetInvoiceLineTitle();
		getInvoiceLineTitle.enrolment = enrolment;
		return getInvoiceLineTitle;
	}

	public static GetInvoiceLineTitle valueOf(ProductItemInterface productItem) {
		GetInvoiceLineTitle getInvoiceLineTitle = new GetInvoiceLineTitle();
		getInvoiceLineTitle.productItem = productItem;
		return getInvoiceLineTitle;
	}

	public String get() {
		StringBuilder title = new StringBuilder();

		if (enrolment != null) {
			title.append(enrolment.getStudent().getContact().getName(true));
			title.append(" enrolled in ");
			title.append(enrolment.getCourseClass().getCourse().getCode());
			title.append("-");
			title.append(enrolment.getCourseClass().getCode());
		} else if (productItem != null) {
			if (productItem.getContact() != null) {
				title.append(productItem.getContact().getName(true));
				title.append(" ");
			}
			title.append(productItem.getProduct().getName());
		}

		return title.toString();
	}
}

