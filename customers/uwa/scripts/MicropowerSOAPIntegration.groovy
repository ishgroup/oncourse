/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import wslite.soap.*

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import ish.common.types.ConfirmationStatus
import ish.common.types.ProductStatus
import ish.math.Money
import ish.oncourse.server.cayenne.Account
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.Country
import ish.oncourse.server.cayenne.Invoice
import ish.oncourse.server.cayenne.InvoiceLine
import ish.oncourse.server.cayenne.Membership
import ish.oncourse.server.cayenne.MembershipProduct
import ish.util.AccountUtil
import ish.util.ProductUtil
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById
import org.apache.commons.lang3.StringUtils

//----------------------
membershipProductId = 222
SOAP_CLIENT = "https://integration.micropower.com.au/MIS3084/IntegrationService.asmx"
XML_NAME_SPACE = "http://micropower.com.au/IntegrationService"
CLIENT_IDENTITY = "H21ji5gNeAQ9EJO4lAFoc0CPwQA/nNl8WwKAGYJZW9CT3lbuT3tgtIENkqQFno9cBN7zr3X/nrX6s96T1mIiZ+jk+srS4iLPgJtntGTA6lscPlyd0XH0488IwRsqIc7f9VtX3LXp0F9WVu8tLZelyCs7Cm39jcTFYJect7nxbL8="
SIGNATURE = "ish"
CLIENT_ID = "8218450"
LAST_RUN_PREFERENCE = "mpower.import.lastRun"
//----------------------

def run(args) {
    ObjectContext context = args.context

    MembershipProduct membershipProduct = SelectById.query(MembershipProduct, membershipProductId).selectOne(context)

    if (membershipProduct) {

        Date today = new Date()
        def lastRun = args.context.selectOne(SelectQuery.query(Preference, Preference.NAME.eq(LAST_RUN_PREFERENCE)))

        // Mpower can only return member changes for a maximum period of 10 days
        // if script hasnt been run before, collect last 10 days worth of changes on first execution
        if (!lastRun) {
            def maxTime = new Date() - 9
            lastRun = args.context.newObject(Preference)
            lastRun.setName(LAST_RUN_PREFERENCE)
            lastRun.setValueString(maxTime.format("yyyy-MM-dd"))
        }

        // Mpower can only return member changes for a maximum period of 10 days
        // if script hasnt been run in over 10 days, collect last 10 days worth of changes for this execution
        if (today - Date.parse("yyyy-MM-dd", lastRun.valueString) > 9) {
            def maxTime = new Date() - 9
            lastRun.setValueString(maxTime.format("yyyy-MM-dd"))
        }

//        Date yesterday = new Date() - 1
//        def yesterdayUTC = yesterday.format("yyyy-MM-dd")+"T00:00:00.00"

        def memberChanges = soapGetMemberChangesFromDate(lastRun.valueString)

        def memberIds = []

        memberChanges.body.GetMemberChangesResponse.GetMemberChangesResult.NotificationItems.NotificationItem.each { node ->
            memberIds.add(node.Id)
        }

        memberIds.each { id ->
            processMember(membershipProduct, id, args.context)
        }

        lastRun.setValueString(today.format("yyyy-MM-dd"))
        context.commitChanges()
    }
}

