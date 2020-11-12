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

/**
 * Created by anarut on 8/24/16.
 */
public class GetInvoiceLineTitle {

    private Enrolment enrolment;

    private ProductItem productItem;

    private GetInvoiceLineTitle() {

    }

    public static GetInvoiceLineTitle valueOf(Enrolment enrolment) {
        GetInvoiceLineTitle getInvoiceLineTitle = new GetInvoiceLineTitle();
        getInvoiceLineTitle.enrolment = enrolment;
        return getInvoiceLineTitle;
    }

    public static GetInvoiceLineTitle valueOf(ProductItem productItem) {
        GetInvoiceLineTitle getInvoiceLineTitle = new GetInvoiceLineTitle();
        getInvoiceLineTitle.productItem = productItem;
        return getInvoiceLineTitle;
    }

    public String get() {
        StringBuilder title = new StringBuilder();

        if (enrolment != null) {
            title.append(enrolment.getStudent().getContact().getName(true));
            title.append(" enrolled in ");
            title.append(enrolment.getCourseClass().getUniqueCode());
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
