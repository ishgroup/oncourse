if (record.serviceNswVoucher && !record.serviceNswRedeemedOn) {
    service_nsw {
        action "validate"
        voucher record
    }
}