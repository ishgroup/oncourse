package ish.common.payable;

import ish.math.Money;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class InvoicePayableLineInterfaceTest {

	@Test
	public void testInvoiceLine() {
		// no tax,no discount quantity 1
		MockInvoiceLine mil = new MockInvoiceLine(new Money("100"), Money.ZERO, BigDecimal.ONE, BigDecimal.ZERO);

		// testing simple values ex tax
		assertEquals("assuming discount each ex", Money.ZERO, mil.getDiscountEachExTax());
		assertEquals("assuming price each ex", new Money("100"), mil.getPriceEachExTax());

		// testing simple values inc tax
		assertEquals("assuming discount each inc", Money.ZERO, mil.getDiscountEachIncTax());
		assertEquals("assuming price each inc", new Money("100"), mil.getPriceEachIncTax());

		// testing total values ex tax
		assertEquals("assuming total price ex", new Money("100"), mil.getPriceTotalExTax());
		assertEquals("assuming total discount ex", Money.ZERO, mil.getDiscountTotalExTax());
		assertEquals("assuming total discounted price ex", new Money("100"), mil.getDiscountedPriceTotalExTax());

		// testing total values inc tax
		assertEquals("assuming total price inc", new Money("100"), mil.getPriceTotalIncTax());
		assertEquals("assuming total discount inc", Money.ZERO, mil.getDiscountTotalIncTax());

		// testing tax values
		assertEquals("assuming total tax on discounted price", Money.ZERO, mil.getDiscountedPriceTotalTax());
		assertEquals("assuming tax on each discounted price", Money.ZERO, mil.getDiscountedTaxOnPriceEach());
		assertEquals("assuming total tax on discounted price", Money.ZERO, mil.getDiscountedPriceTotalTax());
		assertEquals("assuming total tax", Money.ZERO, mil.getTotalTax());

		assertEquals("assuming final price to pay", new Money("100"), mil.getFinalPriceToPayIncTax());
	}

	@Test
	public void testInvoiceLine2() {
		// no tax, discount, quantity 1
		MockInvoiceLine mil = new MockInvoiceLine(new Money("100"), new Money("10"), BigDecimal.ONE, BigDecimal.ZERO);

		// testing simple values ex tax
		assertEquals("assuming discount each ex", new Money("10"), mil.getDiscountEachExTax());
		assertEquals("assuming price each ex", new Money("100"), mil.getPriceEachExTax());

		// testing simple values inc tax
		assertEquals("assuming discount each inc", new Money("10"), mil.getDiscountEachIncTax());
		assertEquals("assuming price each inc", new Money("100"), mil.getPriceEachIncTax());

		// testing total values ex tax
		assertEquals("assuming total price ex", new Money("100"), mil.getPriceTotalExTax());
		assertEquals("assuming total discount ex", new Money("10"), mil.getDiscountTotalExTax());
		assertEquals("assuming total discounted price ex", new Money("90"), mil.getDiscountedPriceTotalExTax());

		// testing total values inc tax
		assertEquals("assuming total price inc", new Money("100"), mil.getPriceTotalIncTax());
		assertEquals("assuming total discount inc", new Money("10"), mil.getDiscountTotalIncTax());

		// testing tax values
		assertEquals("assuming total tax on discounted price", Money.ZERO, mil.getDiscountedPriceTotalTax());
		assertEquals("assuming tax on each discounted price", Money.ZERO, mil.getDiscountedTaxOnPriceEach());
		assertEquals("assuming total tax on discounted price", Money.ZERO, mil.getDiscountedPriceTotalTax());
		assertEquals("assuming total tax", Money.ZERO, mil.getTotalTax());

		assertEquals("assuming final price to pay", new Money("90"), mil.getFinalPriceToPayIncTax());
	}

	@Test
	public void testInvoiceLine3() {
		// 10% tax, no discount, quantity 1
		MockInvoiceLine mil = new MockInvoiceLine(new Money("100"), Money.ZERO, BigDecimal.ONE, new BigDecimal("0.1"));

		// testing simple values ex tax
		assertEquals("assuming discount each ex", Money.ZERO, mil.getDiscountEachExTax());
		assertEquals("assuming price each ex", new Money("100"), mil.getPriceEachExTax());

		// testing simple values inc tax
		assertEquals("assuming discount each inc", Money.ZERO, mil.getDiscountEachIncTax());
		assertEquals("assuming price each inc", new Money("110"), mil.getPriceEachIncTax());

		// testing total values ex tax
		assertEquals("assuming total price ex", new Money("100"), mil.getPriceTotalExTax());
		assertEquals("assuming total discount ex", Money.ZERO, mil.getDiscountTotalExTax());
		assertEquals("assuming total discounted price ex", new Money("100"), mil.getDiscountedPriceTotalExTax());

		// testing total values inc tax
		assertEquals("assuming total price inc", new Money("110"), mil.getPriceTotalIncTax());
		assertEquals("assuming total discount inc", Money.ZERO, mil.getDiscountTotalIncTax());

		// testing tax values
		assertEquals("assuming total tax on discounted price", new Money("10"), mil.getDiscountedPriceTotalTax());
		assertEquals("assuming tax on each discounted price", new Money("10"), mil.getDiscountedTaxOnPriceEach());
		assertEquals("assuming total tax on discounted price", new Money("10"), mil.getDiscountedPriceTotalTax());
		assertEquals("assuming total tax", new Money("10"), mil.getTotalTax());

		assertEquals("assuming final price to pay", new Money("110"), mil.getFinalPriceToPayIncTax());
	}

	@Test
	public void testInvoiceLine4() {
		// 10% tax, discount, quantity 1
		MockInvoiceLine mil = new MockInvoiceLine(new Money("100"), new Money("10"), BigDecimal.ONE, new BigDecimal("0.1"));

		// testing simple values ex tax
		assertEquals("assuming discount each ex", new Money("10"), mil.getDiscountEachExTax());
		assertEquals("assuming price each ex", new Money("100"), mil.getPriceEachExTax());

		// testing simple values inc tax
		assertEquals("assuming discount each inc", new Money("11"), mil.getDiscountEachIncTax());
		assertEquals("assuming price each inc", new Money("110"), mil.getPriceEachIncTax());

		// testing total values ex tax
		assertEquals("assuming total price ex", new Money("100"), mil.getPriceTotalExTax());
		assertEquals("assuming total discount ex", new Money("10"), mil.getDiscountTotalExTax());
		assertEquals("assuming total discounted price ex", new Money("90"), mil.getDiscountedPriceTotalExTax());

		// testing total values inc tax
		assertEquals("assuming total price inc", new Money("110"), mil.getPriceTotalIncTax());
		assertEquals("assuming total discount inc", new Money("11"), mil.getDiscountTotalIncTax());

		// testing tax values
		assertEquals("assuming total tax on discounted price", new Money("9"), mil.getDiscountedPriceTotalTax());
		assertEquals("assuming tax on each discounted price", new Money("10"), mil.getDiscountedTaxOnPriceEach());
		assertEquals("assuming total tax on discounted price", new Money("9"), mil.getDiscountedPriceTotalTax());
		assertEquals("assuming total tax", new Money("9"), mil.getTotalTax());

		assertEquals("assuming final price to pay", new Money("99"), mil.getFinalPriceToPayIncTax());
	}

	@Test
	public void testInvoiceLine5() {
		// 10% tax, discount, quantity 3
		MockInvoiceLine mil = new MockInvoiceLine(new Money("100"), new Money("10"), new BigDecimal("3"), new BigDecimal("0.1"));

		// testing simple values ex tax
		assertEquals("assuming discount each ex", new Money("10"), mil.getDiscountEachExTax());
		assertEquals("assuming price each ex", new Money("100"), mil.getPriceEachExTax());

		// testing simple values inc tax
		assertEquals("assuming discount each inc", new Money("11"), mil.getDiscountEachIncTax());
		assertEquals("assuming price each inc", new Money("110"), mil.getPriceEachIncTax());

		// testing total values ex tax
		assertEquals("assuming total price ex", new Money("300"), mil.getPriceTotalExTax());
		assertEquals("assuming total discount ex", new Money("30"), mil.getDiscountTotalExTax());
		assertEquals("assuming total discounted price ex", new Money("270"), mil.getDiscountedPriceTotalExTax());

		// testing total values inc tax
		assertEquals("assuming total price inc", new Money("330"), mil.getPriceTotalIncTax());
		assertEquals("assuming total discount inc", new Money("33"), mil.getDiscountTotalIncTax());

		// testing tax values
		assertEquals("assuming total tax on discounted price", new Money("27"), mil.getDiscountedPriceTotalTax());
		assertEquals("assuming tax on each discounted price", new Money("10"), mil.getDiscountedTaxOnPriceEach());
		assertEquals("assuming total tax on discounted price", new Money("27"), mil.getDiscountedPriceTotalTax());
		assertEquals("assuming total tax", new Money("27"), mil.getTotalTax());

		assertEquals("assuming final price to pay", new Money("297"), mil.getFinalPriceToPayIncTax());
	}

	@Test
	public void testInvoiceLine6() {
		// 10% tax, discount, quantity null
		MockInvoiceLine mil = new MockInvoiceLine(new Money("100"), Money.ZERO, null, BigDecimal.ZERO);

		// testing simple values ex tax
		assertEquals("assuming discount each ex", Money.ZERO, mil.getDiscountEachExTax());
		assertEquals("assuming price each ex", new Money("100"), mil.getPriceEachExTax());

		// testing simple values inc tax
		assertEquals("assuming discount each inc", Money.ZERO, mil.getDiscountEachIncTax());
		assertEquals("assuming price each inc", new Money("100"), mil.getPriceEachIncTax());

		// testing total values ex tax
		assertEquals("assuming total price ex", Money.ZERO, mil.getPriceTotalExTax());
		assertEquals("assuming total discount ex", Money.ZERO, mil.getDiscountTotalExTax());
		assertEquals("assuming total discounted price ex", Money.ZERO, mil.getDiscountedPriceTotalExTax());

		// testing total values inc tax
		assertEquals("assuming total price inc", Money.ZERO, mil.getPriceTotalIncTax());
		assertEquals("assuming total discount inc", Money.ZERO, mil.getDiscountTotalIncTax());

		// testing tax values
		assertEquals("assuming total tax on discounted price", Money.ZERO, mil.getDiscountedPriceTotalTax());
		assertEquals("assuming tax on each discounted price", Money.ZERO, mil.getDiscountedTaxOnPriceEach());
		assertEquals("assuming total tax on discounted price", Money.ZERO, mil.getDiscountedPriceTotalTax());
		assertEquals("assuming total tax", Money.ZERO, mil.getTotalTax());

		assertEquals("assuming final price to pay", Money.ZERO, mil.getFinalPriceToPayIncTax());
	}

	@Test
	public void testInvoiceLine7() {
		// 10% tax, discount null, quantity 3
		MockInvoiceLine mil = new MockInvoiceLine(new Money("100"), null, new BigDecimal("3"), new BigDecimal("0.1"));

		// testing simple values ex tax
		assertNull("assuming discount each ex", mil.getDiscountEachExTax());
		assertEquals("assuming price each ex", new Money("100"), mil.getPriceEachExTax());

		// testing simple values inc tax
		assertEquals("assuming discount each inc", Money.ZERO, mil.getDiscountEachIncTax());
		assertEquals("assuming price each inc", new Money("110"), mil.getPriceEachIncTax());

		// testing total values ex tax
		assertEquals("assuming total price ex", new Money("300"), mil.getPriceTotalExTax());
		assertEquals("assuming total discount ex", Money.ZERO, mil.getDiscountTotalExTax());
		assertEquals("assuming total discounted price ex", new Money("300"), mil.getDiscountedPriceTotalExTax());

		// testing total values inc tax
		assertEquals("assuming total price inc", new Money("330"), mil.getPriceTotalIncTax());
		assertEquals("assuming total discount inc", Money.ZERO, mil.getDiscountTotalIncTax());

		// testing tax values
		assertEquals("assuming total tax on discounted price", new Money("30"), mil.getDiscountedPriceTotalTax());
		assertEquals("assuming tax on each discounted price", new Money("10"), mil.getDiscountedTaxOnPriceEach());
		assertEquals("assuming total tax on discounted price", new Money("30"), mil.getDiscountedPriceTotalTax());
		assertEquals("assuming total tax", new Money("30"), mil.getTotalTax());

		assertEquals("assuming final price to pay", new Money("330"), mil.getFinalPriceToPayIncTax());
	}

	@Test
	public void testInvoiceLine8() {
		// 10% tax, discount , quantity 3, price null
		MockInvoiceLine mil = new MockInvoiceLine(null, new Money("10"), new BigDecimal("3"), new BigDecimal("0.1"));

		// testing simple values ex tax
		assertEquals("assuming discount each ex", new Money("10"), mil.getDiscountEachExTax());
		assertNull("assuming price each ex", mil.getPriceEachExTax());

		// testing simple values inc tax
		assertEquals("assuming discount each inc", new Money("11"), mil.getDiscountEachIncTax());
		assertEquals("assuming price each inc", Money.ZERO, mil.getPriceEachIncTax());

		// testing total values ex tax
		assertEquals("assuming total price ex", Money.ZERO, mil.getPriceTotalExTax());
		assertEquals("assuming total discount ex", new Money("30"), mil.getDiscountTotalExTax());
		assertEquals("assuming total discounted price ex", new Money("-30"), mil.getDiscountedPriceTotalExTax());

		// testing total values inc tax
		assertEquals("assuming total price inc", Money.ZERO, mil.getPriceTotalIncTax());
		assertEquals("assuming total discount inc", new Money("33"), mil.getDiscountTotalIncTax());

		// testing tax values
		assertEquals("assuming total tax on discounted price", new Money("-3"), mil.getDiscountedPriceTotalTax());
		assertEquals("assuming tax on each discounted price", Money.ZERO, mil.getDiscountedTaxOnPriceEach());
		assertEquals("assuming total tax on discounted price", new Money("-3"), mil.getDiscountedPriceTotalTax());
		assertEquals("assuming total tax", new Money("-3"), mil.getTotalTax());

		assertEquals("assuming final price to pay", new Money("-33"), mil.getFinalPriceToPayIncTax());
	}

	@Test
	public void testInvoiceLine9() {
		// tax null, discount , quantity 3, price null
		MockInvoiceLine mil = new MockInvoiceLine(new Money("100"), new Money("10"), new BigDecimal("3"), null);

		// testing simple values ex tax
		assertEquals("assuming discount each ex", new Money("10"), mil.getDiscountEachExTax());
		assertEquals("assuming price each ex", new Money("100"), mil.getPriceEachExTax());

		// testing simple values inc tax
		assertEquals("assuming discount each inc", Money.ZERO, mil.getDiscountEachIncTax());
		assertEquals("assuming price each inc", Money.ZERO, mil.getPriceEachIncTax());

		// testing total values ex tax
		assertEquals("assuming total price ex", new Money("300"), mil.getPriceTotalExTax());
		assertEquals("assuming total discount ex", Money.ZERO, mil.getDiscountTotalExTax());
		assertEquals("assuming total discounted price ex", new Money("300"), mil.getDiscountedPriceTotalExTax());

		// testing total values inc tax
		assertEquals("assuming total price inc", Money.ZERO, mil.getPriceTotalIncTax());
		assertEquals("assuming total discount inc", Money.ZERO, mil.getDiscountTotalIncTax());

		// testing tax values
		assertEquals("assuming total tax on discounted price", Money.ZERO, mil.getDiscountedPriceTotalTax());
		assertEquals("assuming tax on each discounted price", Money.ZERO, mil.getDiscountedTaxOnPriceEach());
		assertEquals("assuming total tax on discounted price", Money.ZERO, mil.getDiscountedPriceTotalTax());
		assertEquals("assuming total tax", Money.ZERO, mil.getTotalTax());

		assertEquals("assuming final price to pay", Money.ZERO, mil.getFinalPriceToPayIncTax());
	}

}
