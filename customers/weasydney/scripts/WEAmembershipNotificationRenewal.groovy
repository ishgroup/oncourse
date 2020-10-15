def run(args) {
    def context = args.context

        def oneWeek = new Date() + 6
        oneWeek.set(hourOfDay: 0, minute: 0, second: 0)

        def oneWeekOneDay = new Date() + 7
        oneWeekOneDay.set(hourOfDay: 0, minute: 0, second: 0)
        def b = new StringBuffer()
    
        def exp = Membership.EXPIRY_DATE.between(oneWeek, oneWeekOneDay)

        def memberships = context.select(SelectQuery.query(Membership, exp))
        println memberships

    memberships.each() { membership ->
        if (membership.contact && membership.contact.email) {
            email {
                from "support@weasydney.com.au"
                to membership.contact.email
                subject "Membership Renewal"
                content "Dear " + membership.contact?.firstName + ",\n\n" + "Just a quick reminder that your WEA Sydney membership renewal date is coming up in 7 days. To renew your membership please go to the membership page on our website www.weasydney.com.au/membership, contact our office on 9264 2781 or visit us in person at 72 Bathurst St, Sydney." + "\n\n" + "We'd like to take this opportunity to thank you for supporting WEA Sydney in this way over the past year, as a not-for-profit organisation our members help us continue to offer the best short courses in Sydney.\n\n"\
              + "Kind Regards,\n\n"\
              + "Lynda Jupp\n"\
              + "Membership Manager" 
            }
        }
    }
}