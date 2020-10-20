/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.handler

import ish.voucher.VoucherValidationRequest
import ish.voucher.VoucherValidationResult

interface IVoucherValidationService {
    /**
     * Requests fresh voucher data from willow and returns voucher money/enrolment value value remaining
     * wrapped into {@link VoucherValidationResult}.
     */
    VoucherValidationResult getVouchers(VoucherValidationRequest request)
}