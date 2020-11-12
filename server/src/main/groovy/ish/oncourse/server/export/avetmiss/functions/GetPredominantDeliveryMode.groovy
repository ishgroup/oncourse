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

package ish.oncourse.server.export.avetmiss.functions

import ish.common.types.DeliveryMode
import static ish.common.types.DeliveryMode.*

/**
 * DEFINITION - Predominant delivery mode identifies which of the modes available in combination in the Delivery
 * mode identifier field is the largest or only component of delivery for subject activity.
 *
 * CONTEXT - Predominant delivery mode provides additional information for analysing delivery modes reported
 * in the Delivery mode identifier data element.
 *
 * Introduced 01 January 2018 in AVETMISS Release 8.0
 *
 * Format attributes
 *  Length: 1
 *  Type: alphanumeric
 *  Justification: none
 *  Fill character: none
 *  Permitted data element value: not applicable
 */
class GetPredominantDeliveryMode {

    /**
     * Predominant delivery mode ‘I – Internal delivery’ indicates that internal delivery is specified as
     * one of the values in the Delivery mode identifier data element and is the largest or only component.
     */
    private static final String INTERNAL_DELIVERY = "I"

    /**
     * Predominant delivery mode ‘E – External delivery’ indicates that external delivery is specified as
     * one of the values in the Delivery mode identifier data element and is the largest or only component.
     */
    private static final String EXTERNAL_DELIVERY = "E"

    /**
     * Predominant delivery mode ‘W – Workplace-based delivery’ indicates that workplace-based delivery is
     * specified as one of the values in the Delivery mode identifier data element and is the largest or only component.
     */
    private static final String WORKPLACE_BASED_DELIVERY = "W"

    /**
     * Predominant delivery mode ‘N – Not applicable’ indicates recognition of prior learning or credit
     * transfer and reported as ‘NNN’ in the Delivery mode identifier data element.
     */
    private static final String NOT_APPLICABLE = "N"

    private DeliveryMode deliveryMode

    private GetPredominantDeliveryMode() {

    }

    static GetPredominantDeliveryMode valueOf(DeliveryMode deliveryMode) {
        def function = new GetPredominantDeliveryMode()
        function.deliveryMode = deliveryMode
        return function
    }

    String get() {
        if (deliveryMode == null) {
            return INTERNAL_DELIVERY
        }

        switch (deliveryMode) {
            case WORKPLACE:
                return WORKPLACE_BASED_DELIVERY
            case ONLINE:
            case ONLINE_AND_WORKSPACE:
                return EXTERNAL_DELIVERY
            case NA:
            case OTHER:
            case NOT_SET:
                return NOT_APPLICABLE
            case CLASSROOM:
            case CLASSROOM_AND_ONLINE:
            case CLASSROOM_AND_WORKSPACE:
            case CLASSROOM_ONLINE_AND_WORKSPACE:
                return INTERNAL_DELIVERY
            default:
                return INTERNAL_DELIVERY
        }
    }
}