def processMember(MembershipProduct membershipProduct, memberId, context) {

    def xml = soapGetMemberProfile(memberId)

    String firstName = getPropertyValue(xml, "FirstName")
    String lastName = getPropertyValue(xml, "Surname")
    String email1 = getPropertyValue(xml, "Email1")
    String email2 = getPropertyValue(xml, "Email2")


    def contact = ObjectSelect.query(Contact)
            .where(Contact.LAST_NAME.eq(lastName))
            .and(Contact.FIRST_NAME.eq(firstName))
            .and(Contact.EMAIL.in(email1, email2))
            .selectOne(context)


    if (!contact) {
        contact = context.newObject(Contact)
        contact.lastName = lastName
        contact.firstName = firstName
        contact.email = email1

        contact.title = getPropertyValue(xml, "Title")
        String dob = getPropertyValue(xml, "DOB")
        contact.birthDate = dob ? LocalDate.parse(dob, DateTimeFormatter.ofPattern("dd/MM/uuuu")) : null
        contact.mobilePhone = getPropertyValue(xml, "Mobile")
        contact.suburb = getPropertyValue(xml, "Suburb")
        contact.state = getPropertyValue(xml, "State")
        contact.postcode = getPropertyValue(xml, "PostCode")
        String country = getPropertyValue(xml, "Country")
        contact.country = country ? ObjectSelect.query(Country).where(Country.NAME.eq(country)).selectOne(context) : null

        context.commitChanges()
    }

    def membership = ObjectSelect.query(Membership)
            .where(Membership.PRODUCT.eq(membershipProduct))
            .and(Membership.CONTACT.eq(contact))
            .and(Membership.STATUS.eq(ProductStatus.ACTIVE))
            .selectOne(context)

    if (!membership) {
        membership = context.newObject(Membership)
        membership.product = membershipProduct
        membership.contact = contact
        membership.status = ProductStatus.ACTIVE
        membership.expiryDate = ProductUtil.calculateExpiryDate(new Date(), membershipProduct.expiryType, membershipProduct.expiryDays)
        membership.confirmationStatus = ConfirmationStatus.DO_NOT_SEND

        Invoice invoice = context.newObject(Invoice)
        invoice.contact = contact
        invoice.dateDue = LocalDate.now()
        invoice.invoiceDate = LocalDate.now()
        invoice.description = "${membershipProduct.name} created by micropower intergration automatically for ${contact.fullName}".toString()
        invoice.debtorsAccount = AccountUtil.getDefaultDebtorsAccount(context, Account)
        invoice.confirmationStatus = ConfirmationStatus.DO_NOT_SEND

        InvoiceLine invoiceLine = context.newObject(InvoiceLine)
        invoiceLine.invoice = invoice
        invoiceLine.addToProductItems(membership)
        invoiceLine.description = "${membershipProduct.name} created by micropower intergration automatically for ${contact.fullName}".toString()
        invoiceLine.sortOrder = 0
        invoiceLine.title = "${membershipProduct.name} for ${contact.fullName}".toString()
        invoiceLine.unit = StringUtils.EMPTY
        invoiceLine.quantity = BigDecimal.ONE
        invoiceLine.priceEachExTax = Money.ZERO
        invoiceLine.discountEachExTax = Money.ZERO
        invoiceLine.taxEach = Money.ZERO
        invoiceLine.prepaidFeesRemaining = Money.ZERO
        invoiceLine.account = membershipProduct.incomeAccount

        context.commitChanges()
    }

}

def getPropertyValue(xml, propertyKey) {
    return xml.body.GetMemberProfileResponse.GetMemberProfileResult.MemberRecord.MemberFields.'**'.find { node ->
        node.name == propertyKey
    }.value
}

def soapGetMemberChangesFromDate(fromDate) {
    def client = new SOAPClient(SOAP_CLIENT)

    def resp = client.send(SOAPAction:"http://micropower.com.au/IntegrationService/GetMemberChanges") {
        header(xmlns: XML_NAME_SPACE) {
            AuthenticationHeader {
                EncryptedIdentity(CLIENT_IDENTITY)
                Signature(SIGNATURE)
            }
        }
        body(xmlns: "http://micropower.com.au/IntegrationService") {
            GetMemberChanges {
                request {
                    ClientId(CLIENT_ID)
                    FromUTC(fromDate)
                }
            }
        }
    }
    return resp
}

def soapGetMemberProfile(memberId) {
    def client =  new SOAPClient(SOAP_CLIENT)

    def resp = client.send(SOAPAction: "http://micropower.com.au/IntegrationService/GetMemberProfile") {
        header(xmlns: XML_NAME_SPACE) {
            AuthenticationHeader {
                EncryptedIdentity(CLIENT_IDENTITY)
                Signature(SIGNATURE)
            }
        }
        body(xmlns: XML_NAME_SPACE) {
            GetMemberProfile {
                request {
                    ClientId(CLIENT_ID)
                    SearchBySpecification {
                        SearchKeyType("Primary")
                        SearchValue(memberId)
                    }
                }
            }
        }
    }
    return resp
}
