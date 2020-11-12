/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.event


import ish.math.Money

class VoucherData implements Serializable {
    private Long id
    private Money redemptionValue
    private Integer enrolmentsUsed

    VoucherData() {
    }

    void setId(Long id) {
        this.id = id
    }

    void setRedemptionValue(Money redemptionValue) {
        this.redemptionValue = redemptionValue
    }

    void setEnrolmentsUsed(Integer enrolmentsUsed) {
        this.enrolmentsUsed = enrolmentsUsed
    }

    Long getId() {
        return this.id
    }

    Money getRedemptionValue() {
        return this.redemptionValue
    }

    Integer getEnrolmentsUsed() {
        return this.enrolmentsUsed
    }
}
