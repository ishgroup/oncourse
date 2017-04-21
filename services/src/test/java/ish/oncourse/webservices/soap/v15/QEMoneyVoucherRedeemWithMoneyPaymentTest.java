package ish.oncourse.webservices.soap.v15;

import ish.common.types.VoucherPaymentStatus;
import ish.oncourse.webservices.util.GenericParametersMap;
import ish.oncourse.webservices.util.GenericReplicationStub;
import ish.oncourse.webservices.util.GenericTransactionGroup;
import ish.oncourse.webservices.v15.stubs.replication.VoucherPaymentInStub;

import java.util.Date;
import java.util.List;

public abstract class QEMoneyVoucherRedeemWithMoneyPaymentTest extends QEVoucherRedeemWithMoneyPaymentGUITest {

	protected GenericTransactionGroup preparePaymentStructureForMoneyVoucherAndMoneyPayment(GenericTransactionGroup transaction, GenericParametersMap parametersMap) {
		preparePaymentStructureForTwoEnrolmentsWithoutVoucher(transaction, parametersMap);
		List<GenericReplicationStub> stubs = transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo();
		final Date current = new Date();

		VoucherPaymentInStub voucherPaymentInStub = new VoucherPaymentInStub();
		voucherPaymentInStub.setAngelId(1l);
		voucherPaymentInStub.setCreated(current);
		voucherPaymentInStub.setModified(current);
		voucherPaymentInStub.setEntityIdentifier(VOUCHER_PAYMENT_IN_IDENTIFIER);
		voucherPaymentInStub.setEnrolmentsCount(1);
		voucherPaymentInStub.setPaymentInId(2l);
		voucherPaymentInStub.setVoucherId(4l);
		voucherPaymentInStub.setStatus(VoucherPaymentStatus.APPROVED.getDatabaseValue());
		stubs.add(voucherPaymentInStub);

		return transaction;
	}
}
