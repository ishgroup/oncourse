/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.soap.v15;

import ish.oncourse.model.Voucher;
import ish.oncourse.webservices.util.GenericReplicationStub;
import ish.oncourse.webservices.util.GenericTransactionGroup;
import ish.oncourse.webservices.util.PortHelper;
import ish.oncourse.webservices.v15.stubs.replication.TransactionGroup;
import ish.oncourse.webservices.v15.stubs.replication.VoucherStub;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class QEVoucherValidationRequestTest extends RealWSTransportTest {
	private static final String DATASET_XML = "ish/oncourse/webservices/soap/QEVoucherValidationRequestTestDateSet.xml";

	@Before
	public void before() throws Exception {
		testEnv = new V15TestEnv(DATASET_XML, null);
		testEnv.start();
	}

	@Test
	public void testGetVouchersRequest() throws Exception {
		ObjectContext context = testEnv.getTestEnv().getCayenneService().newNonReplicatingContext();

		Voucher voucherInUse = Cayenne.objectForPK(context, Voucher.class, 1);
		assertTrue(voucherInUse.isInUse());

		List<Voucher> allVouchers = ObjectSelect.query(Voucher.class)
				.select(context);
		assertEquals(4, allVouchers.size());

		GenericTransactionGroup request = PortHelper.createTransactionGroup(testEnv.getTestEnv().getSupportedVersion());

		for (Voucher voucher : allVouchers) {
			request.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(buildRequestStub(voucher));
		}
		assertEquals(4, request.getGenericAttendanceOrBinaryDataOrBinaryInfo().size());

		testEnv.getTestEnv().authenticate();

		GenericTransactionGroup response = ((PaymentPortType) testEnv.getTestEnv().getTransportConfig().getPaymentPortType()).getVouchers((TransactionGroup) testEnv.getTestEnv().getTransportConfig().castGenericTransactionGroup(request));
		assertEquals(3, response.getGenericAttendanceOrBinaryDataOrBinaryInfo().size());

		Map<Long, VoucherStub> reponseStubsMap = new HashMap<>();

		for (GenericReplicationStub stub : response.getGenericAttendanceOrBinaryDataOrBinaryInfo()) {
			assertTrue(stub instanceof VoucherStub);
			reponseStubsMap.put(stub.getWillowId(), (VoucherStub) stub);
		}

		// check that voucher which is currently in use is not included into the response
		assertNull(reponseStubsMap.get(voucherInUse.getId()));

		allVouchers.remove(voucherInUse);

		// check that all vouchers not currently in use are in the response with correct values
		for (Voucher voucher : allVouchers) {

			VoucherStub stub = reponseStubsMap.get(voucher.getId());

			assertEquals(voucher.getAngelId(), stub.getAngelId());
			assertEquals(voucher.getRedemptionValue() != null ?
					voucher.getRedemptionValue().toBigDecimal() : null, stub.getRedemptionValue());
			assertEquals(voucher.getRedeemedCoursesCount(), stub.getRedeemedCoursesCount());
		}
	}

	private GenericReplicationStub buildRequestStub(Voucher voucher) {
		VoucherStub stub = new VoucherStub();
		stub.setEntityIdentifier(voucher.getObjectId().getEntityName());
		stub.setWillowId(voucher.getId());
		stub.setAngelId(voucher.getAngelId());

		return stub;
	}
}
