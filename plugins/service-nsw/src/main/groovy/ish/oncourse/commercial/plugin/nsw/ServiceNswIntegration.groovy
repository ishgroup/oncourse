/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.plugin.nsw

import groovy.transform.CompileDynamic
import groovyx.net.http.RESTClient
import ish.common.types.DataType
import ish.oncourse.commercial.plugin.nsw.ServiceNswException
import ish.oncourse.server.cayenne.*
import ish.oncourse.server.integration.OnSave
import ish.oncourse.server.integration.Plugin
import ish.oncourse.server.integration.PluginTrait
import ish.util.DateFormatter
import org.apache.cayenne.ObjectContext
import org.apache.commons.lang.StringUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.time.LocalDate

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.Method.POST
import static ish.oncourse.server.api.v1.function.CustomFieldFunctions.updateCustomFields

@CompileDynamic
@Plugin(type = 18, oneOnly = true)
class ServiceNswIntegration implements PluginTrait {

    private static boolean isTest = false

    private static final String VOUCHER_CODE_TYPE_KEY = "serviceNswVoucher"
    private static final String VOUCHER_REDEEMED_DATE_TYPE_KEY = "serviceNswRedeemedOn"

    private static String INVALID_VOUCHER_TEMPLATE = """
A Service NSW voucher entered into onCourse is not valid. The error message from Service NSW is:

    %s

Voucher: %s
VoucherCode: %s\n
"""

    private static String PINS_ARE_NOT_APPROPRIATE_TEMPLATE = "The following students weren't a match for the voucher code above:\n"

    private static String STUDENT_INFO_TEMPLATE = """
Student: %s
PIN: %s\n
"""

    static final String URL_TEST = "https://api-psm.g.testservicensw.net"
    static final String URL = "https://api.g.service.nsw.gov.au"
    static final String BASE_URL = isTest ? URL_TEST : URL

    String voucherType
    String channelCode
    String posTerminalId
    String programme
    String apiKey

    private List<ApiData> apiData
    private Float amount
    private String voucherCode
    private String emailAddress
    private Voucher voucher

    private static Logger logger = LogManager.logger

    ServiceNswIntegration(Map args) {
        loadConfig(args)

        this.voucherType = configuration.getIntegrationProperty("voucher").value
        this.channelCode = configuration.getIntegrationProperty("chanelCode").value
        this.posTerminalId = configuration.getIntegrationProperty("terminalId").value
        this.programme = configuration.getIntegrationProperty("programme").value
        this.apiKey = configuration.getIntegrationProperty("apiKey").value

        apiData = []
    }

    void validate() {
        String result = null
        apiData.collect { it.pin }.toSet().each {pin ->
            logger.warn("pin:$pin, voucherCode:$voucherCode, voucherId:$voucher.id")
            result = sendValidateRequest(pin)
            if (result == "Voucher is valid.") {
                return
            } else if (result != "Invalid pin.") {
                sendEmail(getStandartErrorMessage(result))
            }
        }
        if (result == "Invalid pin.") {
            sendEmail(invalidPinErrorMessage)
        }
    }

    private String sendValidateRequest(String pin) {
        new RESTClient(BASE_URL).request(POST, JSON) {
            uri.path = '/voucher-management/checkBalance'
            headers.'x-api-key' = apiKey

            body = [
                    pin: pin,
                    voucherCode: voucherCode,
                    voucherType: voucherType
            ]

            response.failure = { resp, result ->
                logger.error("Bad Request. Voucher: ${voucher.id}; Error: ${result.toString()}")
                if (resp.getStatusLine().getStatusCode() == 400) {
                    return result['message'].toString()
                }
                throw new ServiceNswException(result.toString())
            }
            response.success = { resp, result ->
                return "Voucher is valid."
            }
        }
    }

    void redeem() {
        def result = null
        apiData.each {data ->
            result = sendRedeemRequest(data.pin, data.postcode)
            if (result == "Voucher was redeemed.") {
                return
            } else if (result != "Invalid pin.") {
                sendEmail(getStandartErrorMessage(result))
            }
        }

        if (result == "Invalid pin.") {
            sendEmail(invalidPinErrorMessage)
        } else {
            Map<String, String> customFields = voucher.customFields.collectEntries {[(it.customFieldType.key) : it.value] }
            customFields[VOUCHER_REDEEMED_DATE_TYPE_KEY] = DateFormatter.formatDate(new Date())
            updateCustomFields(voucher.context, voucher, customFields, VoucherCustomField)
            voucher.context.commitChanges()
        }
    }

