package ish.oncourse.admin.billing.get

import ish.oncourse.model.College
import org.apache.cayenne.ObjectContext

/**
 *
 * akoiro - 2/24/16.
 */
class BillingContext {
    def ObjectContext context
    def College college
    def Date from
    def Date to
}
