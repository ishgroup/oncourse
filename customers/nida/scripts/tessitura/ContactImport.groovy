logger = LogManager.getLogger()

/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

enum AddressType {
    Unknown(1),
    BusinessAddress(2),
    HomeAddress(3),
    POBoxAddress(4),
    CorporateOrFoundation(5),
    SecondaryAddress(6)

    private final int value

    AddressType(int value) {
        this.value = value
    }

    public int value() {
        return value
    }
}

enum PhoneType {
    Landline("1"),
    Fax("3"),
    Mobile("15")

    private final String value

    PhoneType(String value) {
        this.value = value
    }

    public String value() {
        return value
    }
}

enum ConstituentType {
    Individual(1),
    Corporation(2)

    private final int value

    ConstituentType(int value) {
        this.value = value
    }

    public int value() {
        return value
    }
}

class ImportException extends RuntimeException {
    public ImportException() {
        super()
    }

    public ImportException(String error) {
        super(error)
    }
}


LAST_RUN_PREFERENCE = "nida.tessitura.import.lastRun"

TESSITURA_BASE_URL = "https://nidaapp.tessituranetworkramp.com/LiveAPI/TessituraService/"
COUNTIES_PATH = "ReferenceData/Countries/Summary"
STATES_PATH = "ReferenceData/States/Summary"
PREFIXES_PATH = "ReferenceData/Prefixes/Summary"

CREATE_CONTACT_PATH = "CRM/Constituents/Snapshot/"
CREATE_ATTRIBUTE_PATH = "CRM/Attributes/"
SEARCH_CONTACT_PATH = "CRM/Constituents/Search"
def lastRun = args.context.selectOne(SelectQuery.query(Preference, Preference.NAME.eq(LAST_RUN_PREFERENCE)))

if (lastRun == null) {
    lastRun = args.context.newObject(Preference)
    lastRun.setName(LAST_RUN_PREFERENCE)
    lastRun.setValueString("01/01/1970 00:00:00")
}

def fromDate = Date.parse("dd/MM/yyyy hh:mm:ss", lastRun.valueString)
logger.warn("Running Tessitura export from {}", lastRun.valueString)

// contacts
def contactsToImport = args.context.select(SelectQuery.query(Contact, Contact.MODIFIED_ON.gt(fromDate)))
logger.warn("Running Tessitura contact import {} records.", contactsToImport.size())
importContacts(contactsToImport)

// enrolments
def enrolmentsToImport = args.context.select(SelectQuery.query(Enrolment, Enrolment.STATUS.in(EnrolmentStatus.SUCCESS, EnrolmentStatus.CANCELLED, EnrolmentStatus.REFUNDED).andExp(Enrolment.MODIFIED_ON.gt(fromDate))))
logger.warn("Running Tessitura enrolment import {} records.", enrolmentsToImport.size())
importEnrolments(enrolmentsToImport)

// invoice lines
def invoiceLinesToImport = args.context.select(SelectQuery.query(InvoiceLine, InvoiceLine.CREATED_ON.gt(fromDate)))
logger.warn("Running Tessitura invoice import {} records.", invoiceLinesToImport.size())
importInvoiceLines(invoiceLinesToImport)

// certificates
def certificatesToImport = args.context.select(SelectQuery.query(Certificate, Certificate.IS_QUALIFICATION.eq(true).andExp(Certificate.CREATED_ON.gt(fromDate))))
logger.warn("Running Tessitura certificate import {} records.", certificatesToImport.size())
importCertificates(certificatesToImport)

// waiting lists
def waitingListsToImport = args.context.select(SelectQuery.query(WaitingList, WaitingList.CREATED_ON.gt(fromDate)))
logger.warn("Running Tessitura waiting list import {} records.", waitingListsToImport.size())
importWaitingLists(waitingListsToImport)

// contact relations
def relationsToImport = args.context.select(SelectQuery.query(ContactRelation, ContactRelation.CREATED_ON.gt(fromDate)))
logger.warn("Running Tessitura relations import {} records.", relationsToImport.size())
importRelationships(relationsToImport)

//  lastRun.setValueString(new Date().format("dd/MM/yyyy hh:mm:ss"))
args.context.commitChanges()


def getPrefByName(name, context) {
    return context.selectOne(SelectQuery.query(Preference, Preference.NAME.eq(name)))
}

