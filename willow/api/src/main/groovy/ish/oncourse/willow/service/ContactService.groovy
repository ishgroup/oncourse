package ish.oncourse.willow.service

import com.google.inject.Inject
import ish.oncourse.willow.model.Contact
import org.apache.cayenne.configuration.server.ServerRuntime
import org.apache.cayenne.query.ObjectSelect
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.validation.constraints.NotNull
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

class ContactService {
    
    Logger logger = LoggerFactory.getLogger(this.getClass())
    
    @Inject
    ServerRuntime cayenneRuntime
    
    @GET()
    @Path('/contact/{uuid}')
    @Produces(MediaType.APPLICATION_JSON)
    Contact getContact(@NotNull @PathParam('uuid') String uuid) {
        ish.oncourse.model.Contact contact = ObjectSelect.query(ish.oncourse.model.Contact).where(ish.oncourse.model.Contact.UNIQUE_CODE.eq(uuid)).selectOne(cayenneRuntime.newContext())
        if (contact) {
            new Contact(id: contact.id, firstName: contact.givenName, lastName: contact.familyName, email: contact.emailAddress, uniqueIdentifier: uuid)
        } else {
            logger.error("There is no contact with uuid: ${uuid}")
            throw new IllegalArgumentException("There is no contact with uuid: ${uuid}")
        }
    }
}
