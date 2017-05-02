package ish.oncourse.willow.service.impl

import com.google.inject.Inject
import groovy.transform.CompileStatic
import ish.oncourse.willow.functions.CreateOrGetContact
import ish.oncourse.willow.model.web.Contact
import ish.oncourse.willow.model.web.CreateContactParams
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
    private CollegeService collegeService
    
    @Inject
    ContactApiServiceImpl(ServerRuntime cayenneRuntime, CollegeService collegeService) {
        this.cayenneRuntime = cayenneRuntime
        this.collegeService = collegeService
    }

    @Override
    String createOrGetContact(CreateContactParams createContactParams) {
        CreateOrGetContact createOrGet = new CreateOrGetContact(params:createContactParams, context: cayenneRuntime.newContext(), college: collegeService.college).perform()
        if (!createOrGet.validationError.formErrors.empty || !createOrGet.validationError.fieldsErrors.empty) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity(createOrGet.validationError).build())
        } else {
            createOrGet.contactId
        }
    }

    @Override
    Contact getContact(String studentUniqueIdentifier) {
        ish.oncourse.model.Contact contact = (ObjectSelect.query(ish.oncourse.model.Contact)
                .where(ish.oncourse.model.Contact.UNIQUE_CODE.eq(studentUniqueIdentifier)) & ish.oncourse.model.Contact.COLLEGE.eq(collegeService.college))
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
