/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.checkout;




import ish.oncourse.server.cayenne.Enrolment;
import ish.oncourse.server.cayenne.ProductItem;

import java.text.SimpleDateFormat;

/**
 * Created by anarut on 8/24/16.
 */
public class GetInvoiceLineDescription {

    private static final String DATE_FORMAT = "dd-MM-yyyy h:mm a z";

    private Enrolment enrolment;

    private ProductItem productItem;

    private GetInvoiceLineDescription() {

    }

    public static GetInvoiceLineDescription valueOf(Enrolment enrolment) {
        GetInvoiceLineDescription getInvoiceLineDescription = new GetInvoiceLineDescription();
        getInvoiceLineDescription.enrolment = enrolment;
        return getInvoiceLineDescription;
    }

    public static GetInvoiceLineDescription valueOf(ProductItem productItem) {
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
                description.append(productItem.getContact().getFullName());
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
