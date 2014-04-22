/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.voucher;

import ish.common.types.PaymentType;
import ish.common.types.ProductStatus;
import ish.math.Money;
import ish.oncourse.model.*;
import ish.oncourse.services.ServiceModule;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.test.ServiceTest;
import ish.oncourse.utils.DiscountUtils;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.PersistenceState;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SortOrder;
import org.apache.commons.lang.time.DateUtils;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author dzmitry
 */
public class VoucherRedemptionHelperTest extends ServiceTest {

    private ObjectContext context;

	@Before
	public void setup() throws Exception {

        initTest("ish.oncourse.services", "service", ServiceModule.class);

        InputStream st = VoucherServiceTest.class.getClassLoader().getResourceAsStream("ish/oncourse/services/voucher/voucherRedemptionHelperTest.xml");

        FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);
        DataSource dataSource = getDataSource("jdbc/oncourse");
        ReplacementDataSet rDataSet = new ReplacementDataSet(dataSet);
        Date start1 = DateUtils.addDays(new Date(), -4);
        Date start2 = DateUtils.addDays(new Date(), -2);
        Date start3 = DateUtils.addDays(new Date(), 2);
        Date start4 = DateUtils.addDays(new Date(), 4);
        rDataSet.addReplacementObject("[start_date1]", start1);
        rDataSet.addReplacementObject("[start_date2]", start2);
        rDataSet.addReplacementObject("[start_date3]", start3);
        rDataSet.addReplacementObject("[start_date4]", start4);
        rDataSet.addReplacementObject("[end_date1]", DateUtils.addHours(start1, 2));
        rDataSet.addReplacementObject("[end_date2]", DateUtils.addHours(start2, 2));
        rDataSet.addReplacementObject("[end_date3]", DateUtils.addHours(start3, 2));
        rDataSet.addReplacementObject("[end_date4]", DateUtils.addHours(start4, 2));
        rDataSet.addReplacementObject("[null]", null);

        DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(dataSource.getConnection(), null), rDataSet);
        context = getService(ICayenneService.class).newContext();
	}

	@Test
	public void testEmptyService() {
        College college = Cayenne.objectForPK(context, College.class, 1);

		VoucherRedemptionHelper service = new VoucherRedemptionHelper(getContext(),college);
		service.processAgainstInvoices();

		assertTrue(service.getPayments().isEmpty());
	}

	@Test
	public void testApplyCourseVoucher() {
		ObjectContext context = getContext();


        College college = Cayenne.objectForPK(context, College.class, 1);

		CourseClass cc = Cayenne.objectForPK(context, CourseClass.class, 1);

		InvoiceLine il = Cayenne.objectForPK(context, InvoiceLine.class, 2);

		Voucher courseVoucher = Cayenne.objectForPK(context, Voucher.class, 1);

		VoucherRedemptionHelper service = new VoucherRedemptionHelper(context, college);

		service.setInvoice(il.getInvoice());
		service.addVoucher(courseVoucher, courseVoucher.getValueRemaining());
		service.addInvoiceLines(Arrays.asList(il));

		service.processAgainstInvoices();

		assertFalse(service.getPayments().isEmpty());
		assertEquals(1, service.getPayments().size());

		PaymentIn payment = service.getPayments().get(0);

		assertEquals(cc.getFeeIncGst(), payment.getAmount());
		assertEquals(PaymentType.VOUCHER, payment.getType());

		context.commitChanges();
	}

	@Test
	public void testApplyCourseVoucherWithDiscount() {
		ObjectContext context = getContext();

		Discount discount = Cayenne.objectForPK(context, Discount.class, 1);

		CourseClass cc = Cayenne.objectForPK(context, CourseClass.class, 1);

		InvoiceLine il = Cayenne.objectForPK(context, InvoiceLine.class, 2);

		il.setDiscountEachExTax(DiscountUtils.discountValue(discount, cc.getFeeExGst()));

		Voucher courseVoucher = Cayenne.objectForPK(context, Voucher.class, 1);

        College college = Cayenne.objectForPK(context, College.class, 1);

        VoucherRedemptionHelper service = new VoucherRedemptionHelper(context, college);

		service.setInvoice(il.getInvoice());
		service.addVoucher(courseVoucher, courseVoucher.getValueRemaining());
		service.addInvoiceLines(Arrays.asList(il));

		service.processAgainstInvoices();

		assertFalse(service.getPayments().isEmpty());
		assertEquals(1, service.getPayments().size());

		PaymentIn payment = service.getPayments().get(0);

		assertEquals(il.getDiscountedPriceTotalIncTax(), payment.getAmount());
		assertEquals(PaymentType.VOUCHER, payment.getType());

		context.commitChanges();
	}

	@Test
	public void testApplyMoneyVoucher() {
		ObjectContext context = getContext();

		CourseClass cc = Cayenne.objectForPK(context, CourseClass.class, 1);

		InvoiceLine il = Cayenne.objectForPK(context, InvoiceLine.class, 2);

		Voucher moneyVoucher = Cayenne.objectForPK(context, Voucher.class, 2);

        College college = Cayenne.objectForPK(context, College.class, 1);

        VoucherRedemptionHelper service = new VoucherRedemptionHelper(context,college);

		service.setInvoice(il.getInvoice());
		service.addVoucher(moneyVoucher, moneyVoucher.getValueRemaining());
		service.addInvoiceLines(Arrays.asList(il));

		service.processAgainstInvoices();

		assertFalse(service.getPayments().isEmpty());
		assertEquals(1, service.getPayments().size());

		PaymentIn payment = service.getPayments().get(0);

		assertEquals(cc.getFeeIncGst(), payment.getAmount());
		assertEquals(PaymentType.VOUCHER, payment.getType());

		context.commitChanges();
	}

	@Test
	public void testApplyMultipleMoneyVouchers() {
		ObjectContext context = getContext();

		Invoice invoice = Cayenne.objectForPK(context, Invoice.class, 1);

		Voucher voucher1 = Cayenne.objectForPK(context, Voucher.class, 2);
		Voucher voucher2 = Cayenne.objectForPK(context, Voucher.class, 4);

        College college = Cayenne.objectForPK(context, College.class, 1);

        VoucherRedemptionHelper service = new VoucherRedemptionHelper(context, college);

		service.setInvoice(invoice);
		service.addVoucher(voucher1, voucher1.getValueRemaining());
		service.addVoucher(voucher2, voucher2.getValueRemaining());
		service.addInvoiceLines(invoice.getInvoiceLines());

		service.processAgainstInvoices();

		assertFalse(service.getPayments().isEmpty());
		assertEquals(2, service.getPayments().size());

		Money paymentsSum = Money.ZERO;
		for (PaymentIn p : service.getPayments().values()) {
			paymentsSum = paymentsSum.add(p.getAmount());

			assertEquals(PaymentType.VOUCHER, p.getType());
		}

		assertEquals(new Money("220.0"), paymentsSum);

		context.commitChanges();

		voucher1.setPersistenceState(PersistenceState.HOLLOW);
		voucher2.setPersistenceState(PersistenceState.HOLLOW);

		assertEquals(ProductStatus.ACTIVE, voucher2.getStatus());
		assertEquals(ProductStatus.REDEEMED, voucher1.getStatus());

		assertEquals(new Money("90.00"), voucher2.getValueRemaining());

		assertEquals(Money.ZERO, voucher1.getRedemptionValue());
	}

	// @Test
	public void testApplyMultipleVouchersToSingleInvoiceLine() {
		ObjectContext context = getContext();

		Cayenne.objectForPK(context, CourseClass.class, 1);

		InvoiceLine il1 = Cayenne.objectForPK(context, InvoiceLine.class, 2);
		InvoiceLine il2 = Cayenne.objectForPK(context, InvoiceLine.class, 11);

		Voucher voucher1 = Cayenne.objectForPK(context, Voucher.class, 4);
		Voucher voucher2 = Cayenne.objectForPK(context, Voucher.class, 5);
		Voucher voucher3 = Cayenne.objectForPK(context, Voucher.class, 6);

        College college = Cayenne.objectForPK(context, College.class, 1);

        VoucherRedemptionHelper service = new VoucherRedemptionHelper(context, college);

		service.setInvoice(il1.getInvoice());
		service.addVoucher(voucher1, voucher1.getValueRemaining());
		service.addVoucher(voucher2, voucher2.getValueRemaining());
		service.addVoucher(voucher3, voucher3.getValueRemaining());
		service.addInvoiceLines(Arrays.asList(il1, il2));

		service.processAgainstInvoices();

		assertFalse(service.getPayments().isEmpty());
		assertEquals(3, service.getPayments().size());

		Money paymentsSum = Money.ZERO;
		for (PaymentIn p : service.getPayments().values()) {
			paymentsSum = paymentsSum.add(p.getAmount());

			assertEquals(PaymentType.VOUCHER, p.getType());
		}

		assertEquals(il1.getDiscountedPriceTotalIncTax().add(il2.getDiscountedPriceTotalIncTax()), paymentsSum);

		context.commitChanges();

		voucher1.setPersistenceState(PersistenceState.HOLLOW);
		voucher2.setPersistenceState(PersistenceState.HOLLOW);
		voucher3.setPersistenceState(PersistenceState.HOLLOW);

		assertEquals(ProductStatus.REDEEMED, voucher1.getStatus());
		assertEquals(ProductStatus.REDEEMED, voucher2.getStatus());
		assertEquals(ProductStatus.ACTIVE, voucher3.getStatus());

		assertEquals(new Money("22.00"), voucher3.getRedemptionValue());
	}

	@Test
	public void testApplyMultipleCourseVouchers() {
		ObjectContext context = getContext();

		CourseClass cc = Cayenne.objectForPK(context, CourseClass.class, 1);

		InvoiceLine il1 = Cayenne.objectForPK(context, InvoiceLine.class, 2);
		InvoiceLine il2 = Cayenne.objectForPK(context, InvoiceLine.class, 3);

		Voucher voucher1 = Cayenne.objectForPK(context, Voucher.class, 1);
		Voucher voucher2 = Cayenne.objectForPK(context, Voucher.class, 3);

        College college = Cayenne.objectForPK(context, College.class, 1);

        VoucherRedemptionHelper service = new VoucherRedemptionHelper(context, college);

		service.setInvoice(il1.getInvoice());
		service.addVoucher(voucher1, voucher1.getValueRemaining());
		service.addVoucher(voucher2, voucher2.getValueRemaining());
		service.addInvoiceLines(Arrays.asList(il1, il2));

		service.processAgainstInvoices();

		assertFalse(service.getPayments().isEmpty());
		assertEquals(1, service.getPayments().size());

		PaymentIn payment1 = service.getPayments().get(0);

		assertEquals(cc.getFeeIncGst().multiply(2.0), payment1.getAmount());
		assertEquals(PaymentType.VOUCHER, payment1.getType());

		context.commitChanges();
	}

	@Test
	public void testCourseVoucherOverspending() {
		ObjectContext context = getContext();

		CourseClass cc = Cayenne.objectForPK(context, CourseClass.class, 1);

		InvoiceLine il1 = Cayenne.objectForPK(context, InvoiceLine.class, 2);
		InvoiceLine il2 = Cayenne.objectForPK(context, InvoiceLine.class, 3);

		Voucher voucher = Cayenne.objectForPK(context, Voucher.class, 7);

        College college = Cayenne.objectForPK(context, College.class, 1);

        VoucherRedemptionHelper service = new VoucherRedemptionHelper(context, college);

		service.setInvoice(il1.getInvoice());
		service.addVoucher(voucher, voucher.getValueRemaining());
		service.addInvoiceLines(Arrays.asList(il1, il2));

		service.processAgainstInvoices();

		assertFalse(service.getPayments().isEmpty());
		assertEquals(1, service.getPayments().size());

		PaymentIn payment1 = service.getPayments().get(0);

		assertEquals(cc.getFeeIncGst(), payment1.getAmount());
		assertEquals(PaymentType.VOUCHER, payment1.getType());

		context.commitChanges();
	}

	@Test
	public void testApplyCourseAndMoneyVouchers() {
		ObjectContext context = getContext();

		Cayenne.objectForPK(context, CourseClass.class, 1);

		InvoiceLine il1 = Cayenne.objectForPK(context, InvoiceLine.class, 10);
		InvoiceLine il2 = Cayenne.objectForPK(context, InvoiceLine.class, 11);

		Voucher voucher1 = Cayenne.objectForPK(context, Voucher.class, 1);
		Voucher voucher2 = Cayenne.objectForPK(context, Voucher.class, 5);
		Voucher voucher3 = Cayenne.objectForPK(context, Voucher.class, 6);

        College college = Cayenne.objectForPK(context, College.class, 1);

        VoucherRedemptionHelper service = new VoucherRedemptionHelper(context, college);

		service.setInvoice(il1.getInvoice());
		service.addVoucher(voucher1, voucher1.getValueRemaining());
		service.addVoucher(voucher2, voucher2.getValueRemaining());
		service.addVoucher(voucher3, voucher3.getValueRemaining());
		service.addInvoiceLines(Arrays.asList(il1, il2));

		service.processAgainstInvoices();

		assertFalse(service.getPayments().isEmpty());
		assertEquals(3, service.getPayments().size());

		Money paymentsSum = Money.ZERO;
		for (PaymentIn p : service.getPayments().values()) {
			paymentsSum = paymentsSum.add(p.getAmount());

			assertEquals(PaymentType.VOUCHER, p.getType());
		}

		assertEquals(il1.getDiscountedPriceTotalIncTax().add(il2.getDiscountedPriceTotalIncTax()), paymentsSum);

		context.commitChanges();

		voucher1.setPersistenceState(PersistenceState.HOLLOW);
		voucher2.setPersistenceState(PersistenceState.HOLLOW);
		voucher3.setPersistenceState(PersistenceState.HOLLOW);

		assertEquals(ProductStatus.ACTIVE, voucher1.getStatus());
		assertEquals(ProductStatus.REDEEMED, voucher2.getStatus());
		assertEquals(ProductStatus.ACTIVE, voucher3.getStatus());
	}

	@Test
	public void testDiscardChanges() {
		ObjectContext context = getContext();

		Cayenne.objectForPK(context, CourseClass.class, 1);

		InvoiceLine il1 = Cayenne.objectForPK(context, InvoiceLine.class, 2);
		InvoiceLine il2 = Cayenne.objectForPK(context, InvoiceLine.class, 11);

		Voucher voucher1 = Cayenne.objectForPK(context, Voucher.class, 1);
		Voucher voucher2 = Cayenne.objectForPK(context, Voucher.class, 5);
		Voucher voucher3 = Cayenne.objectForPK(context, Voucher.class, 6);

        College college = Cayenne.objectForPK(context, College.class, 1);

        VoucherRedemptionHelper service = new VoucherRedemptionHelper(context, college);

		service.setInvoice(il1.getInvoice());
		service.addVoucher(voucher1, voucher1.getValueRemaining());
		service.addVoucher(voucher2, voucher2.getValueRemaining());
		service.addVoucher(voucher3, voucher3.getValueRemaining());
		service.addInvoiceLines(Arrays.asList(il1, il2));

		service.processAgainstInvoices();

		assertFalse(service.getPayments().isEmpty());
		assertEquals(3, service.getPayments().size());

		Money paymentsSum = Money.ZERO;
		for (PaymentIn p : service.getPayments().values()) {
			paymentsSum = paymentsSum.add(p.getAmount());

			assertEquals(PaymentType.VOUCHER, p.getType());
		}

		assertEquals(il1.getDiscountedPriceTotalIncTax().add(il2.getDiscountedPriceTotalIncTax()), paymentsSum);

		assertEquals(1, voucher1.getRedeemedCoursesCount().intValue());

		service.clear();

		assertTrue(context.newObjects().isEmpty());
		assertTrue(service.getPayments().isEmpty());
		assertTrue(service.getVoucherPayments().isEmpty());

		assertEquals(0, voucher1.getRedeemedCoursesCount().intValue());

		assertTrue(service.getVouchers().isEmpty());

		assertEquals(ProductStatus.ACTIVE, voucher1.getStatus());
		assertEquals(ProductStatus.ACTIVE, voucher2.getStatus());
		assertEquals(ProductStatus.ACTIVE, voucher3.getStatus());

		context.commitChanges();
	}

	@Test
	public void testMoneyVoucherRedemptionAgainstPreviousInvoices() {
		ObjectContext context = getContext();

		Invoice invoice = Cayenne.objectForPK(context, Invoice.class, 1);

		Invoice previousOwing = Cayenne.objectForPK(context, Invoice.class, 12);

		Voucher voucher1 = Cayenne.objectForPK(context, Voucher.class, 2);
		Voucher voucher2 = Cayenne.objectForPK(context, Voucher.class, 4);

        College college = Cayenne.objectForPK(context, College.class, 1);

        VoucherRedemptionHelper service = new VoucherRedemptionHelper(context, college);

		service.setInvoice(invoice);
		service.addVoucher(voucher1, voucher1.getValueRemaining());
		service.addVoucher(voucher2, voucher2.getValueRemaining());

		service.addPreviousOwingInvoices(Arrays.asList(previousOwing));

		service.processAgainstInvoices();

		assertFalse(service.getPayments().isEmpty());
		assertEquals(2, service.getPayments().size());

		Money paymentsSum = Money.ZERO;
		for (PaymentIn p : service.getPayments().values()) {
			paymentsSum = paymentsSum.add(p.getAmount());

			assertEquals(PaymentType.VOUCHER, p.getType());
		}

		assertEquals(new Money("220.0"), paymentsSum);

		context.commitChanges();

		voucher1.setPersistenceState(PersistenceState.HOLLOW);
		voucher2.setPersistenceState(PersistenceState.HOLLOW);

		assertEquals(ProductStatus.ACTIVE, voucher2.getStatus());
		assertEquals(ProductStatus.REDEEMED, voucher1.getStatus());

		assertEquals(new Money("90.00"), voucher2.getValueRemaining());

		assertEquals(Money.ZERO, voucher1.getRedemptionValue());
	}

	@Test
	public void testCourseVoucherRedemptionAgainstPreviousInvoices() {
		ObjectContext context = getContext();

		Invoice invoice = Cayenne.objectForPK(context, Invoice.class, 1);
		Invoice previousOwing = Cayenne.objectForPK(context, Invoice.class, 12);

		Voucher voucher1 = Cayenne.objectForPK(context, Voucher.class, 1);
		Voucher voucher2 = Cayenne.objectForPK(context, Voucher.class, 3);

        College college = Cayenne.objectForPK(context, College.class, 1);

        VoucherRedemptionHelper service = new VoucherRedemptionHelper(context, college);

		service.setInvoice(invoice);
		service.addVoucher(voucher1, voucher1.getValueRemaining());
		service.addVoucher(voucher2, voucher2.getValueRemaining());
		service.addPreviousOwingInvoices(Arrays.asList(previousOwing));

		service.processAgainstInvoices();

		assertTrue(service.getPayments().isEmpty());

		context.commitChanges();


		previousOwing.updateAmountOwing();
		assertEquals(new Money("220.0"), previousOwing.getAmountOwing());
	}

	@Test
	public void testCourseAndMoneyVoucherRedemptionAgainstPreviousInvoices() {
		ObjectContext context = getContext();

		Cayenne.objectForPK(context, CourseClass.class, 1);

		Invoice invoice = Cayenne.objectForPK(context, Invoice.class, 10);
		Invoice previousOwing = Cayenne.objectForPK(context, Invoice.class, 12);

		Voucher voucher1 = Cayenne.objectForPK(context, Voucher.class, 1);
		Voucher voucher2 = Cayenne.objectForPK(context, Voucher.class, 5);
		Voucher voucher3 = Cayenne.objectForPK(context, Voucher.class, 6);

        College college = Cayenne.objectForPK(context, College.class, 1);

        VoucherRedemptionHelper service = new VoucherRedemptionHelper(context, college);

		service.setInvoice(invoice);
		service.addVoucher(voucher1, voucher1.getValueRemaining());
		service.addVoucher(voucher2, voucher2.getValueRemaining());
		service.addVoucher(voucher3, voucher3.getValueRemaining());

		service.addPreviousOwingInvoices(Arrays.asList(previousOwing));

		service.processAgainstInvoices();

		assertFalse(service.getPayments().isEmpty());
		assertEquals(2, service.getPayments().size());

		Money paymentsSum = Money.ZERO;
		for (PaymentIn p : service.getPayments().values()) {
			paymentsSum = paymentsSum.add(p.getAmount());

			assertEquals(PaymentType.VOUCHER, p.getType());
		}

		assertEquals(new Money("170.0"), paymentsSum);

		context.commitChanges();

		voucher1.setPersistenceState(PersistenceState.HOLLOW);
		voucher2.setPersistenceState(PersistenceState.HOLLOW);
		voucher3.setPersistenceState(PersistenceState.HOLLOW);

		assertEquals(ProductStatus.ACTIVE, voucher1.getStatus());
		assertEquals(ProductStatus.REDEEMED, voucher2.getStatus());
		assertEquals(ProductStatus.REDEEMED, voucher3.getStatus());
	}

	@Test
	public void testAllKindsOfRedemption() {
		ObjectContext context = getContext();

        Cayenne.objectForPK(context, CourseClass.class, 1);

		InvoiceLine il1 = Cayenne.objectForPK(context, InvoiceLine.class, 10);
		InvoiceLine il2 = Cayenne.objectForPK(context, InvoiceLine.class, 11);

		Invoice previousOwing1 = Cayenne.objectForPK(context, Invoice.class, 11);
		Invoice previousOwing2 = Cayenne.objectForPK(context, Invoice.class, 12);

		Voucher voucher1 = Cayenne.objectForPK(context, Voucher.class, 1);
		Voucher voucher2 = Cayenne.objectForPK(context, Voucher.class, 2);
		Voucher voucher3 = Cayenne.objectForPK(context, Voucher.class, 4);
		Voucher voucher4 = Cayenne.objectForPK(context, Voucher.class, 5);
		Voucher voucher5 = Cayenne.objectForPK(context, Voucher.class, 6);

        College college = Cayenne.objectForPK(context, College.class, 1);

        VoucherRedemptionHelper service = new VoucherRedemptionHelper(context, college);

		service.setInvoice(il1.getInvoice());
		service.addVoucher(voucher1, voucher1.getValueRemaining());
		service.addVoucher(voucher2, voucher2.getValueRemaining());
		service.addVoucher(voucher3, voucher3.getValueRemaining());
		service.addVoucher(voucher4, voucher4.getValueRemaining());
		service.addVoucher(voucher5, voucher5.getValueRemaining());

		service.addInvoiceLines(Arrays.asList(il1, il2));

		service.addPreviousOwingInvoices(Arrays.asList(previousOwing1, previousOwing2));

		service.processAgainstInvoices();

		assertFalse(service.getPayments().isEmpty());
		assertEquals(5, service.getPayments().size());

		// vocuher1 - 1 enrolment
		assertFalse(voucher1.isFullyRedeemed());
		assertEquals(1, voucher1.getVoucherPaymentIns().size());
		assertEquals(1, voucher1.getVoucherPaymentIns().get(0).getPayment().getPaymentInLines().size());
		assertEquals(il1.getDiscountedPriceTotalIncTax(),
				voucher1.getVoucherPaymentIns().get(0).getPayment().getPaymentInLines().get(0).getAmount());

		// voucher2 - $110
		assertTrue(voucher2.isFullyRedeemed());
		assertEquals(1, voucher2.getVoucherPaymentIns().size());

		List<PaymentInLine> voucher2Lines = voucher2.getVoucherPaymentIns().get(0).getPayment().getPaymentInLines();
		assertEquals(2, voucher2Lines.size());

		Ordering.orderList(voucher2Lines, Arrays.asList(new Ordering(PaymentInLine.AMOUNT_PROPERTY, SortOrder.ASCENDING)));
		assertEquals(new Money("38.0"), voucher2Lines.get(0).getAmount());
		assertEquals(new Money("72.0"), voucher2Lines.get(1).getAmount());

		// voucher3 - $200
		assertFalse(voucher3.isFullyRedeemed());
		assertEquals(1, voucher3.getVoucherPaymentIns().size());

		List<PaymentInLine> voucher3Lines = voucher3.getVoucherPaymentIns().get(0).getPayment().getPaymentInLines();
		assertEquals(1, voucher3Lines.size());
		assertEquals(new Money("138.0"), voucher3Lines.get(0).getAmount());

		assertEquals(new Money("62"), voucher3.getValueRemaining());

		// voucher4 - $50
		assertTrue(voucher4.isFullyRedeemed());
		assertEquals(1, voucher4.getVoucherPaymentIns().size());

		List<PaymentInLine> voucher4Lines = voucher4.getVoucherPaymentIns().get(0).getPayment().getPaymentInLines();
		assertEquals(1, voucher4Lines.size());
		assertEquals(new Money("50.0"), voucher4Lines.get(0).getAmount());

		// voucher5 - $120
		assertTrue(voucher5.isFullyRedeemed());
		assertEquals(1, voucher5.getVoucherPaymentIns().size());

		List<PaymentInLine> voucher5Lines = voucher5.getVoucherPaymentIns().get(0).getPayment().getPaymentInLines();
		assertEquals(2, voucher5Lines.size());

		Ordering.orderList(voucher5Lines, Arrays.asList(new Ordering(PaymentInLine.AMOUNT_PROPERTY, SortOrder.ASCENDING)));
		assertEquals(new Money("38.0"), voucher5Lines.get(0).getAmount());
		assertEquals(new Money("82.0"), voucher5Lines.get(1).getAmount());

		context.commitChanges();

		voucher1.setPersistenceState(PersistenceState.HOLLOW);
		voucher2.setPersistenceState(PersistenceState.HOLLOW);
		voucher3.setPersistenceState(PersistenceState.HOLLOW);
		voucher4.setPersistenceState(PersistenceState.HOLLOW);
		voucher5.setPersistenceState(PersistenceState.HOLLOW);

		assertEquals(ProductStatus.ACTIVE, voucher1.getStatus());
		assertEquals(ProductStatus.REDEEMED, voucher2.getStatus());
		assertEquals(ProductStatus.ACTIVE, voucher3.getStatus());
		assertEquals(ProductStatus.REDEEMED, voucher4.getStatus());
		assertEquals(ProductStatus.REDEEMED, voucher5.getStatus());
	}

	@Test
	public void testMoneyVoucherRedeemNowLimit() {
		ObjectContext context = getContext();

		InvoiceLine il1 = Cayenne.objectForPK(context, InvoiceLine.class, 2);

		Invoice previousOwing1 = Cayenne.objectForPK(context, Invoice.class, 11);
		Invoice previousOwing2 = Cayenne.objectForPK(context, Invoice.class, 12);

		Voucher voucher = Cayenne.objectForPK(context, Voucher.class, 4);

        College college = Cayenne.objectForPK(context, College.class, 1);

        VoucherRedemptionHelper service = new VoucherRedemptionHelper(context, college);

		service.setInvoice(il1.getInvoice());
		service.addVoucher(voucher, new Money("150.0"));

		service.addInvoiceLines(Arrays.asList(il1));

		service.addPreviousOwingInvoices(Arrays.asList(previousOwing1, previousOwing2));

		service.processAgainstInvoices();

		assertFalse(service.getPayments().isEmpty());
		assertEquals(1, service.getPayments().size());

		assertFalse(voucher.isFullyRedeemed());
		assertEquals(1, voucher.getVoucherPaymentIns().size());

		List<PaymentInLine> voucherLines = voucher.getVoucherPaymentIns().get(0).getPayment().getPaymentInLines();
		assertEquals(2, voucherLines.size());

		Ordering.orderList(voucherLines, Arrays.asList(new Ordering(PaymentInLine.AMOUNT_PROPERTY, SortOrder.ASCENDING)));
		assertEquals(new Money("40.0"), voucherLines.get(0).getAmount());
		assertEquals(new Money("110.0"), voucherLines.get(1).getAmount());

		assertEquals(new Money("50.0"), voucher.getValueRemaining());

		context.commitChanges();

		voucher.setPersistenceState(PersistenceState.HOLLOW);

		assertEquals(ProductStatus.ACTIVE, voucher.getStatus());
	}

    public ObjectContext getContext() {
        return context;
    }
}
