/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.util;

import ish.common.payable.IInvoiceLineInterface;
import ish.common.payable.PayableLineInterface;
import ish.math.Money;
import ish.oncourse.cayenne.*;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.PersistenceState;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Gathered methods for calculating invoice/payment related values, which should never be different between client and server.
 */
public final class InvoiceUtil {
	private static final Logger logger = Logger.getLogger(InvoiceUtil.class);

	/**
	 * standard private constructor for utility class
	 */
	private InvoiceUtil() {}

	/**
	 * update amount owing for InvoiceInterface (PO and Payroll in the future)
	 * 
	 * @param payable
	 */
	public static void updateAmountOwing(PayableInterface payable) {
		// update invoice owing
		if (payable.getContact() == null) {
			// commented out because of the fact that sometimes existing invoices with no contact related to them are found
			// payable.setAmountOwing(Money.ZERO);
			logger.info("found payable (probably an invoice) with no contact attached to it : " + payable);
		} else {
			List<PaymentLineInterface> paymentLines = getSuccessfulPaymentLines(payable);
			Money totalCredit = sumPaymentLines(paymentLines, PaymentInterface.TYPE_IN, true);
			Money totalDebit = sumPaymentLines(paymentLines, PaymentInterface.TYPE_OUT, true);
			Money totalInvoiced = InvoiceUtil.sumInvoiceLines(payable.getPayableLines());
			Money paymentsBalance = totalCredit.subtract(totalDebit);
			Money totalOwing = totalInvoiced.subtract(paymentsBalance);

			if (logger.isDebugEnabled()) {
				logger.debug("calculate amount owing for invoice :" + payable);
				logger.debug("paymentsBalance :" + paymentsBalance);
				logger.debug("totalDebit :" + totalDebit);
				logger.debug("totalCredit :" + totalCredit);
				logger.debug("total invoice lines :" + totalInvoiced);
				logger.debug("owing :" + totalOwing);
			}

			payable.setAmountOwing(totalOwing);
		}
	}

	/**
	 * gathers all sucessful payment lines associated with InvoiceInterface (PO and Payroll in the future)
	 * 
	 * @param payable
	 * @return List of PaymentLineInterface
	 */
	public static List<PaymentLineInterface> getSuccessfulPaymentLines(PayableInterface payable) {
		// we could put here a specific query, but I'm not sure if that would help
		List<? extends PaymentLineInterface> paymentLines = payable.getPaymentLines();
		ArrayList<PaymentLineInterface> result = new ArrayList<>();
		if (paymentLines != null) {
			for (PaymentLineInterface paymentLine : paymentLines) {
				if (paymentLine.getPayment() != null && paymentLine.getPayment().isSuccess()) {
					result.add(paymentLine);
				}
			}
		}
		return result;
	}

	/**
	 * Utility method to sum payment lines (PaymentInLineInterface, PaymentOutLineInterface)
	 * 
	 * @param paymentLines
	 * @param ofPaymentType
	 * @param onlySuccessfulPayments whether the failed payments should be summed
	 * @return sum of amounts for given payment lines
	 */
	public static Money sumPaymentLines(List<? extends PaymentLineInterface> paymentLines, String ofPaymentType, boolean onlySuccessfulPayments) {
		Money result = Money.ZERO;
		if (!ofPaymentType.equals(PaymentInterface.TYPE_IN) && !ofPaymentType.equals(PaymentInterface.TYPE_OUT)) {
			throw new RuntimeException("the payment type is defined :" + ofPaymentType);
		}

		if (paymentLines != null) {
			for (PaymentLineInterface paymentLine : paymentLines) {
				if (paymentLine.getPayment().getTypeOfPayment().equals(ofPaymentType)) {
					if (!onlySuccessfulPayments || paymentLine.getPayment().isSuccess()) {
						result = result.add(paymentLine.getAmount());
					}
				}
			}
		}
		return result;
	}

	/**
	 * calculates sum of invoice lines including tax
	 * 
	 * @param invoiceLines to be summed
	 * @return sum
	 */
	public static Money sumInvoiceLines(List<? extends PayableLineInterface> invoiceLines) {
		return sumInvoiceLines(invoiceLines, true);
	}

