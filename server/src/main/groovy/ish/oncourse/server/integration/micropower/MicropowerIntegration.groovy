/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.integration.micropower

import groovy.transform.CompileDynamic
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
import ish.oncourse.server.cayenne.Preference
import ish.oncourse.server.integration.Plugin
import ish.oncourse.server.integration.PluginTrait
import ish.util.AccountUtil
import ish.util.ProductUtil
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.commons.lang3.StringUtils
import wslite.soap.SOAPClient

import java.time.LocalDate
import java.time.format.DateTimeFormatter

@CompileDynamic
@Plugin(type = 9)
@CompileDynamic
class MicropowerIntegration implements PluginTrait {

    public static final String MICROPOWER_IDENTITY = "identity"
    public static final String MICROPOWER_SIGNATURE = "signature"
    public static final String MICROPOWER_CLIENT_ID = "clientId"
    public static final String MICROPOWER_PRODUCT_SKU = "productSku"

    static LAST_RUN_PREFERENCE = "mpower.import.lastRun"
    static SOAP_CLIENT = "https://integration.micropower.com.au/MIS3084/IntegrationService.asmx"
    static XML_NAME_SPACE = "http://micropower.com.au/IntegrationService"

    String identity
    String signature
    String clientId
    String productSku
    ObjectContext context

    MicropowerIntegration(Map args) {
        loadConfig(args)

        this.identity = configuration.getIntegrationProperty(MICROPOWER_IDENTITY).value
        this.signature = configuration.getIntegrationProperty(MICROPOWER_SIGNATURE).value
        this.clientId = configuration.getIntegrationProperty(MICROPOWER_CLIENT_ID).value
        this.productSku = configuration.getIntegrationProperty(MICROPOWER_PRODUCT_SKU).value
    }

    /**
     * Gets the membershipProduct with the sku specified in the integration. This is the membershipProduct equivalent
     * to micropower memberships
     *
     * @param sku sku of the equivalent membership product
     * @param context ObjectContext used for cayenne.query.ObjectSelect
     * @return membershipProduct with the sku specified
     */
    protected MembershipProduct getMembershipProduct() {
        return ObjectSelect.query(MembershipProduct)
                            .where(MembershipProduct.SKU.eq(productSku))
                            .selectOne(this.context)
    }

    /**
     * A 'lastRun' preference is used to check the last time this script has run.
     * We can only get changes in the last 10 days, so, if the lastRun date is OVER 10 days old,
     * set the lastRun date as 10 days ago.
     *
     * If no lastRun preference exists, one is created.
     *
     * @param context ObjectContext used for cayenne.query.ObjectSelect
     * @return lastRun date in format yyyy-MM-dd
     */
    protected String getLastRunDate() {
        Date today = new Date()
        Preference lastRun = ObjectSelect.query(Preference)
                .where(Preference.NAME.eq(LAST_RUN_PREFERENCE))
                .selectOne(this.context)


        // Mpower can only return member changes for a maximum period of 10 days
        // if script hasnt been run before, collect last 10 days worth of changes on first execution
        if (!lastRun) {
            def maxTime = new Date() - 9
            lastRun = this.context.newObject(Preference)
            lastRun.setName(LAST_RUN_PREFERENCE)
            lastRun.setValueString(maxTime.format("yyyy-MM-dd"))
        }

        // Mpower can only return member changes for a maximum period of 10 days
        // if script hasnt been run in over 10 days, collect last 10 days worth of changes for this execution
        if (today - Date.parse("yyyy-MM-dd", lastRun.valueString) > 9) {
            def maxTime = today - 9
            lastRun.setValueString(maxTime.format("yyyy-MM-dd"))
        }

        this.context.commitChanges()
        return lastRun.valueString
    }

    /**
     * Get the id of every member profile that has been updated/created since fromDate
     *
     * @param fromDate the date to check for changes from
     * @return a list of micropower member ids
     */
    protected getMemberIds(fromDate) {
        def memberChanges = soapGetMemberChangesFromDate(fromDate)
        def memberIds = []

        memberChanges.body.GetMemberChangesResponse.GetMemberChangesResult.NotificationItems.NotificationItem.each { node ->
            memberIds.add(node.Id)
        }
        return memberIds
    }