def generateContactImportXml(args) {
    def contact = args.contact
    def countries = args.countries
    def states = args.states
    def stateCodes = args.stateCodes
    def titles = args.titles

    def trimmedState = contact?.state?.trim()?.toLowerCase()

    def countryId = countries[contact?.country?.name?.toLowerCase()] ?: countries[contact?.country?.isoCodeAlpha3?.toLowerCase()]
    def suburb = contact?.suburb?.take(30)
    def stateId = states[trimmedState] != null ? states[trimmedState] : stateCodes[trimmedState]
    def street = contact?.street?.take(55) ?: "Unknown"

    def streetParts = street.split('\n', 2)

    def street1 = streetParts[0]
    def street2 = streetParts.length > 1 ? streetParts[1] : null

    def writer = new StringWriter()

    def xml = new MarkupBuilder(writer)
    xml.ConstituentSnapshot() {
        Address() {
            AddressType() {
                if (contact?.street?.toUpperCase()?.startsWith("PO BOX")) {
                    Id(AddressType.POBoxAddress.value())
                } else if (contact.isCompany) {
                    Id(AddressType.CorporateOrFoundation.value())
                } else {
                    Id(AddressType.HomeAddress.value())
                }
            }
            City(suburb ?: "Sydney")
            Country() {
                Id(countryId ?: 227)
            }
            AllowMarketing(true)
            PrimaryIndicator(false)
            Label("true")
            // FIXME: didn't find description for that, populating with value from example
            Months("YYYYYYYYYYYY")
            // FIXME: didn't find description for that, populating with value from example
            PostalCode(contact.postcode ?: "2000")
            State() {
                Id(countryId != null ? stateId : stateId ?: 79)
            }
            Street1(street1)
            if (street2) {
                Street2(street2)
            }
        }
        ConstituentType() {
            if (contact.isCompany) {
                Id(ConstituentType.Corporation.value())
            } else {
                Id(ConstituentType.Individual.value())
            }
        }
        if (contact.email) {
            ElectronicAddress() {
                Address(contact.email)
                ElectronicAddressType() {
                    Id(1)
                }
            }
        }
        EmarketIndicator() {
            Id(contact.allowEmail ? 4 : 2)
        }
        FirstName(contact?.firstName?.take(20))
        Inactive() {
            Id(1)
        }
        LastName(contact.lastName.take(55))
        OriginalSource() {
            Id(5)
        }
        if (titles[contact.title?.toLowerCase()]) {
            Prefix() {
                Id(titles[contact.title?.toLowerCase()])
            }
        }
        PrimaryPhoneNumbers() {
            if (StringUtils.trimToNull(contact.homePhone)) {
                Phone() {
                    PhoneNumber(contact.homePhone)
                    PhoneType() {
                        Id(PhoneType.Landline.value())
                    }
                }
            }
            if (StringUtils.trimToNull(contact.mobilePhone)) {
                Phone() {
                    PhoneNumber(contact.mobilePhone)
                    PhoneType() {
                        Id(PhoneType.Mobile.value())
                    }
                }
            }
            if (StringUtils.trimToNull(contact.fax)) {
                Phone() {
                    PhoneNumber(contact.fax)
                    PhoneType() {
                        Id(PhoneType.Fax.value())
                    }
                }
            }
        }
    }

    writer.toString()
}

def generateStudentIdAttributeXml(contactId, studentId) {
    def writer = new StringWriter()
    def xml = new MarkupBuilder(writer)
    xml.Attribute() {
        Constituent() {
            Id(contactId)
        }
        Keyword() {
            DataType(1)
            Description("onCourse Student ID")
            EditMask("Alphanumeric")
            Id(413)
            MultipleValue("false")
        }
        Value(studentId)
    }

    writer.toString()
}