    private String sendRedeemRequest(String pin, postcode) {
        new RESTClient(BASE_URL).request(POST, JSON) {
            uri.path = '/voucher-management/redeem'
            headers.'x-api-key' = apiKey

            body = [
                    pin: pin,
                    voucherCode: voucherCode,
                    voucherType: voucherType,
                    amount: amount,
                    storeChannelCode: channelCode,
                    posTerminalId: posTerminalId,
                    program: programme,
                    programLocation: postcode
            ]

            response.failure = { resp, result ->

                logger.error("Bad Request. Voucher: ${voucher.id}; Error: ${result.toString()}")
                if (resp.getStatusLine().getStatusCode() == 400) {
                    return result['message'].toString()
                }
                throw new ServiceNswException(result.toString())
            }
            response.success = { resp, result ->
                return "Voucher was redeemed."
            }
        }
    }

    private void interruptException(String message) {
        sendEmail(getStandartErrorMessage(message))
        throw new ServiceNswException(message)
    }

    private String getStandartErrorMessage(String exceptionMessage) {
        String.format(INVALID_VOUCHER_TEMPLATE, exceptionMessage, "${licenseService.currentHostName}/sale/$voucher.id", voucherCode)
    }

    private String getInvalidPinErrorMessage() {
        StringBuilder message = new StringBuilder(getStandartErrorMessage('The PIN is not valid.'))
                .append(PINS_ARE_NOT_APPROPRIATE_TEMPLATE)
        apiData.each {data ->
            message.append(String.format(STUDENT_INFO_TEMPLATE, "${licenseService.currentHostName}/contact/$apiData.contactId", apiData.pin))
        }
        message.toString()
    }

    private void sendEmail(String message) {
        emailService.email {
            subject('Сreative kids voucher is not valid')
            content(message)
            from(preferenceController.emailFromAddress)
            to(emailAddress)
        }
    }

    boolean init(Voucher voucher, String emailAddress) {
        if (voucher.getCustomFieldValue("serviceNswVoucher") && voucher.getCustomFieldValue("serviceNswRedeemedOn") == null) {
            this.voucherCode = voucher.getCustomFieldValue("serviceNswVoucher")
            this.voucher = voucher
            this.emailAddress = emailAddress ?: preferenceController.emailAdminAddress

            PaymentInLine paymentInLine = voucher.voucherPaymentsIn*.paymentIn*.paymentInLines.flatten()[0] as PaymentInLine
            if (paymentInLine) {
                paymentInLine.invoice.invoiceLines
                        .findAll {it.enrolment != null } //
                        .each { invoiceLine ->
                            Contact contact = invoiceLine.enrolment.student.contact
                            LocalDate birthDate = contact.birthDate
                            if (birthDate == null) {
                                sendEmail("Birthday date wasn't specified. Please contact the customer.\n" +
                                        "${licenseService.currentHostName}/contact/${invoiceLine.enrolment.student.contact.id}")
                                throw new ServiceNswException("Birthday date wasn't specified. ContactId: ${invoiceLine.enrolment.student.contact.id}")
                            }
                            String pin = birthDate.collect { it ->
                                StringUtils.leftPad(it.dayOfMonth.toString(), 2, '0') + StringUtils.leftPad(it.month.value.toString(), 2, '0')
                            }.first()
                            String postCode = invoiceLine.enrolment.courseClass.room.site.postcode

                            apiData << new ApiData(contact.id, pin, postCode)
                        }
                setAmount(paymentInLine.amount.floatValue())
            } else {
                setAmount(voucher.valueRemaining.floatValue())
            }

            return true
        }
        return false
    }

    @OnSave
    static void createRequiredCustomFieldTypes(IntegrationConfiguration configuration, Map<String,String> props) {
        ObjectContext context = configuration.context
        props.each { key, value->
            configuration.setProperty(key, value)
        }
        createVoucherCodeCustomFieldType(context)
        createVoucherRedeemedOnCustomFieldType(context)
    }

    private static void createVoucherCodeCustomFieldType(ObjectContext context) {
        context.newObject(CustomFieldType).with {type ->
            type.key = VOUCHER_CODE_TYPE_KEY
            type.name = "Creative Kids Voucher Number"
            type.entityIdentifier = Voucher.class.simpleName
            type.dataType = DataType.PATTERN_TEXT
            type.pattern = "9[0-9a-zA-Z]{15}"
            type.isMandatory = false
        }
    }

    private static void createVoucherRedeemedOnCustomFieldType(ObjectContext context) {
        context.newObject(CustomFieldType).with {type ->
            type.key = VOUCHER_REDEEMED_DATE_TYPE_KEY
            type.name = "Service NSW Redeemed On"
            type.entityIdentifier = Voucher.class.simpleName
            type.dataType = DataType.DATE_TIME
            type.isMandatory = false
        }
    }

    private void setAmount(float amount) {
        if (amount < 100f) {
            amount = 100f
        }
        this.amount = amount
    }

    String getVoucherCode() {
        return this.voucherCode
    }

    private class ApiData {
        Long contactId
        String pin
        String postcode

        ApiData(Long contactId, String pin, String postcode) {
            this.contactId = contactId
            this.pin = pin
            this.postcode = postcode
        }
    }
}
