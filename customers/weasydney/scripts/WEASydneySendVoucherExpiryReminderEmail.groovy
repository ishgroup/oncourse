def run(args) {

        def oneWeek = new Date() + 14
        oneWeek.set(hourOfDay: 0, minute: 0, second: 0)

        def oneWeekOneDay = new Date() + 15
        oneWeekOneDay.set(hourOfDay: 0, minute: 0, second: 0)

        def vouchers = ObjectSelect.query(Voucher)
                    .where(Voucher.EXPIRY_DATE.between(oneWeek, oneWeekOneDay))
                    .and(Voucher.STATUS.eq(ProductStatus.ACTIVE))
                    .select(args.context)

    vouchers.each { voucher ->
        email {
            to voucher.invoiceLine?.invoice?.contact
            from "info@weasydney.edu.au"
            template "WEA Sydney Voucher Expiry Reminder"
            bindings voucher:voucher
        }
    }

}