	/**
	 * sums the invoice lines.
	 * 
	 * @param theInvoiceLines list of invoice lines to sum
	 * @param incTax boolean indicating if the sum should include the tax
	 * @return sum
	 */
	public static Money sumInvoiceLines(List<? extends PayableLineInterface> theInvoiceLines, boolean incTax) {
		Money result = Money.ZERO;
		for (PayableLineInterface il : theInvoiceLines) {

			Money invoicePriceToAdd = incTax ? (il.getFinalPriceToPayIncTax()) : il.getFinalPriceToPayExTax();
			result = result.add(invoicePriceToAdd);
		}
		return result;
	}

	/**
	 * deallocates a given payment from the invoice. Works only for the payment lines which are not committed to the database (after the payment-invoice line is
	 * committed to the database this relationship is immutable)
	 * 
	 * @param invoice to be un-linked from payment
	 * @param payment to be un-linked from invoice
	 * @param paymentL a list of payment lines, separate to the one linked to invoice/payment. Payment line will be removed from this list if it is removed from
	 *            invoice
	 * @return Money amount associated with the payment line
	 */
	public static Money invoiceDeallocate(InvoiceInterface invoice, PaymentInterface payment, List<? extends PaymentLineInterface> paymentL) {
		Money result = Money.ZERO;
		if (invoice == null || invoice.getPaymentLines() == null) {
			return result;
		}
		List<PaymentLineInterface> list = new ArrayList<>(invoice.getPaymentLines());

		for (PaymentLineInterface pLine : list) {
			if (pLine.getPersistenceState() == PersistenceState.NEW) {
				invoice.removeFromPaymentLines(pLine);
				payment.removeFromPaymentLines(pLine);
				invoice.setValueForKey(InvoiceInterface.TO_BE_PAID_PROPERTY, Money.ZERO);
				result = pLine.getAmount();
				payment.getObjectContext().deleteObjects(pLine);
				paymentL.remove(pLine);
				invoice.setValueForKey(InvoiceInterface.IS_SELECTED, false);
			}
		}
		return result;
	}

	/**
	 * method allocating a given amount of money to a single given invoice. Newly created payment line wont overpay the invoice.
	 * 
	 * @param invoice to assign the payment line to
	 * @param amountToAllocate amount to be allocated
	 * @param paymentIn the parent payment record
	 * @param paymentInL list of payment lines associated with this invoice
	 * @return allocated amount
	 */
	public static Money invoiceAllocate(InvoiceInterface invoice, Money amountToAllocate, PaymentInterface paymentIn, List<PaymentLineInterface> paymentInL) {
		return invoiceAllocate(invoice, amountToAllocate, paymentIn, paymentInL, false);
	}

