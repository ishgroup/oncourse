/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.utils;

import ish.common.types.ApplicationStatus;
import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentStatus;
import ish.common.types.PaymentType;
import ish.common.types.ProductStatus;
import ish.oncourse.model.*;
import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Utility class containing PaymentIn helper methods.
 */
public class PaymentInUtil {

	private static final Logger logger = Logger.getLogger(PaymentInUtil.class);

	private static boolean hasSuccessEnrolments(PaymentIn payment) {
		Expression paymentIdMatchExpression = ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, payment.getId());
		Expression enrollmentExpression = ExpressionFactory.matchExp(PaymentIn.PAYMENT_IN_LINES_PROPERTY + "." +
			PaymentInLine.INVOICE_PROPERTY + "." + Invoice.INVOICE_LINES_PROPERTY + "." +
			InvoiceLine.ENROLMENT_PROPERTY + "." + Enrolment.STATUS_PROPERTY, EnrolmentStatus.SUCCESS);
		SelectQuery checkQuery = new SelectQuery(PaymentIn.class, paymentIdMatchExpression.andExp(enrollmentExpression));

		List<PaymentIn> result = payment.getObjectContext().performQuery(checkQuery);
		return !result.isEmpty();
	}

	private static boolean hasSuccessProductItems(PaymentIn payment) {
		Expression paymentIdMatchExpression = ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, payment.getId());
		Expression productItemExpression = ExpressionFactory.matchExp(PaymentIn.PAYMENT_IN_LINES_PROPERTY + "." +
			PaymentInLine.INVOICE_PROPERTY + "." + Invoice.INVOICE_LINES_PROPERTY + "." +
			InvoiceLine.PRODUCT_ITEMS_PROPERTY + "." + ProductItem.STATUS_PROPERTY, ProductStatus.ACTIVE);
		SelectQuery checkQuery = new SelectQuery(PaymentIn.class, paymentIdMatchExpression.andExp(productItemExpression));

		List<PaymentIn> result = payment.getObjectContext().performQuery(checkQuery);
		return !result.isEmpty();
	}

	public static void abandonPayment(PaymentIn payment, boolean reverseInvoice) {
		try {
			logger.info(String.format("Canceling paymentIn with id:%s, created:%s and status:%s.", payment.getId(), payment.getCreated(), payment.getStatus()));

			if (reverseInvoice && !hasSuccessEnrolments(payment) && !hasSuccessProductItems(payment)) {
				payment.abandonPayment();
			} else {
				payment.abandonPaymentKeepInvoice();
			}
			payment.getObjectContext().commitChanges();
		} catch (final CayenneRuntimeException ce) {
			logger.debug(String.format("Unable to cancel payment with id:%s and status:%s.", payment.getId(), payment.getStatus()), ce);
			payment.getObjectContext().rollbackChanges();
		}
	}

	public static void makeFail(List<InvoiceLine> invoiceLines) {
		processInvoiceLines(invoiceLines, EnrolmentStatus.FAILED, ProductStatus.CANCELLED);
	}

	public static void makeSuccess(List<InvoiceLine> invoiceLines) {
		processInvoiceLines(invoiceLines, EnrolmentStatus.SUCCESS, ProductStatus.ACTIVE);
	}

	private static void processInvoiceLines(List<InvoiceLine> invoiceLines, EnrolmentStatus enrolmentStatus, ProductStatus productStatus) {
		Date date = new Date();
		for (InvoiceLine il : invoiceLines) {
			il.setModified(date);
			for (InvoiceLineDiscount ilDiscount : il.getInvoiceLineDiscounts()) {
				ilDiscount.setModified(date);
			}

			Enrolment enrol = il.getEnrolment();
			if (enrol != null) {
				enrol.setModified(date);
				if (enrol.getStatus() == EnrolmentStatus.IN_TRANSACTION) {
					enrol.setStatus(enrolmentStatus);
					updateApplicationStatusToAcceptedByEnrolment(enrol);
				}
			}

			List<ProductItem> productItems = new ArrayList<>();

			productItems.addAll(il.getProductItems());

			for (ProductItem productItem : productItems) {
				if (productItem.getStatus() == ProductStatus.NEW) {
					productItem.setStatus(productStatus);
				}
			}
		}
	}

	public static Collection<PaymentIn> getRelatedVoucherPayments(PaymentIn moneyPayment) {

		Set<PaymentIn> voucherPayments = new HashSet<>();

		for (PaymentInLine moneyPaymentLine : moneyPayment.getPaymentInLines()) {

			for (PaymentInLine paymentLine : moneyPaymentLine.getInvoice().getPaymentInLines()) {
				if (moneyPaymentLine != paymentLine) {
					PaymentIn payment = paymentLine.getPaymentIn();

					if (PaymentType.VOUCHER.equals(payment.getType())) {
						voucherPayments.add(payment);
					}
				}
			}
		}

		return voucherPayments;
	}

	public static void reverseVoucherPayment(PaymentIn voucherPayment) {
		if (!PaymentType.VOUCHER.equals(voucherPayment.getType())) {
			throw new IllegalArgumentException("Only VOUCHER type payments can be reversed using this method.");
		}

		Voucher voucher = voucherPayment.getVoucher();

		if (voucher.isMoneyVoucher()) {
			voucher.setRedemptionValue(voucher.getValueRemaining().add(voucherPayment.getAmount()));
		} else {
			voucher.setRedeemedCoursesCount(voucher.getRedeemedCoursesCount() - voucherPayment.getVoucherPaymentIns().size());
		}

		voucher.setStatus(ProductStatus.ACTIVE);

		voucherPayment.setStatus(PaymentStatus.FAILED);

        /*
        we need to update modification date for VoucherPaymentIn to be sure these VoucherPaymentIn objects
        will be sent to angel in the same transaction as and the voucher payment.
         */
        List<VoucherPaymentIn> voucherPaymentIns = voucherPayment.getVoucherPaymentIns();
        for (VoucherPaymentIn voucherPaymentIn : voucherPaymentIns) {
            voucherPaymentIn.setModified(new Date());
        }
    }


	private static void updateApplicationStatusToAcceptedByEnrolment(Enrolment enrolment) {
		List<Application> applications = enrolment.getStudent().getApplications();
		if (!applications.isEmpty()) {
			Expression exp = ExpressionFactory.matchExp(Application.STATUS_PROPERTY, ApplicationStatus.OFFERED)
					.andExp(ExpressionFactory.matchExp(Application.COURSE_PROPERTY, enrolment.getCourseClass().getCourse()))
					.andExp(ExpressionFactory.matchExp(Application.STUDENT_PROPERTY, enrolment.getStudent()));
			List<Application> appList = exp.filterObjects(applications);
			if (!appList.isEmpty()) {
				Application app = appList.get(0);
				app.setStatus(ApplicationStatus.ACCEPTED);
			}
		}
	}
}