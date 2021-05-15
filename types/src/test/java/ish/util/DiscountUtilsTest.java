/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.util;

import ish.common.payable.IInvoiceLineInterface;
import ish.common.types.DiscountType;
import ish.math.Money;
import ish.math.MoneyRounding;
import ish.oncourse.cayenne.DiscountCourseClassInterface;
import ish.oncourse.cayenne.DiscountInterface;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class DiscountUtilsTest {

	@Test
	public void testPersentDiscountValue() {
		BigDecimal taxRate = new BigDecimal(0.1);
		Money priceExTax = new Money("120.00");

		IInvoiceLineInterface invoiceLine = mock(InvoiceLine.class);
		when(invoiceLine.getPriceEachExTax()).thenReturn(priceExTax);
		when(invoiceLine.getDiscountEachExTax()).thenCallRealMethod();
		when(invoiceLine.getTaxEach()).thenCallRealMethod();
		doCallRealMethod().when(invoiceLine).setDiscountEachExTax(any(Money.class));
		doCallRealMethod().when(invoiceLine).setTaxEach(any(Money.class));

		//15% discount, rounding to the nearest dollar, priceExTax $120
		DiscountInterface discount = mock(DiscountInterface.class);
		when(discount.getDiscountMax()).thenReturn(Money.ZERO);
		when(discount.getDiscountMin()).thenReturn(Money.ZERO);
		when(discount.getDiscountPercent()).thenReturn(new BigDecimal(0.15));
		when(discount.getRounding()).thenReturn(MoneyRounding.ROUNDING_1D);
		when(discount.getDiscountType()).thenReturn(DiscountType.PERCENT);

		DiscountCourseClassInterface classDiscount = mock(DiscountCourseClassInterface.class);
		when(classDiscount.getDiscount()).thenReturn(discount);
		when(classDiscount.getDiscountDollar()).thenReturn(null);
		
		Money discountAmount = DiscountUtils.discountValue(classDiscount, priceExTax, taxRate);
		Assertions.assertEquals(new Money("18.18"), discountAmount);
		Money finalPrice = DiscountUtils.getDiscountedFee(classDiscount, priceExTax, taxRate);
		Assertions.assertEquals(new Money("112.00"), finalPrice);
		DiscountUtils.applyDiscounts(classDiscount, invoiceLine, taxRate, Money.ZERO);
		Assertions.assertEquals(new Money("112.00"), invoiceLine.getPriceEachExTax().subtract(invoiceLine.getDiscountEachExTax()).add(invoiceLine.getTaxEach()));

		//12.5% discount, rounding to the nearest 50 cents, priceExTax $125
		when(discount.getDiscountPercent()).thenReturn(new BigDecimal(0.125));
		when(discount.getRounding()).thenReturn(MoneyRounding.ROUNDING_50C);
		priceExTax = new Money("125.00");
		when(invoiceLine.getPriceEachExTax()).thenReturn(priceExTax);
		
		discountAmount = DiscountUtils.discountValue(classDiscount, priceExTax, taxRate);
		Assertions.assertEquals(new Money("15.45"), discountAmount);
		finalPrice = DiscountUtils.getDiscountedFee(classDiscount, priceExTax, taxRate);
		Assertions.assertEquals(new Money("120.50"), finalPrice);
		DiscountUtils.applyDiscounts(classDiscount, invoiceLine, taxRate, Money.ZERO);
		Assertions.assertEquals(new Money("120.50"), invoiceLine.getPriceEachExTax().subtract(invoiceLine.getDiscountEachExTax()).add(invoiceLine.getTaxEach()));
		
		//12.5% discount, rounding to the nearest 10 cents
		when(discount.getDiscountPercent()).thenReturn(new BigDecimal(0.125));
		when(discount.getRounding()).thenReturn(MoneyRounding.ROUNDING_10C);
		priceExTax = new Money("125.00");
		
		when(invoiceLine.getPriceEachExTax()).thenReturn(priceExTax);
		discountAmount = DiscountUtils.discountValue(classDiscount, priceExTax, taxRate);
		Assertions.assertEquals(new Money("15.64"), discountAmount);
		finalPrice = DiscountUtils.getDiscountedFee(classDiscount, priceExTax, taxRate);
		Assertions.assertEquals(new Money("120.30"), finalPrice);
		DiscountUtils.applyDiscounts(classDiscount, invoiceLine, taxRate, Money.ZERO);
		Assertions.assertEquals(new Money("120.30"), invoiceLine.getPriceEachExTax().subtract(invoiceLine.getDiscountEachExTax()).add(invoiceLine.getTaxEach()));
	}

	@Test
	public void testDollarDiscountValue() {
		BigDecimal taxRate = new BigDecimal(0.1);
		Money priceExTax = new Money("144.55");
		Money taxAdjustmet = new Money("-0.01");

		IInvoiceLineInterface invoiceLine = mock(InvoiceLine.class);
		when(invoiceLine.getPriceEachExTax()).thenReturn(priceExTax);
		when(invoiceLine.getDiscountEachExTax()).thenCallRealMethod();
		when(invoiceLine.getTaxEach()).thenCallRealMethod();
		doCallRealMethod().when(invoiceLine).setDiscountEachExTax(any(Money.class));
		doCallRealMethod().when(invoiceLine).setTaxEach(any(Money.class));

		//$20.50 discount, rounding to the nearest 50 cents, priceExTax $120
		DiscountInterface discount = mock(DiscountInterface.class);
		when(discount.getDiscountMax()).thenReturn(Money.ZERO);
		when(discount.getDiscountMin()).thenReturn(Money.ZERO);
		when(discount.getDiscountDollar()).thenReturn(new Money("20.50"));
		when(discount.getRounding()).thenReturn(MoneyRounding.ROUNDING_50C);
		when(discount.getDiscountType()).thenReturn(DiscountType.DOLLAR);
		
		DiscountCourseClassInterface classDiscount = mock(DiscountCourseClassInterface.class);
		when(classDiscount.getDiscount()).thenReturn(discount);
		when(classDiscount.getDiscountDollar()).thenReturn(null);
		
		//real case with priceExTax $144.55 ($159 priceIncTax) and taxAdjustment $-0.01
		Money discountAmount = DiscountUtils.discountValue(classDiscount, priceExTax, taxRate);
		Assertions.assertEquals(new Money("20.46"), discountAmount);
		Money finalPrice = DiscountUtils.getDiscountedFee(classDiscount, priceExTax, taxRate);
		Assertions.assertEquals(new Money("136.50"), finalPrice);
		DiscountUtils.applyDiscounts(classDiscount, invoiceLine, taxRate, taxAdjustmet);
		Assertions.assertEquals(new Money("136.50"), invoiceLine.getPriceEachExTax().subtract(invoiceLine.getDiscountEachExTax()).add(invoiceLine.getTaxEach()));
	
		//the same case but  rounding to the nearest dollar
		when(discount.getRounding()).thenReturn(MoneyRounding.ROUNDING_1D);
		
		discountAmount = DiscountUtils.discountValue(classDiscount, priceExTax, taxRate);
		Assertions.assertEquals(new Money("20.91"), discountAmount);
		finalPrice = DiscountUtils.getDiscountedFee(classDiscount, priceExTax, taxRate);
		Assertions.assertEquals(new Money("136.00"), finalPrice);
		DiscountUtils.applyDiscounts(classDiscount, invoiceLine, taxRate, taxAdjustmet);
		Assertions.assertEquals(new Money("136.00"), invoiceLine.getPriceEachExTax().subtract(invoiceLine.getDiscountEachExTax()).add(invoiceLine.getTaxEach()));
	}

	/**
	 * for different combination of feeIncTax and discountIncTax we found situation when we lose one cent
	 */
	@Test
	public void testOneCentProblem() {
		BigDecimal taxRate = new BigDecimal(0.1);
		BigDecimal rate = new BigDecimal(1.1);
		for (int feeIncTaxDollars = 1; feeIncTaxDollars < 100 ; feeIncTaxDollars++){
			Money feeIncTax = new Money(feeIncTaxDollars,0);
			for (int discountIncTaxDollars = 1 ; discountIncTaxDollars < 0.1 * feeIncTaxDollars ; discountIncTaxDollars++) {
				Money discountIncTax = new Money(discountIncTaxDollars, 0);
				Money expectedPrice = feeIncTax.subtract(discountIncTax);

				Money discountExtTax = discountIncTax.divide(rate).round(MoneyRounding.ROUNDING_NONE);
				Money feeExtTax = feeIncTax.divide(rate).round(MoneyRounding.ROUNDING_NONE);
				Money taxAdjustment = MoneyUtil.calculateTaxAdjustment(feeIncTax, feeExtTax, taxRate);

				IInvoiceLineInterface invoiceLine = mock(InvoiceLine.class);
				when(invoiceLine.getPriceEachExTax()).thenReturn(feeExtTax);
				when(invoiceLine.getDiscountEachExTax()).thenCallRealMethod();
				when(invoiceLine.getTaxEach()).thenCallRealMethod();
				doCallRealMethod().when(invoiceLine).setDiscountEachExTax(any(Money.class));
				doCallRealMethod().when(invoiceLine).setTaxEach(any(Money.class));

				DiscountInterface discount = mock(DiscountInterface.class);
				when(discount.getRounding()).thenReturn(MoneyRounding.ROUNDING_NONE);

				DiscountCourseClassInterface classDiscount = mock(DiscountCourseClassInterface.class);
				when(classDiscount.getDiscount()).thenReturn(discount);
				when(classDiscount.getDiscountDollar()).thenReturn(discountExtTax);

				DiscountUtils.applyDiscounts(classDiscount,invoiceLine,taxRate,taxAdjustment);

				Money invoiceLineTaxEach = invoiceLine.getTaxEach();
				Money invoiceLineDiscountEachExTax = invoiceLine.getDiscountEachExTax();
				Money actualPrice = feeExtTax.subtract(invoiceLineDiscountEachExTax).add(invoiceLineTaxEach);

				Assertions.assertEquals(expectedPrice, actualPrice);
			}
		}
	}
	
	
	abstract class InvoiceLine implements IInvoiceLineInterface {
		private Money discountEachExTax;
		private Money taxEach;
		@Override
		public Money getDiscountEachExTax() {
			return discountEachExTax;
		}

		@Override
		public void setDiscountEachExTax(Money discountEachExTax) {
			this.discountEachExTax = discountEachExTax;
		}

		@Override
		public Money getTaxEach() {
			return taxEach;
		}

		@Override
		public void setTaxEach(Money taxEach) {
			this.taxEach = taxEach;
		}
	}
}