	/**
	 * method allocating a given amount of money to a single given invoice.
	 * 
	 * @param invoice to assign the payment line to
	 * @param amountToAllocate amount to be allocated
	 * @param payment the parent payment record
	 * @param paymentLines list of payment lines associated with this invoice
	 * @param allocateAll boolean flag to force allocation of all the money and overpaying the invoice
	 * @return allocated amount
	 */
	public static Money invoiceAllocate(InvoiceInterface invoice, Money amountToAllocate, PaymentInterface payment, List<PaymentLineInterface> paymentLines,
			boolean allocateAll) {

		// check the compulsory arguments
		if (invoice == null) {
			throw new IllegalArgumentException("No invoice to allocate payments to!");
		}

		if (invoice.getAmountOwing() == null) {
			throw new IllegalStateException("Invoice does not have amount owing!");
		}

		if (amountToAllocate == null) {
			throw new IllegalArgumentException("Amount to allocate not specified!");
		}

		if (payment.getTypeOfPayment() == null ||
				!(payment.getTypeOfPayment().equals(PaymentInterface.TYPE_IN) || payment.getTypeOfPayment().equals(PaymentInterface.TYPE_OUT))) {
			throw new IllegalArgumentException("Invalid payment type!");
		}

		if (paymentLines == null) {
			throw new IllegalArgumentException("List of payment lines cannot be null!");
		}

		PaymentLineInterface pLine = paymentLineForInvoiceAndPayment(payment, invoice);

		if (pLine.getPersistenceState() != PersistenceState.NEW) {
			throw new IllegalStateException("The amount can be only altered for a new payment line.");
		}

		if (!paymentLines.contains(pLine)) {
			paymentLines.add(pLine);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("invoice no: " + invoice.getInvoiceNumber() + ", amount owing:" + invoice.getAmountOwing() + ", amount to allocate:" +
					amountToAllocate);
		}

		Money amount = Money.ZERO;
		if (allocateAll) {
			// invoice is to be overpaid or underpaid
			amount = amountToAllocate;
		} else if (payment.getTypeOfPayment().equals(PaymentInterface.TYPE_IN)) {
			// payment in has to be processed
			if (invoice.getAmountOwing().compareTo(Money.ZERO) < 0) {
				// if credit invoice
				if (invoice.getAmountOwing().compareTo(amountToAllocate) < 0) {
					amount = amountToAllocate;
				} else {
					amount = invoice.getAmountOwing();
				}
			} else if (invoice.getAmountOwing().compareTo(amountToAllocate) < 0) {
				// if invoice is to be paid in full
				amount = invoice.getAmountOwing();
			} else {
				// invoice is to be paid not in full
				amount = amountToAllocate;
			}
		} else if (payment.getTypeOfPayment().equals(PaymentInterface.TYPE_OUT)) {
			// payment out has to be processed
			if (invoice.getAmountOwing().compareTo(Money.ZERO) > 0) {
				// if invoice
				amount = invoice.getAmountOwing().negate();
			} else if (invoice.getAmountOwing().compareTo(amountToAllocate.negate()) > 0) {
				// if credit note is to be paid in full
				amount = invoice.getAmountOwing().negate();
			} else {
				// credit note is to be paid not in full
				amount = amountToAllocate;
			}
		} else {
			throw new IllegalStateException("Failed to allocate money to an invoice");
		}

		if (logger.isDebugEnabled()) {
			logger.debug("\tAllocating :" + amount);
		}

		// finalise the payment in line values
		pLine.setAmount(amount);
		invoice.setValueForKey(InvoiceInterface.TO_BE_PAID_PROPERTY, amount);
		invoice.setValueForKey(InvoiceInterface.IS_SELECTED, !amount.equals(Money.ZERO));
		return amount;
	}

	/**
	 * method allocating a given amount of money to a list of given invoices. Newly created payment line wont overpay the invoices.
	 * 
	 * @param spendingMoney amount to be allocated
	 * @param invoices to assign the payment lines to
	 * @param payment the parent payment record
	 * @param paymentLines list of payment lines associated with this invoice
	 * @return allocated amount
	 */
	public static Money allocateMoneyToInvoices(Money spendingMoney, List<? extends InvoiceInterface> invoices, PaymentInterface payment,
			List<PaymentLineInterface> paymentLines) {
		return allocateMoneyToInvoices(spendingMoney, invoices, payment, paymentLines, false);
	}

