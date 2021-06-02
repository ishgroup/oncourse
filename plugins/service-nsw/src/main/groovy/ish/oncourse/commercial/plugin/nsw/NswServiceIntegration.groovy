/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.plugin.nsw

import groovy.transform.CompileDynamic
import groovyx.net.http.RESTClient
import ish.common.types.DataType
import ish.math.Money
import ish.oncourse.server.cayenne.CustomFieldType
import ish.oncourse.server.cayenne.IntegrationConfiguration
import ish.oncourse.server.cayenne.PaymentInLine
import ish.oncourse.server.cayenne.Voucher
import ish.oncourse.server.cayenne.VoucherCustomField
import ish.oncourse.server.integration.OnSave
import ish.oncourse.server.integration.Plugin
import ish.oncourse.server.integration.PluginTrait
import ish.util.DateFormatter
import org.apache.cayenne.ObjectContext
import org.apache.commons.lang.StringUtils
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.Method.POST
import static ish.oncourse.server.api.v1.function.CustomFieldFunctions.updateCustomFields

@CompileDynamic
@Plugin(type = 18, oneOnly = true)
class NswServiceIntegration implements PluginTrait {

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

    private String pin
    private Float amount
    private String voucherCode
    private String postcode
    private Voucher voucher

    private static Logger logger = LogManager.logger

    NswServiceIntegration(Map args) {
        loadConfig(args)

        this.voucherType = configuration.getIntegrationProperty("voucher").value
        this.channelCode = configuration.getIntegrationProperty("chanelCode").value
        this.posTerminalId = configuration.getIntegrationProperty("terminalId").value
        this.programme = configuration.getIntegrationProperty("programme").value
        this.apiKey = configuration.getIntegrationProperty("apiKey").value

    }

    void validate() {
        if (voucherCode) {
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
                    emailService.email {
                        subject('Сreative kids voucher is not valid')
                        content("The following voucher is not valid. Please contact the customer.\n${licenseService.currentHostName}/sale/${voucher.id}")
                        from(preferenceController.emailFromAddress)
                        to(preferenceController.emailAdminAddress)
                    }

                    throw new RuntimeException(result.toString())
                }
                response.success = { resp, result ->
                    return result
                }
            }
        }
    }

    void redeem() {
        if (voucherCode) {
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
                    throw new RuntimeException(result.toString())
                }
                response.success = { resp, result ->
                    return result
                }
            }

            voucher.redemptionValue = Money.ZERO
            Map<String, String> customFields = voucher.customFields.collectEntries {[(it.customFieldType.key) : it.value] }
            customFields[VOUCHER_REDEEMED_DATE_TYPE_KEY] = DateFormatter.formatDate(new Date())
            updateCustomFields(voucher.context, voucher, customFields, VoucherCustomField)
            voucher.context.commitChanges()
        }
    }

    void takeNecessaryVoucherInfo(Voucher voucher) {
        PaymentInLine payment = voucher.voucherPaymentsIn*.paymentIn*.paymentInLines.flatten()[0] as PaymentInLine
        this.pin = payment?.invoice?.invoiceLines[0]?.enrolment?.student?.contact?.birthDate?.collect {it ->
                    StringUtils.leftPad(it.dayOfMonth.toString(), 2, '0') + StringUtils.leftPad(it.month.value.toString(), 2, '0')
                }[0]
        this.amount = payment.amount.floatValue()
        this.postcode = payment?.invoice?.invoiceLines[0]?.enrolment?.courseClass?.room?.site?.postcode
        this.voucherCode = voucher.customFields.find {it.customFieldType.key == VOUCHER_CODE_TYPE_KEY }?.value
        this.voucher = voucher

        logger.warn("pin:$pin, amount:$amount, voucherId:$voucher.id, voucherCode:$voucherCode")
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
}
