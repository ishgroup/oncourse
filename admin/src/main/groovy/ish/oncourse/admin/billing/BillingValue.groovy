package ish.oncourse.admin.billing

import groovy.transform.Immutable
import org.apache.commons.lang3.builder.EqualsBuilder

/**
 *
 * akoiro - 2/23/16.
 */
@Immutable
class BillingValue {
    def String code;
    def String description;
    def Long quantity;
    def BigDecimal unitPrice;
    def Date paidUntil;

    @Override
    boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj)
    }

    def BigDecimal getNet() {
        return unitPrice.multiply(quantity)
    }
}
