def run(args) {

        def oneWeek = new Date() + 6
        oneWeek.set(hourOfDay: 0, minute: 0, second: 0)

        def oneWeekOneDay = new Date() + 7
        oneWeekOneDay.set(hourOfDay: 0, minute: 0, second: 0)

        def vouchers = ObjectSelect.query(Voucher)
                    .where(Voucher.EXPIRY_DATE.between(oneWeek, oneWeekOneDay))
                    .and(Voucher.STATUS.eq(ProductStatus.ACTIVE))
                    .select(args.context)

    vouchers.each() { voucher ->
        email {
            from "support@sydneycommunitycollege.edu.au"
            to voucher.invoiceLine?.invoice?.contact?.email
            subject "Voucher is about to Expire"
            content "Dear " + voucher.invoiceLine?.invoice?.contact?.fullName + ",\n\n" + "Your course voucher " + voucher.code + " is due to expire in 7 days. Please note that vouchers once expired cannot be reactivated. To use your voucher please enrol in any of the current classes available on our website https://www.sydneycommunitycollege.edu.au/" + "\n\n"\
              + "Kind Regards,\n\n"\
              + "College Support Team\n"\
              + "Sydney Community College"
        }
    }
}