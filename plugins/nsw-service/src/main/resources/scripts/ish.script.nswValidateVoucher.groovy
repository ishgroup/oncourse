/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

if (record.customFields.find {it.customFieldType.key == "serviceNswVoucher"}?.value != null) {
    if (record.customFields.find {it.customFieldType.key == "serviceNswRedeemedOn"}?.value == null) {
        nsw {
            action "validate"
            voucher record
        }
    }
}