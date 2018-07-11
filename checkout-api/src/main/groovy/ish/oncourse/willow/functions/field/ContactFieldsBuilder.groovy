package ish.oncourse.willow.functions.field

import ish.oncourse.model.Contact
import ish.oncourse.model.Field
import ish.oncourse.model.WebSite
import ish.oncourse.willow.model.field.ContactFields

class ContactFieldsBuilder {

    private boolean mandatoryOnly
    private Contact contact
    private WebSite webSite
    private Set<Field> fields

    ContactFieldsBuilder(boolean mandatoryOnly, Contact contact, WebSite webSite, Set<Field> fields) {
        this.mandatoryOnly = mandatoryOnly
        this.contact = contact
        this.webSite = webSite
        this.fields = fields
    }

    ContactFields build() {
        ContactFields contactFields = new ContactFields()
        contactFields.contactId = contact.id.toString()
        contactFields.headings = FieldHelper.valueOf(mandatoryOnly, contact, webSite, fields).buildFieldHeadings()
        return contactFields
    }
}
