if (PayslipPayType.EMPLOYEE == record.payType) {
    message {
        template tutorNoticeTemplate
        record record
    }
}