package ish.oncourse.solr.functions.course

import ish.oncourse.model.Contact
import ish.oncourse.solr.model.SContact

import static org.apache.commons.lang3.StringUtils.EMPTY

/**
 * User: akoiro
 * Date: 4/11/17
 */
class ContactFunctions {

    static SContact getSContact(Contact contact) {
        return new SContact().with {
            it.id = contact.id
            it.tutorId = contact.tutor?.angelId
            it.name = "${contact.givenName ? contact.givenName: EMPTY} ${contact.familyName ? contact.familyName : EMPTY}".trim()
            it
        }
    }
}
