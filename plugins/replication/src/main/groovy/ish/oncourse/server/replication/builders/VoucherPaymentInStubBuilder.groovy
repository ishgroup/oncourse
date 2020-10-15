package ish.oncourse.server.replication.builders

import ish.oncourse.server.cayenne.VoucherPaymentIn
import ish.oncourse.webservices.v21.stubs.replication.VoucherPaymentInStub

/**
 */
class VoucherPaymentInStubBuilder extends AbstractAngelStubBuilder<VoucherPaymentIn, VoucherPaymentInStub> {

    @Override
    protected VoucherPaymentInStub createFullStub(VoucherPaymentIn entity) {
        def stub = new VoucherPaymentInStub()

        stub.setCreated(entity.getCreatedOn())
        stub.setModified(entity.getModifiedOn())

        stub.setEnrolmentsCount(entity.getEnrolmentsCount())
        stub.setPaymentInId(entity.getPaymentIn().getId())
        stub.setVoucherId(entity.getVoucher().getId())
        stub.setStatus(entity.getStatus() != null ? entity.getStatus().getDatabaseValue() : null)
		stub.setInvoiceLineId(entity.getInvoiceLine() != null ? entity.getInvoiceLine().getId() : null)
        return stub
    }
}
