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

package ish.oncourse.get;

import ish.common.types.DeliveryMode;
import ish.oncourse.server.cayenne.Outcome;

public class GetDeliveryMode {

    private Outcome outcome;

    private GetDeliveryMode() {
    }

    public static GetDeliveryMode valueOf(Outcome outcome) {
        GetDeliveryMode getDeliveryMode = new GetDeliveryMode();
        getDeliveryMode.outcome = outcome;
        return getDeliveryMode;
    }

    public DeliveryMode get() {
        DeliveryMode deliveryMode = outcome.getDeliveryMode();

        if (deliveryMode == null && outcome.getEnrolment() != null) {
            deliveryMode = outcome.getEnrolment().getCourseClass().getDeliveryMode();
        }

        return deliveryMode;
    }
}
