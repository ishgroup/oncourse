/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.util;

import ish.common.payable.IInvoiceLineInterface;
import ish.common.types.DiscountType;
import ish.math.Money;
import ish.math.MoneyRounding;
import ish.oncourse.cayenne.DiscountInterface;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
		
		Money discountAmount = DiscountUtils.discountValue(Arrays.asList(discount), priceExTax, taxRate);
		assertEquals(new Money("18.18"), discountAmount);
		Money finalPrice = DiscountUtils.getDiscountedFee(Arrays.asList(discount), priceExTax, taxRate);
		assertEquals(new Money("112.00"), finalPrice);
		DiscountUtils.applyDiscounts(Arrays.asList(discount), invoiceLine, taxRate, Money.ZERO);
		assertEquals(new Money("112.00"), invoiceLine.getPriceEachExTax().subtract(invoiceLine.getDiscountEachExTax()).add(invoiceLine.getTaxEach()));

		//12.5% discount, rounding to the nearest 50 cents, priceExTax $125
		when(discount.getDiscountPercent()).thenReturn(new BigDecimal(0.125));
		when(discount.getRounding()).thenReturn(MoneyRounding.ROUNDING_50C);
		priceExTax = new Money("125.00");
		when(invoiceLine.getPriceEachExTax()).thenReturn(priceExTax);
		
		discountAmount = DiscountUtils.discountValue(Arrays.asList(discount), priceExTax, taxRate);
		assertEquals(new Money("15.45"), discountAmount);
		finalPrice = DiscountUtils.getDiscountedFee(Arrays.asList(discount), priceExTax, taxRate);
		assertEquals(new Money("120.50"), finalPrice);
		DiscountUtils.applyDiscounts(Arrays.asList(discount), invoiceLine, taxRate, Money.ZERO);
		assertEquals(new Money("120.50"), invoiceLine.getPriceEachExTax().subtract(invoiceLine.getDiscountEachExTax()).add(invoiceLine.getTaxEach()));
		
		//12.5% discount, rounding to the nearest 10 cents
		when(discount.getDiscountPercent()).thenReturn(new BigDecimal(0.125));
		when(discount.getRounding()).thenReturn(MoneyRounding.ROUNDING_10C);
		priceExTax = new Money("125.00");
		
		when(invoiceLine.getPriceEachExTax()).thenReturn(priceExTax);
		discountAmount = DiscountUtils.discountValue(Arrays.asList(discount), priceExTax, taxRate);
		assertEquals(new Money("15.64"), discountAmount);
		finalPrice = DiscountUtils.getDiscountedFee(Arrays.asList(discount), priceExTax, taxRate);
		assertEquals(new Money("120.30"), finalPrice);
		DiscountUtils.applyDiscounts(Arrays.asList(discount), invoiceLine, taxRate, Money.ZERO);
		assertEquals(new Money("120.30"), invoiceLine.getPriceEachExTax().subtract(invoiceLine.getDiscountEachExTax()).add(invoiceLine.getTaxEach()));
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
		
		//real case with priceExTax $144.55 ($159 priceIncTax) and taxAdjustment $-0.01
		Money discountAmount = DiscountUtils.discountValue(Arrays.asList(discount), priceExTax, taxRate);
		assertEquals(new Money("20.46"), discountAmount);
		Money finalPrice = DiscountUtils.getDiscountedFee(Arrays.asList(discount), priceExTax, taxRate);
		assertEquals(new Money("136.50"), finalPrice);
		DiscountUtils.applyDiscounts(Arrays.asList(discount), invoiceLine, taxRate, taxAdjustmet);
		assertEquals(new Money("136.50"), invoiceLine.getPriceEachExTax().subtract(invoiceLine.getDiscountEachExTax()).add(invoiceLine.getTaxEach()));
	
		//the same case but  rounding to the nearest dollar
		when(discount.getRounding()).thenReturn(MoneyRounding.ROUNDING_1D);
		
		discountAmount = DiscountUtils.discountValue(Arrays.asList(discount), priceExTax, taxRate);
		assertEquals(new Money("20.91"), discountAmount);
		finalPrice = DiscountUtils.getDiscountedFee(Arrays.asList(discount), priceExTax, taxRate);
		assertEquals(new Money("136.00"), finalPrice);
		DiscountUtils.applyDiscounts(Arrays.asList(discount), invoiceLine, taxRate, taxAdjustmet);
		assertEquals(new Money("136.00"), invoiceLine.getPriceEachExTax().subtract(invoiceLine.getDiscountEachExTax()).add(invoiceLine.getTaxEach()));
	}

	@Test
	public void testMultiDiscountsValue() {
		BigDecimal taxRate = new BigDecimal(0.1);
		Money priceExTax = new Money("144.55");
		Money taxAdjustmet = new Money("-0.01");
		
		//priceExTax $144.55 taxAdjustment $-0.01
		IInvoiceLineInterface invoiceLine = mock(InvoiceLine.class);
		when(invoiceLine.getPriceEachExTax()).thenReturn(priceExTax);
		when(invoiceLine.getDiscountEachExTax()).thenCallRealMethod();
		when(invoiceLine.getTaxEach()).thenCallRealMethod();
		doCallRealMethod().when(invoiceLine).setDiscountEachExTax(any(Money.class));
		doCallRealMethod().when(invoiceLine).setTaxEach(any(Money.class));

		//$20.50 discount1, rounding to the nearest 50 cents, priceExTax $120
		DiscountInterface discount1 = mock(DiscountInterface.class);
		when(discount1.getDiscountMax()).thenReturn(Money.ZERO);
		when(discount1.getDiscountMin()).thenReturn(Money.ZERO);
		when(discount1.getDiscountDollar()).thenReturn(new Money("20.50"));
		when(discount1.getRounding()).thenReturn(MoneyRounding.ROUNDING_50C);
		when(discount1.getDiscountType()).thenReturn(DiscountType.DOLLAR);

		//15% discount3, rounding to the nearest dollar
		DiscountInterface discount2 = mock(DiscountInterface.class);
		when(discount2.getDiscountMax()).thenReturn(Money.ZERO);
		when(discount2.getDiscountMin()).thenReturn(Money.ZERO);
		when(discount2.getDiscountPercent()).thenReturn(new BigDecimal(0.15));
		when(discount2.getRounding()).thenReturn(MoneyRounding.ROUNDING_1D);
		when(discount2.getDiscountType()).thenReturn(DiscountType.PERCENT);

		//13% discount3, rounding to the nearest dollar
		DiscountInterface discount3 = mock(DiscountInterface.class);
		when(discount3.getDiscountMax()).thenReturn(Money.ZERO);
		when(discount3.getDiscountMin()).thenReturn(Money.ZERO);
		when(discount3.getDiscountPercent()).thenReturn(new BigDecimal(0.13));
		when(discount3.getRounding()).thenReturn(MoneyRounding.ROUNDING_10C);
		when(discount3.getDiscountType()).thenReturn(DiscountType.PERCENT);

		
		//the broadest rounding mode should be applied
		Money discountAmount = DiscountUtils.discountValue(Arrays.asList(discount1, discount2, discount3), priceExTax, taxRate);
		assertEquals(new Money("60.91"), discountAmount);
		Money finalPrice = DiscountUtils.getDiscountedFee(Arrays.asList(discount1, discount2, discount3), priceExTax, taxRate);
		assertEquals(new Money("92.00"), finalPrice);
		DiscountUtils.applyDiscounts(Arrays.asList(discount1, discount2, discount3), invoiceLine, taxRate, taxAdjustmet);
		assertEquals(new Money("92.00"), invoiceLine.getPriceEachExTax().subtract(invoiceLine.getDiscountEachExTax()).add(invoiceLine.getTaxEach()));
		
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
