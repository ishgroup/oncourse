package ish.oncourse.willow.functions.concession

import groovy.transform.CompileStatic
import ish.oncourse.model.College
import ish.oncourse.willow.model.checkout.concession.ConcessionType
import org.apache.cayenne.ObjectContext

@CompileStatic
class GetConcessionTypes {
    
    College college
    ObjectContext context

    List<ConcessionType> get() {
        college.activeConcessionTypes.collect { persistentConcession ->
            new ConcessionType().with { c ->
                c.id = persistentConcession.id.toString()
                c.name = persistentConcession.name
                c.hasExpireDate = persistentConcession.hasExpiryDate
                c.hasNumber = persistentConcession.hasConcessionNumber
                c
            }
        }
    }
    
}
