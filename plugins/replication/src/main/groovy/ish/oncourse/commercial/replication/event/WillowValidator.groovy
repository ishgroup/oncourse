/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.event

import com.google.inject.Inject
import groovy.transform.CompileStatic
import ish.math.Money
import ish.oncourse.commercial.replication.Replication
import ish.oncourse.commercial.replication.builders.IAngelStubBuilder
import ish.oncourse.commercial.replication.modules.ISoapPortLocator
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.api.v1.model.CheckoutModelDTO
import ish.oncourse.server.api.v1.model.CheckoutValidationErrorDTO
import ish.oncourse.server.api.v1.model.SaleTypeDTO
import ish.oncourse.server.cayenne.Voucher
import ish.oncourse.webservices.util.GenericParameterEntry
import ish.oncourse.webservices.util.GenericParametersMap
import ish.oncourse.webservices.util.PortHelper
import ish.oncourse.webservices.v23.stubs.replication.ParametersMap
import ish.oncourse.webservices.v23.stubs.replication.TransactionGroup
import ish.oncourse.webservices.v23.stubs.replication.VoucherStub
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.SelectQuery
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@CompileStatic
class WillowValidator {

    @Inject
    private ISoapPortLocator soapPortLocator

    @Inject
    private IAngelStubBuilder stubBuilder

    @Inject
    private ICayenneService cayenneService

    private static final Logger logger = LogManager.getLogger(WillowValidator)

    List<CheckoutValidationErrorDTO> validate(CheckoutModelDTO model) {
        Map <Long, List<Long>> classContacts = [:]
        List<CheckoutValidationErrorDTO> errors = []

        model.contactNodes.each { node ->
            node.enrolments.each { enrolment ->
                if (classContacts[enrolment.classId] != null) {
                    classContacts[enrolment.classId] << node.contactId
                } else {
                    classContacts[enrolment.classId] = [node.contactId]
                }
            }
        }
        validateClassContacts(classContacts, errors)

        if (!model.redeemedVouchers.isEmpty()) {
            List<Long> vouchersIds = model.redeemedVouchers.collect { key, value -> Long.valueOf(key) }
            validateVouchers(vouchersIds, errors)
        }

        return errors
    }


    private void validateClassContacts(Map <Long, List<Long>> classContacts, List<CheckoutValidationErrorDTO> errors) {
        GenericParametersMap parametersMap = PortHelper.createParametersMap(Replication.ACTUAL_REPLICATION_VERSION)

        classContacts.each { k, v ->
            GenericParameterEntry entry = PortHelper.createParameterEntry(Replication.ACTUAL_REPLICATION_VERSION)
            entry.name = k
            entry.value = v.join(',')
            parametersMap.genericEntry << entry
        }
        GenericParametersMap response

        try {
            response = soapPortLocator.paymentPort().verifyCheckout(parametersMap as ParametersMap)
        } catch (Exception e) {
            logger.error("Cannot contact willow side")
            logger.catching(e)
            errors << new CheckoutValidationErrorDTO( error: "There was an error processing this enrolment, possibly because of a problem with your internet connection. \n" +
                    "This means that the number of remaining places in the classes could not be checked against recent web enrolments")
            return
        }

        if (response != null && response.genericEntry != null && !response.genericEntry.empty) {
            response.genericEntry.each { entry ->
                Long classId = Long.valueOf(entry.name.split('-')[0])
                Long contactId = Long.valueOf(entry.name.split('-')[1])
                errors << new CheckoutValidationErrorDTO(nodeId: contactId, itemId: classId, itemType: SaleTypeDTO.CLASS, error: entry.value)
            }
        }
    }


    private void validateVouchers(List<Long> voucherIds, List<CheckoutValidationErrorDTO> errors) {
        ObjectContext context = cayenneService.getNewContext()
        List<Voucher> vouchers = context.select(SelectQuery.query(Voucher.class, Voucher.ID.in(voucherIds)))

        if (voucherIds.size() != vouchers.size()) {
            errors << new CheckoutValidationErrorDTO(propertyName: "redeemedVouchers", error: "Some of the passed voucher ids don't match any record in the db.")
        }

        TransactionGroup vouchersGroup = new TransactionGroup()
        vouchers.each { voucher ->
            vouchersGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(stubBuilder.convert(voucher))
        }

        TransactionGroup response
        try {
            response = soapPortLocator.paymentPort().getVouchers(vouchersGroup)
        } catch (Exception e) {
            logger.error("Cannot contact willow side")
            logger.catching(e)
            errors << new CheckoutValidationErrorDTO( error: "Selected vouchers cannot be validated, possibly because of a problem with your internet connection.\n" +
                    " Please try again later.")
            return
        }

        List<VoucherData> voucherDataList = new ArrayList<>()
        response.getGenericAttendanceOrBinaryDataOrBinaryInfo().each { stub ->
            if (stub instanceof VoucherStub) {
                VoucherStub voucherStub = stub as VoucherStub
                VoucherData voucherData = new VoucherData()
                voucherData.id = voucherStub.angelId
                voucherData.redemptionValue = Money.valueOf(voucherStub.redemptionValue)
                voucherData.enrolmentsUsed = voucherStub.redeemedCoursesCount

                voucherDataList.add(voucherData)
            }
        }

        vouchers.each { voucher ->
            VoucherData voucherData = voucherDataList.find { it.id == voucher.id }
            if (voucherData) {
                if (voucher.moneyVoucher) {
                    if (voucher.redemptionValue != voucherData.redemptionValue) {
                        errors << new CheckoutValidationErrorDTO(nodeId: voucher.id, propertyName: "redeemedVouchers", error: "Voucher is inconsistent with willow.")
                    }
                } else {
                    if (voucher.redeemedCourseCount != voucherData.enrolmentsUsed) {
                        errors << new CheckoutValidationErrorDTO(nodeId: voucher.id, propertyName: "redeemedVouchers", error: "Voucher is inconsistent with willow.")
                    }
                }
            } else {
                if (voucher.willowId) {
                    errors << new CheckoutValidationErrorDTO(nodeId: voucher.id, propertyName: "redeemedVouchers", error: "Voucher is currently in use by willow.")
                }
            }
        }
    }
}