def importContacts(contacts) {
    def tessituraClient = new RESTClient(TESSITURA_BASE_URL)

    // need preemptive basic authentication header since tessitura service doesn't negotiate authentication
    tessituraClient.headers["Authorization"] = "Basic b25jb3Vyc2U6V2ViVXNlcjpPTkNPVVJTRV9TRVJWRVI6RTVjQGxhbnRl"

    // don't throw exception if getting something other than code 200
    tessituraClient.handler.failure = { response -> return response }

    def countries = [:]
    def states = [:]
    def stateCodes = [:]
    def prefixes = [:]

    tessituraClient.get(path: COUNTIES_PATH, contentType: ContentType.XML) { response, countryData ->
        if (response.statusLine.statusCode == 200) {
            countryData.Country.each {
                country -> countries.put(country.Description.text().toLowerCase(), country.Id.text())
            }
        } else {
            logger.error("Received response with code: {}, body: {}", response.statusLine.statusCode, response.data)
            throw new RuntimeException("Tessitura is not available.")
        }
    }

    tessituraClient.get(path: STATES_PATH, contentType: ContentType.XML) { response, stateData ->
        if (response.statusLine.statusCode == 200) {
            stateData.State.each {
                state -> states.put(state.Description.text().toLowerCase(), state.Id.text())
            }

            stateData.State.each {
                state -> stateCodes.put(state.StateCode.text().toLowerCase(), state.Id.text())
            }
        } else {
            logger.error("Received response with code: {}, body: {}", response.statusLine.statusCode, response.data)
            throw new RuntimeException("Tessitura is not available.")
        }
    }

    tessituraClient.get(path: PREFIXES_PATH, contentType: ContentType.XML) { response, prefixData ->
        if (response.statusLine.statusCode == 200) {
            prefixData.Prefix.each { prefix ->
                if (prefix.Inactive.text() == "false") {
                    prefixes.put(prefix.Description.text().toLowerCase(), prefix.Id.text())
                }
            }
        } else {
            logger.error("Received response with code: {}, body: {}", response.statusLine.statusCode, response.data)
            throw new RuntimeException("Tessitura is not available.")
        }
    }

    def counter = 1

    contacts.each { contact ->



        try {
            def tessituraContact = findTessituraContact(contact, tessituraClient)

            if (tessituraContact) {
                logger.warn("Found existing contact. Updating. {} contact out of {}, id={}", counter, contacts.size(), contact.id)
                updateContact(tessituraContact, contact, tessituraClient, countries, states, stateCodes, prefixes)
            } else {
                logger.warn("Creating new contact. Updating. {} contact out of {}, id={}", counter, contacts.size(), contact.id)
                def requestXmlBody = generateContactImportXml(
                        contact: contact, countries: countries, states: states, stateCodes: stateCodes, titles: prefixes)

                def tessituraId

                tessituraClient.post(path: CREATE_CONTACT_PATH,
                        body: requestXmlBody,
                        requestContentType: ContentType.XML,
                        contentType: ContentType.XML) { response, constituent ->
                    if (response.statusLine.statusCode == 200) {
                        tessituraId = constituent.Id.text()
                    } else {
                        logger.error("Got response code {} when importing contact {} {}\nRequest: {}\nResponse: {}",
                                response.statusLine.statusCode, contact.firstName, contact.lastName, requestXmlBody, response.data)
                    }
                }

                if (tessituraId) {
                    def setAttrRequest = generateStudentIdAttributeXml(tessituraId, contact.id)

                    tessituraClient.post(path: CREATE_ATTRIBUTE_PATH,
                            body: setAttrRequest,
                            requestContentType: ContentType.XML) { response ->
                        if (response.statusLine.statusCode != 200) {
                            // handle error
                            logger.error("Got response code {} when creating student for {} {}\nRequest: {}\nResponse: {}",
                                    response.statusLine.statusCode, contact.firstName, contact.lastName, setAttrRequest, response.data)
                        }
                    }

                    def industryTag = Cayenne.objectForQuery(contact.objectContext, SelectQuery.query(Tag, Tag.NAME.eq("Industry")))

                    def fullTagName
                    fullTagName = { tag -> tag.parentTag == industryTag ? tag.name : "${fullTagName(tag.parentTag)} - ${tag.name}" }

                    contact.tags.findAll { tag ->
                        def findParent
                        findParent = { Tag parent ->
                            parent ? parent == industryTag ? true : findParent(parent.parentTag) : false
                        }

                        findParent(tag)
                    }.each { tag ->
                        tessituraClient.request(Method.POST, ContentType.XML) {
                            uri.path = CREATE_ATTRIBUTE_PATH
                            body = ({
                                def writer = new StringWriter()
                                def xml = new MarkupBuilder(writer)
                                xml.Attribute() {
                                    Constituent() {
                                        Id(tessituraId)
                                    }
                                    Keyword() {
                                        Id(423)
                                    }
                                    Value(fullTagName(tag).take(30))
                                }

                                writer.toString()
                            })()

                            response.failure = { emailResp, reader ->
                                throw new ImportException(
                                        "Tessitura error. Code ${emailResp.statusLine.statusCode}. Response data: ${reader}")
                            }
                        }
                    }
                }
            }
        } catch (ImportException importException) {
            logger.error("Import failed for contact {} {} (id={})", contact.firstName, contact.lastName, contact.id, importException)

            sendEmail("Tessitura ",
                    "The following error was encountered when sending data from onCourse to Tessitura:\n\n" +
                            "Error importing contact ${contact.firstName} ${contact.lastName}:\n" +
                            "${importException.message}\n\n" +
                            "Please fix this problem and the data will be synchronised automatically.",
                    "martin.keen@nida.edu.au")
        }

        counter++

        if ("stop" == getPrefByName("tessitura.import", contact.context)?.valueString) {
            throw new RuntimeException("Import interrupted")
        }
    }
}


def  findTessituraEnrolment(enrolment, tessituraHost) {

    tessituraHost.request(Method.GET, ContentType.XML) {
        uri.path = "Custom/Enrolments/"
        uri.query = [oncourse_id : enrolment.id]

        response.success = { resp, searchResult ->
            if (searchResult.Enrolment.size() == 1) {
                return searchResult.Enrolment
            }
            return null
        }

        response.failure = { resp, reader ->
            throw new RuntimeException(
                    "Tessitura error. Code ${resp.statusLine.statusCode}. Response data: ${reader}")
        }
    }

}

def findTessituraContact(contact, tessituraHost) {

    tessituraHost.request(Method.GET, ContentType.XML) {
        uri.path = SEARCH_CONTACT_PATH
        uri.query = [type: "attribute", key: "onCourse Student ID", op: "Equals", value: contact.id]

        response.success = { resp, searchResult ->
            if (searchResult.ConstituentSummaries.ConstituentSummary.size() == 1) {
                return searchResult.ConstituentSummaries.ConstituentSummary
            }

            return null
        }

        response.failure = { resp, reader ->
            throw new RuntimeException(
                    "Tessitura error. Code ${resp.statusLine.statusCode}. Response data: ${reader}")
        }
    }
}

def findAddressForConstituent(constituentId, tessituraHost) {
    tessituraHost.request(Method.GET, ContentType.XML) {
        uri.path = "CRM/Addresses/"
        uri.query = [constituentId: constituentId, includeAffiliations: "false", primaryOnly: "true", includeFromAffiliations: "false"]

        response.success = { resp, searchResult ->
            if (searchResult.Address.size() == 1) {
                return searchResult.Address.Id.text()
            }

            return null
        }

        response.failure = { resp, reader ->
            throw new RuntimeException(
                    "Tessitura error. Code ${resp.statusLine.statusCode}. Response data: ${reader}")
        }
    }
}

