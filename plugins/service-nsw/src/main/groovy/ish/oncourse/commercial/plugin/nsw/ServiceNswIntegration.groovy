/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.plugin.nsw

import groovy.transform.CompileDynamic
import groovyx.net.http.RESTClient
import ish.common.types.DataType
import ish.oncourse.server.cayenne.*
import ish.oncourse.server.integration.OnSave
import ish.oncourse.server.integration.Plugin
import ish.oncourse.server.integration.PluginTrait
import ish.util.DateFormatter
import org.apache.cayenne.ObjectContext
import org.apache.commons.lang.StringUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.Method.POST
import static ish.oncourse.server.api.v1.function.CustomFieldFunctions.updateCustomFields

@CompileDynamic
@Plugin(type = 18, oneOnly = true)
class ServiceNswIntegration implements PluginTrait {

    private static boolean isTest = false

    private static final String VOUCHER_CODE_TYPE_KEY = "serviceNswVoucher"
    private static final String VOUCHER_REDEEMED_DATE_TYPE_KEY = "serviceNswRedeemedOn"

    static final String URL_TEST = "https://api-psm.g.testservicensw.net"
    static final String URL = "https://api.g.service.nsw.gov.au"
    static final String BASE_URL = isTest ? URL_TEST : URL

    String voucherType
    String channelCode
    String posTerminalId
    String programme
    String apiKey

    private Map<String, String> pinPostcode
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
        def result = null
        apiData.collect { it.pin }.toSet().each {pin ->
            logger.warn("pin:$pin, voucherCode:$voucherCode, voucherId:$voucher.id")
            try {
                result = sendValidateRequest(pin)
                return
            } catch (ServiceNswException ex) {
                if (ex.message != 'Invalid Pin.') {
                    interruptException(ex.message)
                }
            }
        }
        if (!result) {
            interruptException('Invalid Pin.')
        }
    }

    private void sendValidateRequest(String pin) {
        new RESTClient(BASE_URL).request(POST, JSON) {
            uri.path = '/voucher-management/checkBalance'
            headers.'x-api-key' = apiKey

            body = [
                    pin: pin,
                    voucherCode: voucherCode,
                    voucherType: voucherType
            ]

            response.failure = { resp, result ->
                logger.error(result.toString())
                throw new ServiceNswException(result['message'].toString())
            }
            response.success = { resp, result ->
                return result
            }
        }
    }

    void redeem() {
        def result = null
        apiData.each {data ->
            try {
                result = sendRedeemRequest(data.pin, data.postcode)
                return
            } catch (ServiceNswException ex) {
                if (ex.message != 'Invalid Pin.') {
                    interruptException(ex.message)
                }
            }
        }

        if (result) {
            Map<String, String> customFields = voucher.customFields.collectEntries {[(it.customFieldType.key) : it.value] }
            customFields[VOUCHER_REDEEMED_DATE_TYPE_KEY] = DateFormatter.formatDate(new Date())
            updateCustomFields(voucher.context, voucher, customFields, VoucherCustomField)
            voucher.context.commitChanges()
        } else {
            interruptException('Invalid Pin.')
        }
    }

    private void sendRedeemRequest(String pin, postcode) {
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

                logger.error(result.toString())
                throw new ServiceNswException(result['message'].toString())
            }
            response.success = { resp, result ->
                return result
            }
        }
    }

    private void interruptException(String message) {
        emailService.email {
            subject('Сreative kids voucher is not valid')
            content("The following voucher is not valid. Please contact the customer.\n${licenseService.currentHostName}/sale/${voucher.id}")
            from(preferenceController.emailFromAddress)
            to(emailAddress)
        }
        throw new ServiceNswException(message)
    }

    boolean init(Voucher voucher, String emailAddress) {
        if (voucher.getCustomFieldValue("serviceNswVoucher")) {
            PaymentInLine paymentInLine = voucher.voucherPaymentsIn*.paymentIn*.paymentInLines.flatten()[0] as PaymentInLine
            paymentInLine?.invoice?.invoiceLines?.each {invoiceLine ->
                String pin = invoiceLine.enrolment.student.contact.birthDate?.collect {it ->
                    StringUtils.leftPad(it.dayOfMonth.toString(), 2, '0') + StringUtils.leftPad(it.month.value.toString(), 2, '0')
                }[0]
                String postCode = invoiceLine.enrolment.courseClass.room.site.postcode

                apiData << new ApiData(pin, postCode)
            }
            this.amount = paymentInLine.amount.floatValue()
            this.voucherCode = voucher.getCustomFieldValue("serviceNswVoucher")
            this.voucher = voucher
            this.emailAddress = emailAddress ?: preferenceController.emailAdminAddress

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

    String getVoucherCode() {
        return this.voucherCode
    }

    private class ApiData {
        String pin
        String postcode

        ApiData(String pin, String postcode) {
            this.pin = pin
            this.postcode = postcode
        }
    }
}
