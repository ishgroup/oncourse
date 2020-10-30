/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.handler

import com.google.inject.Inject
import ish.math.Money
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.Voucher
import ish.oncourse.server.modules.ISoapPortLocator
import ish.oncourse.server.replication.builders.IAngelStubBuilder
import ish.oncourse.webservices.soap.v22.ReplicationFault
import ish.oncourse.webservices.v22.stubs.replication.TransactionGroup
import ish.oncourse.webservices.v22.stubs.replication.VoucherStub
import ish.voucher.VoucherData
import ish.voucher.VoucherValidationRequest
import ish.voucher.VoucherValidationResult
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.SelectQuery
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class VoucherValidationService  implements IVoucherValidationService {

    private static final Logger logger = LogManager.getLogger()

    private ISoapPortLocator portLocator

    private IAngelStubBuilder stubBuilder

    private ICayenneService cayenneService

    @Inject
    VoucherValidationService(ISoapPortLocator soapPortLocator, IAngelStubBuilder stubBuilder, ICayenneService cayenneService) {
        this.portLocator = soapPortLocator
        this.stubBuilder = stubBuilder
        this.cayenneService = cayenneService
    }

    @Override
    VoucherValidationResult getVouchers(VoucherValidationRequest request) {
        def port = portLocator.paymentPort()

        try {
            def response = port.getVouchers(createVoucherRequestTransaction(request.getVoucherIds()))

            def result = new VoucherValidationResult()
            result.setVoucherData(parseResponseTransactionGroup(response))

            return result
        } catch (ReplicationFault e) {
            logger.error("Voucher validation request to willow failed.", e)
            throw new RuntimeException("Voucher validation request to willow failed.", e)
        }
    }

    private TransactionGroup createVoucherRequestTransaction(Collection<Long> voucherIds) {

        ObjectContext context = cayenneService.getNewContext()

        def vouchers = context.select(SelectQuery.query(Voucher.class, Voucher.ID.in(voucherIds)))

        if (voucherIds.size() != vouchers.size()) {
            throw new IllegalStateException("Some of the passed voucher ids don't match any record in the db.")
        }

        def transactionGroup = new TransactionGroup()

        for (def voucher : vouchers) {
            transactionGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(stubBuilder.convert(voucher))
        }

        return transactionGroup
    }

    private List<VoucherData> parseResponseTransactionGroup(TransactionGroup response) {

        List<VoucherData> voucherDataList = new ArrayList<>()

        for (def stub : response.getGenericAttendanceOrBinaryDataOrBinaryInfo()) {
            if (stub instanceof VoucherStub) {
                def voucherStub = (VoucherStub) stub

                def voucherData = new VoucherData()
                voucherData.setId(voucherStub.getAngelId())
                voucherData.setRedemptionValue(Money.valueOf(voucherStub.getRedemptionValue()))
                voucherData.setEnrolmentsUsed(voucherStub.getRedeemedCoursesCount())

                voucherDataList.add(voucherData)
            }
        }

        return voucherDataList
    }
}