	/**
	 * method allocating a given amount of money to a list of given invoices.
	 * 
	 * @param spendingMoney amount to be allocated
	 * @param invoices to assign the payment lines to
	 * @param payment the parent payment record
	 * @param paymentLines list of payment lines associated with this invoice
	 * @param allowOverpayment boolean flag to force allocation of all the money and overpaying the invoices
	 * @return allocated amount
	 */
	public static Money allocateMoneyToInvoices(Money spendingMoney, List<? extends InvoiceInterface> invoices, PaymentInterface payment,
			List<PaymentLineInterface> paymentLines, boolean allowOverpayment) {
		Money moneyRequiredForPayingAllInvoices = sumInvoices(payment.getContact().getOwingInvoices());
		if (invoices.size() > 0) {
			// go through the credit invoices first:
			for (InvoiceInterface anInvoice : invoices) {

				Money amountRequredToAllocate = spendingMoney.subtract(moneyRequiredForPayingAllInvoices);

				if (payment.getTypeOfPayment().equals(PaymentInterface.TYPE_IN) && anInvoice.getAmountOwing().compareTo(Money.ZERO) < 0 &&
						amountRequredToAllocate.compareTo(Money.ZERO) < 0) {

					Money allocatedAmount = invoiceAllocate(anInvoice, amountRequredToAllocate, payment, paymentLines);
					spendingMoney = spendingMoney.subtract(allocatedAmount);
				} else if (payment.getTypeOfPayment().equals(PaymentInterface.TYPE_OUT) && anInvoice.getAmountOwing().compareTo(Money.ZERO) > 0 &&
						amountRequredToAllocate.compareTo(Money.ZERO) > 0) {
					Money allocatedAmount = invoiceAllocate(anInvoice, amountRequredToAllocate, payment, paymentLines);
					spendingMoney = spendingMoney.subtract(allocatedAmount);
				}
			}
			// now go through the invoices and pay as much as possible
			for (InvoiceInterface anInvoice : invoices) {
				if (payment.getTypeOfPayment().equals(PaymentInterface.TYPE_IN) && anInvoice.getAmountOwing().compareTo(Money.ZERO) > 0) {
					Money allocatedAmount = invoiceAllocate(anInvoice, spendingMoney, payment, paymentLines, allowOverpayment &&
							invoices.indexOf(anInvoice) == invoices.size());
					spendingMoney = spendingMoney.subtract(allocatedAmount);
				} else if (payment.getTypeOfPayment().equals(PaymentInterface.TYPE_OUT) && anInvoice.getAmountOwing().compareTo(Money.ZERO) < 0) {
					Money allocatedAmount = invoiceAllocate(anInvoice, spendingMoney, payment, paymentLines, allowOverpayment &&
							invoices.indexOf(anInvoice) == invoices.size());
					spendingMoney = spendingMoney.subtract(allocatedAmount);
				}
			}
		}
		return spendingMoney;
	}

	/**
	 * safety method to avoid duplicating payment lines for a given invoice and payment in. finds a given payment line records associated with given invoice and
	 * payment parameters.
	 * 
	 * @param payment
	 * @param invoice
	 * @return existing or new PaymentInLine if no existing records found.
	 */
	public static PaymentLineInterface paymentLineForInvoiceAndPayment(PaymentInterface payment, InvoiceInterface invoice) {

		for (PaymentLineInterface pl : payment.getPaymentLines()) {
			if (pl.getInvoice().equalsIgnoreContext(invoice)) {
				return pl;
			}
		}

		String paymentLineClassString = null;
		if (payment instanceof PaymentInInterface) {
			paymentLineClassString = payment.getClass().getName().replace("PaymentIn", "PaymentInLine");
		} else if (payment instanceof PaymentOutInterface) {
			paymentLineClassString = payment.getClass().getName().replace("PaymentOut", "PaymentOutLine");
		} else {
			throw new IllegalArgumentException("The provided payment record is not valid.");
		}

		try {
			@SuppressWarnings("unchecked")
			Class<? extends PaymentLineInterface> c = (Class<? extends PaymentLineInterface>) Class.forName(paymentLineClassString);
			PaymentLineInterface result = payment.getContext().newObject(c);
			result.setInvoice(invoice);
			result.setPayment(payment);
			result.setAccount(invoice.getDebtorsAccount());
			return result;
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("Cannot find a class in the package " + paymentLineClassString, e);
		}
	}

	/**
	 * sums all invoice amount owing for a payer
	 * 
	 * @param payer to be analysed
	 * @return Money sum of amount owing
	 */
	public static Money amountOwingForPayer(ContactInterface payer) {
		Money result = Money.ZERO;

		// update invoice owing
		if (payer != null) {
			List<? extends InvoiceInterface> invoices = payer.getInvoices();

			for (InvoiceInterface invoice : invoices) {
				updateAmountOwing(invoice);
				result = result.add(invoice.getAmountOwing());
			}
		}
		return result;
	}

	/**
	 * returns a list of invoices which are not balanced
	 * 
	 * @param oc context to be used for queries
	 * @param aClass class of InvoiceInterface records
	 * @param optionalQualifier additional qualifier to the invoice query
	 * @return list of invoices
	 */
	@SuppressWarnings("unchecked")
	public static List<InvoiceInterface> getUnbalancedInvoices(ObjectContext oc, Class<? extends InvoiceInterface> aClass, Expression optionalQualifier) {
		Expression qualifier = ExpressionFactory.greaterExp(InvoiceInterface.AMOUNT_OWING_PROPERTY, Money.ZERO);
		if (optionalQualifier != null) {
			optionalQualifier.andExp(qualifier);
		}
		SelectQuery query = new SelectQuery(aClass, optionalQualifier);
		query.addOrdering(InvoiceInterface.INVOICES_DUE_ORDERING);
		return oc.performQuery(query);
	}