def findEmailAddressForConstituent(constituentId, tessituraHost) {
    tessituraHost.request(Method.GET, ContentType.XML) {
        uri.path = "CRM/ElectronicAddresses/"
        uri.query = [constituentIds: constituentId, includeAffiliations: "false", primaryOnly: "true", includeFromAffiliations: "false"]

        response.success = { resp, searchResult ->
            if (searchResult.ElectronicAddress.size() == 1) {
                return searchResult.ElectronicAddress.Id.text()
            }

            return null
        }

        response.failure = { resp, reader ->
            throw new RuntimeException(
                    "Tessitura error. Code ${resp.statusLine.statusCode}. Response data: ${reader}")
        }
    }
}

def findPhonesForConstituent(constituentId, tessituraHost) {
    tessituraHost.request(Method.GET, ContentType.XML) {
        uri.path = "CRM/Phones/"
        uri.query = [constituentId: constituentId, includeAffiliations: "false", primaryOnly: "false"]

        response.success = { resp, searchResult ->
            return searchResult
        }

        response.failure = { resp, reader ->
            throw new RuntimeException(
                    "Tessitura error. Code ${resp.statusLine.statusCode}. Response data: ${reader}")
        }
    }
}

def updateContact(contactSummary, contact, tessituraHost, countries, states, stateCodes, titles) {

    def tessituraContactId = contactSummary.Id.text()

    //update basic contact detail
    tessituraHost.request(Method.GET, ContentType.XML) {
        uri.path = "CRM/Constituents/${tessituraContactId}"

        response.success = { resp, constituent ->

            constituent.EmarketIndicator.Description.replaceNode {}
            constituent.EmarketIndicator.Id.replaceBody(contact.allowEmail ? 4 : 2)
            constituent.EmarketIndicator.Inactive.replaceNode {}

            constituent.FirstName.replaceBody(contact?.firstName?.take(20))
            constituent.LastName.replaceBody(contact.lastName.take(55))

            if (titles[contact.title?.toLowerCase()]) {
                constituent.Prefix.Description.replaceNode {}
                constituent.Prefix.Id.replaceBody(titles[contact.title?.toLowerCase()])
                constituent.Prefix.Inactive.replaceNode {}
            }

            tessituraHost.request(Method.PUT, ContentType.XML) {
                uri.path = "CRM/Constituents/${tessituraContactId}"
                body = XmlUtil.serialize(constituent)

                response.failure = { updateResponse, reader ->
                    throw new RuntimeException(
                            "Tessitura error. Code ${updateResponse.statusLine.statusCode}. Response data: ${reader}")
                }
            }
        }

        response.failure = { resp, reader ->
            throw new RuntimeException(
                    "Tessitura error. Code ${resp.statusLine.statusCode}. Response data: ${reader}")
        }
    }

    // update address
    def addressId = findAddressForConstituent(tessituraContactId, tessituraHost)

    if (addressId) {
        tessituraHost.request(Method.GET, ContentType.XML) {
            uri.path = "CRM/Addresses/${addressId}"

            response.success = { resp, address ->
                def trimmedState = contact?.state?.trim()?.toLowerCase()

                def countryId = countries[contact?.country?.name?.toLowerCase()] ?: countries[contact?.country?.isoCodeAlpha3?.toLowerCase()]
                def suburb = contact?.suburb?.take(30)
                def stateId = states[trimmedState] != null ? states[trimmedState] : stateCodes[trimmedState]

                if (countryId == null) {
                    stateId = stateId ?: 79
                }

                def street = contact?.street?.take(55) ?: "Unknown"

                address.AddressType.Description.replaceNode {}
                address.AddressType.Inactive.replaceNode {}

                if (contact?.street?.toUpperCase()?.startsWith("PO BOX")) {
                    address.AddressType.Id.replaceBody(AddressType.POBoxAddress.value())
                } else if (contact.isCompany) {
                    address.AddressType.Id.replaceBody(AddressType.CorporateOrFoundation.value())
                } else {
                    address.AddressType.Id.replaceBody(AddressType.HomeAddress.value())
                }

                address.City.replaceBody(suburb ?: "Sydney")

                address.Country.Descreiption.replaceNode {}
                address.Country.Inactive.replaceNode {}
                address.Country.Id.replaceBody(countryId ?: 227)

                address.PostalCode.replaceBody(contact.postcode ?: "2000")

                if (stateId) {
                    address.State.Country.replaceNode {}
                    address.State.Description.replaceNode {}
                    address.State.Inactive.replaceNode {}
                    address.State.StateCode.replaceNode {}
                    address.State.Id.replaceBody(stateId)
                } else {
                    address.State.replaceNode {}
                }

                def streetParts = street.split('\n', 2)

                address.Street1.replaceBody(streetParts[0])
                address.Street2.replaceBody(streetParts.length > 1 ? streetParts[1] : "")

                tessituraHost.request(Method.PUT, ContentType.XML) {
                    uri.path = "CRM/Addresses/${addressId}"
                    body = XmlUtil.serialize(address)

                    response.failure = { addrResp, reader ->
                        throw new ImportException(
                                "Tessitura error. Code ${addrResp.statusLine.statusCode}. Response data: ${reader}")
                    }
                }
            }

            response.failure = { resp, reader ->
                throw new RuntimeException(
                        "Tessitura error. Code ${resp.statusLine.statusCode}. Response data: ${reader}")
            }
        }
    }

    // update email
    def emailId = findEmailAddressForConstituent(tessituraContactId, tessituraHost)

    if (emailId) {
        if (StringUtils.trimToNull(contact.email)) {
            tessituraHost.request(Method.GET, ContentType.XML) {
                uri.path = "CRM/ElectronicAddresses/${emailId}"

                response.success = { resp, emailAddress ->

                    emailAddress.Address.replaceBody(contact.email)

                    tessituraHost.request(Method.PUT, ContentType.XML) {
                        uri.path = "CRM/ElectronicAddresses/${emailId}"
                        body = XmlUtil.serialize(emailAddress)

                        response.failure = { emailResp, reader ->
                            throw new ImportException(
                                    "Tessitura error. Code ${emailResp.statusLine.statusCode}. Response data: ${reader}")
                        }
                    }
                }

                response.failure = { resp, reader ->
                    throw new RuntimeException(
                            "Tessitura error. Code ${resp.statusLine.statusCode}. Response data: ${reader}")
                }
            }
        } else {
            tessituraHost.request(Method.DELETE, ContentType.XML) {
                uri.path = "CRM/ElectronicAddresses/${emailId}"

                response.failure = { resp, reader ->
                    throw new ImportException(
                            "Tessitura error. Code ${resp.statusLine.statusCode}. Response data: ${reader}")
                }
            }
        }
    } else {
        tessituraHost.request(Method.POST, ContentType.XML) {
            uri.path = "CRM/ElectronicAddresses/"
            body = ({

                def writer = new StringWriter()
                def xml = new MarkupBuilder(writer)

                xml.ElectronicAddress() {
                    Address(contact.email)
                    Constituent() {
                        Id(tessituraContactId)
                    }
                    ElectronicAddressType() {
                        Id(1)
                    }
                }

                writer.toString()
            })()

            response.failure = { emailResp, reader ->
                throw new ImportException(
                        "Tessitura error. Code ${emailResp.statusLine.statusCode}. Response data: ${reader}")
            }
        }
    }

    def phones = findPhonesForConstituent(tessituraContactId, tessituraHost)

    def updatePhoneNumber = { phoneId, newNumber ->
        tessituraHost.request(Method.GET, ContentType.XML) {
            uri.path = "CRM/Phones/${phoneId}"

            response.success = { resp, phone ->
                phone.PhoneNumber.replaceBody(newNumber)

                tessituraHost.request(Method.PUT, ContentType.XML) {
                    uri.path = "CRM/Phones/${phoneId}"
                    body = XmlUtil.serialize(phone)

                    response.failure = { phoneResp, reader ->
                        throw new RuntimeException(
                                "Tessitura error. Code ${phoneResp.statusLine.statusCode}. Response data: ${reader}")
                    }
                }
            }

            response.failure = { resp, reader ->
                throw new RuntimeException(
                        "Tessitura error. Code ${resp.statusLine.statusCode}. Response data: ${reader}")
            }
        }
    }

    def createPhone = { type, number ->
        tessituraHost.request(Method.POST, ContentType.XML) {
            uri.path = "CRM/Phones/"
            body = ({
                def writer = new StringWriter()
                def xml = new MarkupBuilder(writer)

                xml.Phone() {
                    Constituent() {
                        Id(tessituraContactId)
                    }
                    PhoneNumber(number)
                    PhoneType() {
                        Id(type)
                    }
                }

                writer.toString()
            })()

            response.failure = { resp, reader ->
                throw new RuntimeException(
                        "Tessitura error. Code ${resp.statusLine.statusCode}. Response data: ${reader}")
            }
        }
    }

    if (StringUtils.trimToNull(contact.homePhone)) {
        def homePhone = phones.Phone.find { phone -> phone.PhoneType.Id.text() == PhoneType.Landline.value() }

        if (!homePhone.isEmpty()) {
            updatePhoneNumber(homePhone.Id.text(), contact.homePhone)
        } else {
            createPhone(PhoneType.Landline.value(), contact.homePhone)
        }
    }
    if (StringUtils.trimToNull(contact.mobilePhone)) {
        def mobilePhone = phones.Phone.find { phone -> phone.PhoneType.Id.text() == PhoneType.Mobile.value() }

        if (!mobilePhone.isEmpty()) {
            updatePhoneNumber(mobilePhone.Id.text(), contact.mobilePhone)
        } else {
            createPhone(PhoneType.Mobile.value(), contact.mobilePhone)
        }
    }
    if (StringUtils.trimToNull(contact.fax)) {
        def fax = phones.Phone.find { phone -> phone.PhoneType.Id.text() == PhoneType.Fax.value() }

        if (!fax.isEmpty()) {
            updatePhoneNumber(fax.Id.text(), contact.fax)
        } else {
            createPhone(PhoneType.Fax.value(), contact.fax)
        }
    }
}

