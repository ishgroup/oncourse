/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

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
        	if (voucher.invoiceLine?.invoice?.contact?.mobilePhone != null) {
				sms {
					to voucher.invoiceLine?.invoice?.contact
					text "We wish to remind you that your WEA Sydney Gift Voucher will expire on ${voucher.expiryDate.format("dd MMMM YYYY")}. For upcoming courses see our website."
				}
			}
        }



}
