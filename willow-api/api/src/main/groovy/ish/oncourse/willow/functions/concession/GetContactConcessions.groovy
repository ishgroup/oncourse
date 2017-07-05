package ish.oncourse.willow.functions.concession

import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.StudentConcession
import ish.oncourse.willow.checkout.functions.GetContact
import ish.oncourse.willow.model.checkout.concession.Concession
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect

import java.time.ZoneOffset

class GetContactConcessions {

    ObjectContext context
    College college
    List<String> contactIds
    
    List<Concession> get() {
        List<Concession> list = []
        
        contactIds.each { id ->
            Contact contact = new GetContact(context, college, id).get(false)
            
            if (!contact.isCompany && contact.student) {
                list += contact.student.studentConcessions.collect { sc ->
                    new Concession().with { c ->
                        c.contactId = id
                        c.name = sc.concessionType.name
                        c.concessionTypeId = sc.concessionType.id.toString()
                        c.expiryDate =  sc.expiresOn?.toInstant()?.atZone(ZoneOffset.UTC)?.toLocalDateTime()
                        c.number =  sc.concessionNumber
                        c
                    }
                }
            }
        }
        list
    }
}