def importInvoiceLines(invoiceLines) {
    def logger = LogManager.getLogger()

    def tessituraClient = new RESTClient(TESSITURA_BASE_URL)

    // need preemptive basic authentication header since tessitura service doesn't negotiate authentication
    tessituraClient.headers["Authorization"] = "Basic b25jb3Vyc2U6V2ViVXNlcjpPTkNPVVJTRV9TRVJWRVI6RTVjQGxhbnRl"

    // don't throw exception if getting something other than code 200
    tessituraClient.handler.failure = { response -> return response }

    def counter = 1

    invoiceLines.each { invoiceLine ->

        logger.warn("Importing {} invoice line out of {}, id={}", counter, invoiceLines.size(), invoiceLine.id)

        def tessituraPayer = findTessituraContact(invoiceLine.invoice.contact, tessituraClient)

        if (tessituraPayer) {
            tessituraClient.request(Method.POST, ContentType.XML) {
                uri.path = "Custom/InvoiceLines/"
                body = ({
                    def writer = new StringWriter()
                    def xml = new MarkupBuilder(writer)

                    xml.InvoiceLine() {
                        oncourse_id(invoiceLine.id)
                        oncourse_enrolment_id(invoiceLine?.enrolment?.id)
                        customer_no(tessituraPayer.Id.text())
                        oncourse_contact_id(invoiceLine.invoice.contact.id)
                        amount(invoiceLine.finalPriceToPayIncTax.doubleValue())
                        gst(invoiceLine.totalTax.doubleValue())
                        discount(invoiceLine?.discounts?.find { true }?.name)
                        description(invoiceLine.description)
                        due_date(invoiceLine.invoice.dateDue.format("yyyy-MM-dd'T'HH:mm:ss"))
                    }

                    writer.toString()
                })()

                response.failure { resp, reader ->
                    throw new RuntimeException(
                            "Tessitura error. Code ${resp.statusLine.statusCode}. Response data: ${reader}")
                }
            }
        } else {
            logger.error("Couldn't find payer (contact.id=${invoiceLine.invoice.contact.id})")
        }

        counter++

        if ("stop" == getPrefByName("tessitura.import", invoiceLine.context)?.valueString) {
            throw new RuntimeException("Import interrupted")
        }
    }
}

