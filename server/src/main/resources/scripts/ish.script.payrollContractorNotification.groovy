if (PayslipPayType.CONTRACTOR == record.payType) {
    message {
        template contractorNoticeTemplate
        record record
    }
}