	/**
	 * simply calculates sum of amounts owing for a given list of invoices.
	 * 
	 * @param invoices to be summed
	 * @return Money sum of amount owing
	 */
	public static Money sumInvoices(List<? extends InvoiceInterface> invoices) {
		Money result = Money.ZERO;
		for (InvoiceInterface invoice : invoices) {
			result = result.add(invoice.getAmountOwing());
		}
		return result;
	}

	/**
	 * Sets priceEachEx, discountEachEx and taxEach in param InvoiceLine. This should be the only way the invoice line is populated. <BR>
	 * <BR>
	 * When calling from places like QE or enrol app the parameters should be sourced from CourseClass(priceEachEx, taxAdjustment) and
	 * Discount.getDiscountValue()
	 * 
	 * @param invoiceLine - invoice line to be populated
	 * @param priceEachEx - price each excluding tax
	 * @param discountEachEx - discount each excluding tax
	 * @param taxRate - applied tax rate
	 * @param taxAdjustment - applied taxAdjustment
	 */
	public static void fillInvoiceLine(IInvoiceLineInterface invoiceLine, Money priceEachEx, Money discountEachEx, BigDecimal taxRate, Money taxAdjustment) {
		if (invoiceLine == null) {
			throw new IllegalArgumentException("Invoice line cannot be null");
		}
		// the two fields regarding price and discount each are simple to set
		invoiceLine.setPriceEachExTax(priceEachEx);
		invoiceLine.setDiscountEachExTax(discountEachEx);
		Money taxEach = calculateTaxEachForInvoiceLine(priceEachEx, discountEachEx, taxRate, taxAdjustment);
		invoiceLine.setTaxEach(taxEach);
	}

	/**
	 * the taxEach value might be tricky, as not always calculations between inc->ex, ex->inc values are reversible due to rounding issues <br>
	 * <br>
	 * these calculations we use: <br>
	 * finalPriceEachEx = originalPriceEachEx - originalDiscountEachEx <br>
	 * finalPriceEachInc = totalPriceEachEx * (1 + taxRate) <br>
	 * taxEach = finalPriceEachInc - finalPriceEachEx <br>
	 * this way taxEach value balances any possible rounding issues, so later we can simply and safely rely on following equation<br>
	 * finalPriceEachInc = originalPriceEachEx - originalDiscountEachEx + taxEach<br>
	 * <br>
	 * 
	 * @param priceEachEx - price each excluding tax
	 * @param discountEachEx - discount each excluding tax
	 * @param taxRate - applied tax rate
	 * @return taxEach value to set in InvoiceLine
	 * @param taxAdjustment - applied taxAdjustment
	 */
	public static Money calculateTaxEachForInvoiceLine(Money priceEachEx, Money discountEachEx, BigDecimal taxRate, Money taxAdjustment) {
		// calculate final total value ex tax
		Money finalPriceEachEx = priceEachEx.subtract(discountEachEx);
		// calculate final total value inc tax
		Money priceEachInc = MoneyUtil.getPriceIncTax(priceEachEx, taxRate, taxAdjustment);
		Money discountEachInc = MoneyUtil.getPriceIncTax(discountEachEx, taxRate, Money.ZERO);
		Money finalPriceEachInc = priceEachInc.subtract(discountEachInc);
		// and finally taxEach
		Money taxEach = finalPriceEachInc.subtract(finalPriceEachEx);
		// and start to calculate the taxAdjustment
		final Money originalTax = priceEachInc.subtract(priceEachEx);
		final Money calculatedTaxAdjustment = originalTax.subtract(taxEach).subtract(discountEachEx.multiply(taxRate));
		if (!calculatedTaxAdjustment.isZero()) {
			taxEach = taxEach.add(calculatedTaxAdjustment);
		}
		return taxEach;
	}
}