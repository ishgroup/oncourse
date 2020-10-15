/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

String MOBILE_PREFIX = '04'

def iterator = ObjectSelect.query(Contact)
        .where(Contact.MOBILE_PHONE.isNull().orExp(Contact.MOBILE_PHONE.eq('')))
        .and(Contact.HOME_PHONE.startsWith(MOBILE_PREFIX))
        .batchIterator(args.context, 100)

iterator.each { batch ->
    batch.each {c ->
        c.mobilePhone = c.homePhone
    }
    args.context.commitChanges()
}

iterator.close()

iterator = ObjectSelect.query(Contact)
        .where(Contact.MOBILE_PHONE.isNull().orExp(Contact.MOBILE_PHONE.eq('')))
        .and(Contact.WORK_PHONE.startsWith(MOBILE_PREFIX))
        .batchIterator(args.context, 100)

iterator.each { batch ->
    batch.each {c ->
        c.mobilePhone = c.workPhone
    }
    args.context.commitChanges()
}

iterator.close()

iterator = ObjectSelect.query(Contact)
        .where(Contact.MOBILE_PHONE.isNull().orExp(Contact.MOBILE_PHONE.eq('')))
        .and(Contact.FAX.startsWith(MOBILE_PREFIX))
        .batchIterator(args.context, 100)

iterator.each { batch ->
    batch.each {c ->
        c.mobilePhone = c.fax
    }
    args.context.commitChanges()
}
iterator.close()