    /**
     * Creates equivalent onCourse contacts to micropower members. Creates a membership and invoice for the members
     * micropower membership.
     *
     * @param id id of micropower member. Used to retrieve profile details. If a contact with the member details do not
     * exist in onCourse, a contact is created
     * @param membershipProduct membership the contact will recieve
     * @param context context ObjectContext used for cayenne.query.ObjectSelect
     * @return
     */
    protected processMember(id, membershipProduct) {
        def xml = soapGetMemberProfile(id)

        String firstName = getPropertyValue(xml, "FirstName")
        String lastName = getPropertyValue(xml, "Surname")
        String email1 = getPropertyValue(xml, "Email1")
        String email2 = getPropertyValue(xml, "Email2")

        def contact = ObjectSelect.query(Contact)
                .where(Contact.LAST_NAME.eq(lastName))
                .and(Contact.FIRST_NAME.eq(firstName))
                .and(Contact.EMAIL.in(email1, email2))
                .selectOne(this.context)

        if (!contact) {
            contact = this.context.newObject(Contact)
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
            contact.country = country ? ObjectSelect.query(Country).where(Country.NAME.eq(country)).selectOne(this.context) : null

            this.context.commitChanges()
        }

        def membership = ObjectSelect.query(Membership)
                .where(Membership.PRODUCT.eq(membershipProduct))
                .and(Membership.CONTACT.eq(contact))
                .and(Membership.STATUS.eq(ProductStatus.ACTIVE))
                .selectOne(this.context)

        if (!membership) {
            membership = this.context.newObject(Membership)
            membership.product = this.context.localObject(membershipProduct)
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

            InvoiceLine invoiceLine = this.context.newObject(InvoiceLine)
            invoiceLine.invoice = invoice
            invoiceLine.addToProductItems(membership)
            invoiceLine.description = "${membershipProduct.name} created by micropower intergration automatically for ${contact.fullName}".toString()
            invoiceLine.sortOrder = 0
            invoiceLine.title = "${membershipProduct.name} for ${contact.fullName}".toString()
            invoiceLine.unit = StringUtils.EMPTY
            invoiceLine.quantity = BigDecimal.ONE
            invoiceLine.priceEachExTax = Money.ZERO()
            invoiceLine.discountEachExTax = Money.ZERO()
            invoiceLine.taxEach = Money.ZERO()
            invoiceLine.prepaidFeesRemaining = Money.ZERO()
            invoiceLine.account = this.context.localObject(membershipProduct.incomeAccount)

            this.context.commitChanges()
        }

    }


    /**
     * Queries micropower SOAP api for a list of profile changes from a given date
     * A profile change object contains the Id of the member profile changed
     *
     * @param fromDate get all changes after fromDate
     * @return list of change items
     */
    def soapGetMemberChangesFromDate(fromDate) {
        def client = new SOAPClient(SOAP_CLIENT)

        def resp = client.send(SOAPAction:"http://micropower.com.au/IntegrationService/GetMemberChanges") {
            header(xmlns: XML_NAME_SPACE) {
                AuthenticationHeader {
                    EncryptedIdentity(identity)
                    Signature(signature)
                }
            }
            body(xmlns: "http://micropower.com.au/IntegrationService") {
                GetMemberChanges {
                    request {
                        ClientId(clientId)
                        FromUTC(fromDate)
                    }
                }
            }
        }
        return resp
    }

    /**
     * Get member profile using id from change item
     *
     * @param memberId
     * @return memberProfile
     */
    def soapGetMemberProfile(memberId) {
        def client =  new SOAPClient(SOAP_CLIENT)

        def resp = client.send(SOAPAction: "http://micropower.com.au/IntegrationService/GetMemberProfile") {
            header(xmlns: XML_NAME_SPACE) {
                AuthenticationHeader {
                    EncryptedIdentity(identity)
                    Signature(signature)
                }
            }
            body(xmlns: XML_NAME_SPACE) {
                GetMemberProfile {
                    request {
                        ClientId(clientId)
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

    /**
     * Convenience method for traversing member profile xml and return the value for a specified property key
     *
     * @param xml
     * @param propertyKey return the value of this key
     * @return value from specified property key
     */
    def getPropertyValue(xml, propertyKey) {
        return xml.body.GetMemberProfileResponse.GetMemberProfileResult.MemberRecord.MemberFields.'**'.find { node ->
            node.name == propertyKey
        }?.value ?: null
    }
}
