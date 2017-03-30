package ish.oncourse.willow.service.impl

import com.google.inject.Inject
import groovy.transform.CompileStatic
import ish.oncourse.willow.model.Contact
import ish.oncourse.willow.model.CreateContactParams
import ish.oncourse.willow.model.FieldErrors
import ish.oncourse.willow.model.ValidationError
import ish.oncourse.willow.service.ContactApi
import org.apache.cayenne.configuration.server.ServerRuntime
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.QueryCacheStrategy
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.ws.rs.BadRequestException
import javax.ws.rs.core.Response

@CompileStatic
class ContactApiServiceImpl implements ContactApi{

    final static  Logger logger = LoggerFactory.getLogger(ContactApiServiceImpl.class)

    private ServerRuntime cayenneRuntime
    
    @Inject
    ContactApiServiceImpl(ServerRuntime cayenneRuntime) {
        this.cayenneRuntime = cayenneRuntime
    }

    @Override
    Contact createOrGetContact(CreateContactParams createContactParams) {
        throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity(
                new ValidationError().with {
                formErrors = ['form error number one', 'form error number two']
                fieldsErrors << new FieldErrors(name: 'firstName', errors: ['error1','error2'])
                fieldsErrors << new FieldErrors(name: 'lastName', errors: ['error3','error4']) 
                    it
            })
            .status(400)
            .build())
    }

    @Override
    Contact getContact(String studentUniqueIdentifier) {
        ish.oncourse.model.Contact contact = ObjectSelect.query(ish.oncourse.model.Contact)
                .where(ish.oncourse.model.Contact.UNIQUE_CODE.eq(studentUniqueIdentifier))
                .cacheStrategy(QueryCacheStrategy.SHARED_CACHE)
                .cacheGroups(ish.oncourse.model.Contact.class.simpleName)
                .selectOne(cayenneRuntime.newContext())
        if (contact) {
            new Contact(id: contact.id.toString(),
                    firstName: contact.givenName,
                    lastName: contact.familyName,
                    email: contact.emailAddress,
                    uniqueIdentifier: studentUniqueIdentifier)
        } else {
            logger.error("There is no contact with uuid: ${studentUniqueIdentifier}")
            throw new IllegalArgumentException("There is no contact with uuid: ${studentUniqueIdentifier}")
        }

    }
}
