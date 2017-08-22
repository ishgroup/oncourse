package ish.oncourse.willow.checkout.functions

import ish.oncourse.model.College
import ish.oncourse.willow.model.common.CommonError
import ish.oncourse.model.Contact
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.cayenne.query.ObjectSelect
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.ws.rs.BadRequestException
import javax.ws.rs.core.Response

class GetContact extends Get<Contact> {

    final static  Logger logger = LoggerFactory.getLogger(GetContact.class)

    GetContact(ObjectContext context, College college, String id) {
        super(context, college, id)
    }
    
    Contact get() {
        get(true)
    }

    Contact get(boolean checkStudent) {
        if (!StringUtils.trimToNull(id)) {
            logger.error("contact Id required")
            throw new BadRequestException(Response.status(400).entity(new CommonError(message: 'contactId required')).build())
        }
        Contact contact = (ObjectSelect.query(Contact).where(ExpressionFactory.matchDbExp(Contact.ID_PK_COLUMN, id)) & Contact.COLLEGE.eq(college)).selectOne(context)
        if (!contact) {
            logger.error("Contact is not exist, id:$id collegeId: $college.id")
            throw new BadRequestException(Response.status(400).entity(new CommonError(message: 'Contact is not exist')).build())
        } else if (checkStudent && !contact.student) {
            logger.error("Contact has no student related, contact:$contact, collegeId: $college.id")
            throw new BadRequestException(Response.status(400).entity(new CommonError(message: 'Contact has no student related')).build())
        }
        contact
    }
}