def importCertificates(certificates) {
    def logger = LogManager.getLogger()

    def tessituraClient = new RESTClient(TESSITURA_BASE_URL)

    // need preemptive basic authentication header since tessitura service doesn't negotiate authentication
    tessituraClient.headers["Authorization"] = "Basic b25jb3Vyc2U6V2ViVXNlcjpPTkNPVVJTRV9TRVJWRVI6RTVjQGxhbnRl"

    // don't throw exception if getting something other than code 200
    tessituraClient.handler.failure = { response -> return response }

    def counter = 1

    certificates.each { certificate ->

        logger.warn("Importing {} certificate out of {}, id={}", counter, certificates.size(), certificate.id)

        if (certificate.student.contact == null) {
            logger.error("Found student [id={}] without related contact, skipping...", certificate.student.id)
            return
        }

        def tessituraContact = findTessituraContact(certificate.student.contact, tessituraClient)

        if (tessituraContact) {
            tessituraClient.request(Method.POST, ContentType.XML) {
                uri.path = "Custom/Qualifications/"

                body = ({
                    def writer = new StringWriter()
                    def xml = new MarkupBuilder(writer)

                    xml.Qualification() {
                        oncourse_id(certificate.id)
                        customer_no(tessituraContact.Id.text())
                        contact_id(certificate.student.contact.id)
                        oncourse_student_id(certificate.student.contact.id)
                        // TODO: is that really the same thing as contact id?
                        qual_name(certificate?.qualification?.title?.take(50))
                        qual_issued_date(certificate.createdOn.format("yyyy-MM-dd'T'HH:mm:ss"))
                        qual_revoked_date(certificate?.revokedOn?.format("yyyy-MM-dd'T'HH:mm:ss"))
                    }

                    writer.toString()
                })()

                response.failure { resp, reader ->
                    throw new RuntimeException(
                            "Tessitura error. Code ${resp.statusLine.statusCode}. Response data: ${reader}")
                }
            }
        } else {
            logger.error("Couldn't find contact (id=${certificate.student.contact.id})")
        }

        counter++

        if ("stop" == getPrefByName("tessitura.import", certificate.context)?.valueString) {
            throw new RuntimeException("Import interrupted")
        }
    }
}

def importWaitingLists(waitingLists) {
    def logger = LogManager.getLogger()

    def tessituraClient = new RESTClient(TESSITURA_BASE_URL)

    // need preemptive basic authentication header since tessitura service doesn't negotiate authentication
    tessituraClient.headers["Authorization"] = "Basic b25jb3Vyc2U6V2ViVXNlcjpPTkNPVVJTRV9TRVJWRVI6RTVjQGxhbnRl"

    // don't throw exception if getting something other than code 200
    tessituraClient.handler.failure = { response -> return response }

    def counter = 1

    waitingLists.each { waitingList ->

        logger.warn("Importing {} waiting list out of {}, id={}", counter, waitingLists.size(), waitingList.id)

        if (waitingList.student.contact == null) {
            logger.error("Found student [id={}] without related contact, skipping...", waitingList.student.id)
            return
        }

        def tessituraContact = findTessituraContact(waitingList.student.contact, tessituraClient)

        if (tessituraContact) {
            tessituraClient.request(Method.POST, ContentType.XML) {
                uri.path = "Custom/WaitingLists/"

                body = ({
                    def writer = new StringWriter()
                    def xml = new MarkupBuilder(writer)

                    xml.WaitingList() {
                        oncourse_id(waitingList.id)
                        customer_no(tessituraContact.Id.text())
                        oncourse_contact_id(waitingList.student.contact.id)
                        course_code(waitingList.course.code)
                        course_name(waitingList.course.name)
                        student_requirements(waitingList.studentNotes)
                        location(waitingList?.sites?.collect { site -> site.name }?.join(", ")?.take(255))
                    }

                    writer.toString()
                })()

                response.failure { resp, reader ->
                    throw new RuntimeException(
                            "Tessitura error. Code ${resp.statusLine.statusCode}. Response data: ${reader}")
                }
            }
        } else {
            logger.error("Couldn't find contact (id=${waitingList.student.contact.id})")
        }

        counter++

        if ("stop" == getPrefByName("tessitura.import", waitingList.context)?.valueString) {
            throw new RuntimeException("Import interrupted")
        }
    }
}

