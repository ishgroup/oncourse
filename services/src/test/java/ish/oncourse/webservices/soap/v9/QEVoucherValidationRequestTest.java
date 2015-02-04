/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.soap.v9;

import ish.oncourse.model.Voucher;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.ServiceTest;
import ish.oncourse.webservices.util.GenericReplicationStub;
import ish.oncourse.webservices.util.GenericTransactionGroup;
import ish.oncourse.webservices.util.PortHelper;
import ish.oncourse.webservices.v9.stubs.replication.VoucherStub;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SelectQuery;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class QEVoucherValidationRequestTest extends RealWSTransportTest {

	@Before
	public void setup() throws Exception {
		serviceTest = new ServiceTest();
		serviceTest.initTest("ish.oncourse.webservices", "services", PaymentServiceTestModule.class);
		tester = serviceTest.getPageTester();
		InputStream st = RealWSTransportTest.class.getClassLoader().getResourceAsStream(getDataSetFile());
		FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
		builder.setColumnSensing(true);
		FlatXmlDataSet dataSet = builder.build(st);
		ReplacementDataSet replacementDataSet = new ReplacementDataSet(dataSet);
		replacementDataSet.addReplacementObject("[null]", null);
		DataSource onDataSource = ServiceTest.getDataSource("jdbc/oncourse");
		DatabaseConnection dbConnection = new DatabaseConnection(onDataSource.getConnection(), null);
		dbConnection.getConfig().setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, false);
		DatabaseOperation.CLEAN_INSERT.execute(dbConnection, replacementDataSet);

		this.cayenneService = serviceTest.getService(ICayenneService.class);
	}

	@Override
	protected String getDataSetFile() {
		return "ish/oncourse/webservices/soap/QEVoucherValidationRequestTestDateSet.xml";
	}

	@Test
	public void testGetVouchersRequest() throws Exception {
		ObjectContext context = cayenneService.newNonReplicatingContext();

		Voucher voucherInUse = Cayenne.objectForPK(context, Voucher.class, 1);
		assertTrue(voucherInUse.isInUse());

		List<Voucher> allVouchers = context.performQuery(new SelectQuery(Voucher.class));
		assertEquals(4, allVouchers.size());

		GenericTransactionGroup request = PortHelper.createTransactionGroup(getSupportedVersion());

		for (Voucher voucher : allVouchers) {
			request.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(buildRequestStub(voucher));
		}
		assertEquals(4, request.getGenericAttendanceOrBinaryDataOrBinaryInfo().size());

		authenticate();

		GenericTransactionGroup response = getPaymentPortType().getVouchers(castGenericTransactionGroup(request));
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
