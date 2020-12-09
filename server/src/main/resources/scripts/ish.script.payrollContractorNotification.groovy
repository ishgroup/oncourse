def payslip = record
if (PayslipPayType.CONTRACTOR == payslip.payType) {
    message {
        template contractorNoticeTemplate
        record payslip
    }
}