def importEnrolments(enrolments) {
    def logger = LogManager.getLogger()

    def tessituraClient = new RESTClient(TESSITURA_BASE_URL)

    // need preemptive basic authentication header since tessitura service doesn't negotiate authentication
    tessituraClient.headers["Authorization"] = "Basic b25jb3Vyc2U6V2ViVXNlcjpPTkNPVVJTRV9TRVJWRVI6RTVjQGxhbnRl"

    // don't throw exception if getting something other than code 200
    tessituraClient.handler.failure = { response -> return response }

    def counter = 1

    enrolments.each { enrolment ->

        logger.warn("Importing {} enrolment out of {}, id={}", counter, enrolments.size(), enrolment.id)

        if (enrolment.student.contact == null) {
            logger.error("Found student [id={}] without related contact, skipping...", enrolment.student.id)
            return
        }

        def tessituraStudent = findTessituraContact(enrolment.student.contact, tessituraClient)
        def tessituraPayer = findTessituraContact(enrolment.invoiceLine.invoice.contact, tessituraClient)

        if (tessituraStudent && tessituraPayer) {
            def tesseturaEnrolment = findTessituraEnrolment(enrolment, tessituraClient)

            if (tesseturaEnrolment) {

                tessituraClient.request(Method.PUT, ContentType.XML) {
                    uri.path = "Custom/Enrolments/${tesseturaEnrolment.id.text()}"
                    body = ({

                        def writer = new StringWriter()
                        def xml = new MarkupBuilder(writer)

                        xml.Enrolment() {
                            id(tesseturaEnrolment.id.text())
                            course_status(enrolment.status.databaseValue)
                        }

                        writer.toString()
                    })()

                    response.failure { resp, reader ->
                        throw new RuntimeException(
                                "Tessitura error. Code ${resp.statusLine.statusCode}. Response data: ${reader}")
                    }
                }

            } else {

                tessituraClient.request(Method.POST, ContentType.XML) {
                    uri.path = "Custom/Enrolments/"
                    body = ({

                        def disciplineTag = ObjectSelect.query(Tag).where(Tag.ID.eq(313)).selectOne(enrolment.objectContext)
                        def deliveryTag = ObjectSelect.query(Tag).where(Tag.ID.eq(325)).selectOne(enrolment.objectContext)
                        def productTag = ObjectSelect.query(Tag).where(Tag.ID.eq(317)).selectOne(enrolment.objectContext)
                        def demographicTag = ObjectSelect.query(Tag).where(Tag.ID.eq(282)).selectOne(enrolment.objectContext)

                        def writer = new StringWriter()
                        def xml = new MarkupBuilder(writer)

                        xml.Enrolment() {
                            oncourse_id(enrolment.id)
                            customer_no(tessituraStudent.Id.text())
                            oncourse_contact_id(enrolment.student.contact.id)
                            paid_by_customer_no(tessituraPayer.Id.text())
                            oncourse_paid_by_contact_id(enrolment.invoiceLine.invoice.contact.id)
                            course_type(enrolment.courseClass.course.code.take(1))
                            course_name(enrolment.courseClass.course.name)
                            start_date(enrolment.courseClass?.startDateTime?.format("yyyy-MM-dd'T'HH:mm:ss"))
                            end_date(enrolment.courseClass?.endDateTime?.format("yyyy-MM-dd'T'HH:mm:ss"))
                            is_full_qualification(enrolment.courseClass.course.isSufficientForQualification)
                            amount_paid(enrolment.invoiceLine.finalPriceToPayIncTax.doubleValue())
                            amount_paid_gst(enrolment.invoiceLine.totalTax.doubleValue())
                            oncourse_tag_group_discipline(enrolment.courseClass.course.tags.find { tag -> tag.root == disciplineTag }?.name)
                            oncourse_tag_group_delivery(enrolment.courseClass.course.tags.find { tag -> tag.root == deliveryTag }?.name)
                            oncourse_tag_group_product(enrolment.courseClass.course.tags.find { tag -> tag.root == productTag }?.name)
                            oncourse_tag_group_demographic(enrolment.courseClass.course.tags.find { tag -> tag.root == demographicTag }?.name)
                            on_waiting_list("false") // FIXME: what should go here?
                            course_status(enrolment.status.databaseValue)
                            course_code(enrolment.courseClass.uniqueCode)
                            site_id(enrolment.courseClass.room?.site?.id)
                        }

                        writer.toString()
                    })()

                    response.failure { resp, reader ->
                        throw new RuntimeException(
                                "Tessitura error. Code ${resp.statusLine.statusCode}. Response data: ${reader}")
                    }
                }
            }
        } else {
            logger.error("Couldn't find student (contact.id=${enrolment.student.contact.id}) " +
                    "or payer (contact.id=${enrolment.invoiceLine.invoice.contact.id})")
        }

        counter++

        if ("stop" == getPrefByName("tessitura.import", enrolment.context)?.valueString) {
            throw new RuntimeException("Import interrupted")
        }
    }
}

