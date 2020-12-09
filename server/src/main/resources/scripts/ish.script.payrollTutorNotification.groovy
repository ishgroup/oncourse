def payslip = record
if (PayslipPayType.EMPLOYEE == payslip.payType) {
    message {
        template tutorNoticeTemplate
        record payslip
    }
}