import groovyx.net.http.ContentType
import groovyx.net.http.Method
import groovyx.net.http.RESTClient
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
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById
import org.apache.commons.lang3.StringUtils

import javax.crypto.Cipher

import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.RSAPublicKeySpec
import java.time.LocalDate
import java.time.format.DateTimeFormatter

//--------------------------------------------------------------
membershipProductId = 222  //set membership product Id
//--------------------------------------------------------------
String signature = 'ish'
String identity = 'knVy3Q5e5NXqFQm68hJPIgzJRmBVmbNP5nuZ3pTT'
clubId = '8218450'
encryptingData = "${signature}:${identity}:${clubId}".toString()

modulusString = '0gUU0AmpFmqte4Ei6jztrcnqwQzXYqkn+xRPZiAs8zoDQasx/JpL5PSLnB8wNcQMlj+l4LIAwTt7ex75pRafvfUhEndkM7fuoaVprF6q470BtddgnsxEiGjGeC0ylv/3ldRs3xRqc4henDRswoeOH6uZUVmPIyYZO02k8zsGd/c='
publicExponentString = 'AQAB'

baseUrl = 'https://mpsapi.micropower.com.au'
//--------------------------------------------------------------

encryption = {
    byte[] modulusBytes = Base64.decoder.decode(modulusString)
    byte[] exponentBytes = Base64.decoder.decode(publicExponentString)

    BigInteger modulus = new BigInteger(1, modulusBytes)
    BigInteger publicExponent = new BigInteger(1, exponentBytes)

    RSAPublicKeySpec rsaPubKey = new RSAPublicKeySpec(modulus, publicExponent)

    KeyFactory fact = KeyFactory.getInstance("RSA")

    PublicKey pubKey = fact.generatePublic(rsaPubKey)

    Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING")
    cipher.init(Cipher.ENCRYPT_MODE, pubKey)

    byte[] plainBytes = encryptingData.getBytes("UTF-8")
    byte[] cipherData = cipher.doFinal(plainBytes)
    byte[] encode = Base64.encoder.encode(cipherData)
    new String(encode, StandardCharsets.ISO_8859_1)
}

members = {
    RESTClient httpClient = new RESTClient(baseUrl)
    httpClient.headers['Authorization'] = "Basic ${encryption()}".toString()

    httpClient.request(Method.GET, ContentType.JSON) {
        uri.path = "/v1/data/clubs/${clubId}/members".toString()

        response.success = { resp, result ->
            result
        }
    }
}

def run(args) {
    ObjectContext context = args.context
    Date today = new Date()

    MembershipProduct membershipProduct = SelectById.query(MembershipProduct, membershipProductId)
            .selectOne(context)

    if (membershipProduct) {
        members().findAll { map -> map.fields }
                .each { map ->
            String lastName = map.fields.find {it -> it.name == 'Surname'}.value
            String firstName = map.fields.find {it -> it.name == 'FirstName'}.value
            String email1 = map.fields.find { it -> it.name == 'Email1'}.value
            String email2 = map.fields.find { it -> it.name == 'Email2'}.value

            Contact contact = ObjectSelect.query(Contact)
                    .where(Contact.LAST_NAME.eq(lastName))
                    .and(Contact.FIRST_NAME.eq(firstName))
                    .and(Contact.EMAIL.in(email1, email2))
                    .selectFirst(context)
            if (!contact) {
                contact = context.newObject(Contact)
                contact.lastName = lastName
                contact.firstName = firstName
                contact.email = email1
                contact.title = map.fields.find {it -> it.name == 'Title'}.value
                String dateOfBirth = map.fields.find { it -> it.name == 'DOB' }.value
                contact.birthDate = dateOfBirth ? LocalDate.parse(dateOfBirth, DateTimeFormatter.ofPattern('dd/MM/yyyy')) : null
                contact.mobilePhone = map.fields.find {it -> it.name == 'Mobile'}.value
                contact.suburb = map.fields.find {it -> it.name == 'Suburb'}.value
                contact.state = map.fields.find {it -> it.name == 'State'}.value
                contact.postcode = map.fields.find {it -> it.name == 'PostCode'}.value
                String country = map.fields.find { it -> it.name == 'Country' }.value
                contact.country = country ? ObjectSelect.query(Country).where(Country.NAME.eq(country)).selectFirst(context) : null

                context.commitChanges()
            }

            Membership membership = ObjectSelect.query(Membership)
                    .where(Membership.PRODUCT.eq(membershipProduct))
                    .and(Membership.CONTACT.eq(contact))
                    .and(Membership.STATUS.eq(ProductStatus.ACTIVE))
                    .selectOne(context)

            if (!membership) {
                membership = context.newObject(Membership)
                membership.product = membershipProduct
                membership.contact = contact
                membership.status = ProductStatus.ACTIVE
                membership.expiryDate = ProductUtil.calculateExpiryDate(today, membershipProduct.expiryType, membershipProduct.expiryDays)
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
    }
}