def importRelationships(contactRelations) {
    def logger = LogManager.getLogger()

    def tessituraClient = new RESTClient(TESSITURA_BASE_URL)

    // need preemptive basic authentication header since tessitura service doesn't negotiate authentication
    tessituraClient.headers["Authorization"] = "Basic b25jb3Vyc2U6V2ViVXNlcjpPTkNPVVJTRV9TRVJWRVI6RTVjQGxhbnRl"

    // don't throw exception if getting something other than code 200
    tessituraClient.handler.failure = { response -> return response }

    def counter = 1

    contactRelations.each { relation ->

        logger.warn("Importing {} contact relation out of {}, id={}", counter, contactRelations.size(), relation.id)

        def createAssociation = { constituent, associate, associationType, reciprocalAssociation = null ->

            def tessituraConstituent = findTessituraContact(constituent, tessituraClient)
            def tessituraAssociate = findTessituraContact(associate, tessituraClient)

            if (tessituraConstituent && tessituraAssociate) {
                tessituraClient.request(Method.POST, ContentType.XML) {
                    uri.path = "CRM/Associations/"
                    body = ({
                        def writer = new StringWriter()
                        def xml = new MarkupBuilder(writer)

                        xml.Association() {
                            AssociatedConstituent() {
                                Id(tessituraAssociate.Id.text())
                            }
                            AssociationType() {
                                Id(associationType)
                            }
                            Constituent() {
                                Id(tessituraConstituent.Id.text())
                            }
                            Inactive("false")
                            IsIncludedInSearchResults("true")
                            if (reciprocalAssociation) {
                                ReciprocalAssociation() {
                                    Id(reciprocalAssociation)
                                }
                            }
                        }

                        writer.toString()
                    })()

                    response.failure { resp, reader ->
                        throw new RuntimeException(
                                "Tessitura error. Code ${resp.statusLine.statusCode}. Response data: ${reader}")
                    }
                }
            } else {
                logger.error("No matching contact records for contact (id=${constituent.id}) or its associate (id=${associate.id}) have been found.")
            }
        }

        switch (relation.relationType.fromContactName) {
            case "Employer":
            case "Employee":
                def employer = relation.relationType.fromContactName == "Employer" ? relation.fromContact : relation.toContact
                def employee = relation.relationType.fromContactName == "Employee" ? relation.fromContact : relation.toContact

                def tessituraEmployer = findTessituraContact(employer, tessituraClient)
                def tessituraEmployee = findTessituraContact(employee, tessituraClient)

                if (tessituraEmployer && tessituraEmployee) {

                    tessituraClient.request(Method.POST, ContentType.XML) {
                        uri.path = "CRM/Affiliations/"
                        body = ({
                            def writer = new StringWriter()
                            def xml = new MarkupBuilder(writer)

                            xml.Affiliation() {
                                AffiliationType() {
                                    Id("10007")
                                }
                                GroupConstituent() {
                                    Id(tessituraEmployer.Id.text())
                                }
                                Inactive("false")
                                IndividualConstituent() {
                                    Id(tessituraEmployee.Id.text())
                                }
                            }

                            writer.toString()
                        })()

                        response.failure { resp, reader ->
                            throw new RuntimeException(
                                    "Tessitura error. Code ${resp.statusLine.statusCode}. Response data: ${reader}")
                        }
                    }
                } else {
                    logger.error("No matching contact records for employer (id=${employer.id}) or employee (id=${employee.id}) have been found.")
                }
                break
            case "Spouse":
                createAssociation(relation.fromContact, relation.toContact, "24")
                break
            case "Sibling":
                createAssociation(relation.fromContact, relation.toContact, "19")
                break
            case "Friend":
                createAssociation(relation.fromContact, relation.toContact, "23")
                break
            case "Parent or Guardian":
            case "Child":
                def parent = relation.relationType.fromContactName == "Parent or Guardian" ? relation.fromContact : relation.toContact
                def child = relation.relationType.fromContactName == "Child" ? relation.fromContact : relation.toContact

                createAssociation(parent, child, "2", "5")
                break
            case "Grandparent":
            case "Grandchild":
                def grandparent = relation.relationType.fromContactName == "Grandparent" ? relation.fromContact : relation.toContact
                def grandchild = relation.relationType.fromContactName == "Grandchild" ? relation.fromContact : relation.toContact

                createAssociation(grandparent, grandchild, "8", "10")
                break


            default:
                throw new IllegalStateException("Unknown relation type: ${relation.relationType.fromContactName}")
        }

        counter++

        if ("stop" == getPrefByName("tessitura.import", relation.context)?.valueString) {
            throw new RuntimeException("Import interrupted")
        }
    }
}

def sendEmail(subject, message, emailAddress) {
    def email = Email.create()

    email.from(Preferences.get("email.from"))
    email.to(emailAddress)
    email.subject(subject)

    email.addPart(null, "text/html", message)

    email.send()
}
