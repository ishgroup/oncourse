if (record.customFields.find {it.customFieldType.key == "serviceNswVoucher"}?.value != null) {
    if (record.customFields.find {it.customFieldType.key == "serviceNswRedeemedOn"}?.value == null) {
        service_nsw {
            action "validate"
            voucher record
        }
    }
}