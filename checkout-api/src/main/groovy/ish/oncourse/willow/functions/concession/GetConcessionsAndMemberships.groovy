package ish.oncourse.willow.functions.concession

import ish.common.types.ProductStatus
import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.util.FormatUtils
import ish.oncourse.willow.checkout.functions.GetContact
import ish.oncourse.willow.model.checkout.ConcessionsAndMemberships
import ish.oncourse.willow.model.checkout.StudentMembership
import ish.oncourse.willow.model.checkout.concession.Concession
import org.apache.cayenne.ObjectContext

import java.text.SimpleDateFormat

class GetConcessionsAndMemberships {

    ObjectContext context
    College college
    List<String> contactIds
    
    ConcessionsAndMemberships get() {
        ConcessionsAndMemberships concessionsAndMemberships = new ConcessionsAndMemberships()
        Date now = new Date()
        
        contactIds.each { id ->
            Contact contact = new GetContact(context, college, id).get(false)
            
            if (!contact.isCompany && contact.student) {
                contact.student.studentConcessions.each { sc ->
                    concessionsAndMemberships.concessions << new Concession().with { c ->
                        c.contactId = id
                        c.name = sc.concessionType.name
                        c.concessionTypeId = sc.concessionType.id.toString()
                        if (sc.expiresOn) {
                            c.expiryDate = new SimpleDateFormat(FormatUtils.DATE_FIELD_PARSE_FORMAT).format(sc.expiresOn)
                        }
                        c.number =  sc.concessionNumber
                        c
                    }
                }
            }
            
            contact.memberships.findAll { it.status == ProductStatus.ACTIVE && (it.expiryDate == null || it.expiryDate.after(now))}
                    .collect {it.product}
                    .unique()
                    .each { p ->
                concessionsAndMemberships.memberships << new StudentMembership().with { m ->
                    m.contactId = id
                    m.name = p.name
                    m
                }
            }
        }
        concessionsAndMemberships
    }
}
