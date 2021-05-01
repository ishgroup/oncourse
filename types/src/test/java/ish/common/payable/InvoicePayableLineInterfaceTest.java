package ish.common.payable;

import ish.math.Money;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;




public class InvoicePayableLineInterfaceTest {

	@Test
	public void testInvoiceLine() {
		// no tax,no discount quantity 1
		MockInvoiceLine mil = new MockInvoiceLine(new Money("100"), Money.ZERO, BigDecimal.ONE, BigDecimal.ZERO);

		// testing simple values ex tax
		Assertions.assertEquals(Money.ZERO, mil.getDiscountEachExTax(), "assuming discount each ex");
		Assertions.assertEquals(new Money("100"), mil.getPriceEachExTax(), "assuming price each ex");

		// testing simple values inc tax
		Assertions.assertEquals(Money.ZERO, mil.getDiscountEachIncTax(), "assuming discount each inc");
		Assertions.assertEquals(new Money("100"), mil.getPriceEachIncTax(), "assuming price each inc");

		// testing total values ex tax
		Assertions.assertEquals(new Money("100"), mil.getPriceTotalExTax(), "assuming total price ex");
		Assertions.assertEquals(Money.ZERO, mil.getDiscountTotalExTax(), "assuming total discount ex");
		Assertions.assertEquals(new Money("100"), mil.getDiscountedPriceTotalExTax(), "assuming total discounted price ex");

		// testing total values inc tax
		Assertions.assertEquals(new Money("100"), mil.getPriceTotalIncTax(), "assuming total price inc");
		Assertions.assertEquals(Money.ZERO, mil.getDiscountTotalIncTax(), "assuming total discount inc");

		// testing tax values
		Assertions.assertEquals(Money.ZERO, mil.getDiscountedPriceTotalTax(), "assuming total tax on discounted price");
		Assertions.assertEquals(Money.ZERO, mil.getDiscountedTaxOnPriceEach(), "assuming tax on each discounted price");
		Assertions.assertEquals(Money.ZERO, mil.getDiscountedPriceTotalTax(), "assuming total tax on discounted price");
		Assertions.assertEquals(Money.ZERO, mil.getTotalTax(), "assuming total tax");

		Assertions.assertEquals(new Money("100"), mil.getFinalPriceToPayIncTax(), "assuming final price to pay");
	}

	@Test
	public void testInvoiceLine2() {
		// no tax, discount, quantity 1
		MockInvoiceLine mil = new MockInvoiceLine(new Money("100"), new Money("10"), BigDecimal.ONE, BigDecimal.ZERO);

		// testing simple values ex tax
		Assertions.assertEquals(new Money("10"), mil.getDiscountEachExTax(), "assuming discount each ex");
		Assertions.assertEquals(new Money("100"), mil.getPriceEachExTax(), "assuming price each ex");

		// testing simple values inc tax
		Assertions.assertEquals(new Money("10"), mil.getDiscountEachIncTax(), "assuming discount each inc");
		Assertions.assertEquals(new Money("100"), mil.getPriceEachIncTax(), "assuming price each inc");

		// testing total values ex tax
		Assertions.assertEquals(new Money("100"), mil.getPriceTotalExTax(), "assuming total price ex");
		Assertions.assertEquals(new Money("10"), mil.getDiscountTotalExTax(), "assuming total discount ex");
		Assertions.assertEquals(new Money("90"), mil.getDiscountedPriceTotalExTax(), "assuming total discounted price ex");

		// testing total values inc tax
		Assertions.assertEquals(new Money("100"), mil.getPriceTotalIncTax(), "assuming total price inc");
		Assertions.assertEquals(new Money("10"), mil.getDiscountTotalIncTax(), "assuming total discount inc");

		// testing tax values
		Assertions.assertEquals(Money.ZERO, mil.getDiscountedPriceTotalTax(), "assuming total tax on discounted price");
		Assertions.assertEquals(Money.ZERO, mil.getDiscountedTaxOnPriceEach(), "assuming tax on each discounted price");
		Assertions.assertEquals(Money.ZERO, mil.getDiscountedPriceTotalTax(), "assuming total tax on discounted price");
		Assertions.assertEquals(Money.ZERO, mil.getTotalTax(), "assuming total tax");

		Assertions.assertEquals(new Money("90"), mil.getFinalPriceToPayIncTax(), "assuming final price to pay");
	}

	@Test
	public void testInvoiceLine3() {
		// 10% tax, no discount, quantity 1
		MockInvoiceLine mil = new MockInvoiceLine(new Money("100"), Money.ZERO, BigDecimal.ONE, new BigDecimal("0.1"));

		// testing simple values ex tax
		Assertions.assertEquals(Money.ZERO, mil.getDiscountEachExTax(), "assuming discount each ex");
		Assertions.assertEquals(new Money("100"), mil.getPriceEachExTax(), "assuming price each ex");

		// testing simple values inc tax
		Assertions.assertEquals(Money.ZERO, mil.getDiscountEachIncTax(), "assuming discount each inc");
		Assertions.assertEquals(new Money("110"), mil.getPriceEachIncTax(), "assuming price each inc");

		// testing total values ex tax
		Assertions.assertEquals(new Money("100"), mil.getPriceTotalExTax(), "assuming total price ex");
		Assertions.assertEquals(Money.ZERO, mil.getDiscountTotalExTax(), "assuming total discount ex");
		Assertions.assertEquals(new Money("100"), mil.getDiscountedPriceTotalExTax(), "assuming total discounted price ex");

		// testing total values inc tax
		Assertions.assertEquals(new Money("110"), mil.getPriceTotalIncTax(), "assuming total price inc");
		Assertions.assertEquals(Money.ZERO, mil.getDiscountTotalIncTax(), "assuming total discount inc");

		// testing tax values
		Assertions.assertEquals(new Money("10"), mil.getDiscountedPriceTotalTax(), "assuming total tax on discounted price");
		Assertions.assertEquals(new Money("10"), mil.getDiscountedTaxOnPriceEach(), "assuming tax on each discounted price");
		Assertions.assertEquals(new Money("10"), mil.getDiscountedPriceTotalTax(), "assuming total tax on discounted price");
		Assertions.assertEquals(new Money("10"), mil.getTotalTax(), "assuming total tax");

		Assertions.assertEquals(new Money("110"), mil.getFinalPriceToPayIncTax(), "assuming final price to pay");
	}

	@Test
	public void testInvoiceLine4() {
		// 10% tax, discount, quantity 1
		MockInvoiceLine mil = new MockInvoiceLine(new Money("100"), new Money("10"), BigDecimal.ONE, new BigDecimal("0.1"));

		// testing simple values ex tax
		Assertions.assertEquals(new Money("10"), mil.getDiscountEachExTax(), "assuming discount each ex");
		Assertions.assertEquals(new Money("100"), mil.getPriceEachExTax(), "assuming price each ex");

		// testing simple values inc tax
		Assertions.assertEquals(new Money("11"), mil.getDiscountEachIncTax(), "assuming discount each inc");
		Assertions.assertEquals(new Money("110"), mil.getPriceEachIncTax(), "assuming price each inc");

		// testing total values ex tax
		Assertions.assertEquals(new Money("100"), mil.getPriceTotalExTax(), "assuming total price ex");
		Assertions.assertEquals(new Money("10"), mil.getDiscountTotalExTax(), "assuming total discount ex");
		Assertions.assertEquals(new Money("90"), mil.getDiscountedPriceTotalExTax(), "assuming total discounted price ex");

		// testing total values inc tax
		Assertions.assertEquals(new Money("110"), mil.getPriceTotalIncTax(), "assuming total price inc");
		Assertions.assertEquals(new Money("11"), mil.getDiscountTotalIncTax(), "assuming total discount inc");

		// testing tax values
		Assertions.assertEquals(new Money("9"), mil.getDiscountedPriceTotalTax(), "assuming total tax on discounted price");
		Assertions.assertEquals(new Money("10"), mil.getDiscountedTaxOnPriceEach(), "assuming tax on each discounted price");
		Assertions.assertEquals(new Money("9"), mil.getDiscountedPriceTotalTax(), "assuming total tax on discounted price");
		Assertions.assertEquals(new Money("9"), mil.getTotalTax(), "assuming total tax");

		Assertions.assertEquals(new Money("99"), mil.getFinalPriceToPayIncTax(), "assuming final price to pay");
	}

	@Test
	public void testInvoiceLine5() {
		// 10% tax, discount, quantity 3
		MockInvoiceLine mil = new MockInvoiceLine(new Money("100"), new Money("10"), new BigDecimal("3"), new BigDecimal("0.1"));

		// testing simple values ex tax
		Assertions.assertEquals(new Money("10"), mil.getDiscountEachExTax(), "assuming discount each ex");
		Assertions.assertEquals(new Money("100"), mil.getPriceEachExTax(), "assuming price each ex");

		// testing simple values inc tax
		Assertions.assertEquals(new Money("11"), mil.getDiscountEachIncTax(), "assuming discount each inc");
		Assertions.assertEquals(new Money("110"), mil.getPriceEachIncTax(), "assuming price each inc");

		// testing total values ex tax
		Assertions.assertEquals(new Money("300"), mil.getPriceTotalExTax(), "assuming total price ex");
		Assertions.assertEquals(new Money("30"), mil.getDiscountTotalExTax(), "assuming total discount ex");
		Assertions.assertEquals(new Money("270"), mil.getDiscountedPriceTotalExTax(), "assuming total discounted price ex");

		// testing total values inc tax
		Assertions.assertEquals(new Money("330"), mil.getPriceTotalIncTax(), "assuming total price inc");
		Assertions.assertEquals(new Money("33"), mil.getDiscountTotalIncTax(), "assuming total discount inc");

		// testing tax values
		Assertions.assertEquals(new Money("27"), mil.getDiscountedPriceTotalTax(), "assuming total tax on discounted price");
		Assertions.assertEquals(new Money("10"), mil.getDiscountedTaxOnPriceEach(), "assuming tax on each discounted price");
		Assertions.assertEquals(new Money("27"), mil.getDiscountedPriceTotalTax(), "assuming total tax on discounted price");
		Assertions.assertEquals(new Money("27"), mil.getTotalTax(), "assuming total tax");

		Assertions.assertEquals(new Money("297"), mil.getFinalPriceToPayIncTax(), "assuming final price to pay");
	}

	@Test
	public void testInvoiceLine6() {
		// 10% tax, discount, quantity null
		MockInvoiceLine mil = new MockInvoiceLine(new Money("100"), Money.ZERO, null, BigDecimal.ZERO);

		// testing simple values ex tax
		Assertions.assertEquals(Money.ZERO, mil.getDiscountEachExTax(), "assuming discount each ex");
		Assertions.assertEquals(new Money("100"), mil.getPriceEachExTax(), "assuming price each ex");

		// testing simple values inc tax
		Assertions.assertEquals(Money.ZERO, mil.getDiscountEachIncTax(), "assuming discount each inc");
		Assertions.assertEquals(new Money("100"), mil.getPriceEachIncTax(), "assuming price each inc");

		// testing total values ex tax
		Assertions.assertEquals(Money.ZERO, mil.getPriceTotalExTax(), "assuming total price ex");
		Assertions.assertEquals(Money.ZERO, mil.getDiscountTotalExTax(), "assuming total discount ex");
		Assertions.assertEquals(Money.ZERO, mil.getDiscountedPriceTotalExTax(), "assuming total discounted price ex");

		// testing total values inc tax
		Assertions.assertEquals(Money.ZERO, mil.getPriceTotalIncTax(), "assuming total price inc");
		Assertions.assertEquals(Money.ZERO, mil.getDiscountTotalIncTax(), "assuming total discount inc");

		// testing tax values
		Assertions.assertEquals(Money.ZERO, mil.getDiscountedPriceTotalTax(), "assuming total tax on discounted price");
		Assertions.assertEquals(Money.ZERO, mil.getDiscountedTaxOnPriceEach(), "assuming tax on each discounted price");
		Assertions.assertEquals(Money.ZERO, mil.getDiscountedPriceTotalTax(), "assuming total tax on discounted price");
		Assertions.assertEquals(Money.ZERO, mil.getTotalTax(), "assuming total tax");

		Assertions.assertEquals(Money.ZERO, mil.getFinalPriceToPayIncTax(), "assuming final price to pay");
	}

	@Test
	public void testInvoiceLine7() {
		// 10% tax, discount null, quantity 3
		MockInvoiceLine mil = new MockInvoiceLine(new Money("100"), null, new BigDecimal("3"), new BigDecimal("0.1"));

		// testing simple values ex tax
		Assertions.Assertions.assertNull(mil.getDiscountEachExTax(), "assuming discount each ex");
		Assertions.assertEquals(new Money("100"), mil.getPriceEachExTax(), "assuming price each ex");

		// testing simple values inc tax
		Assertions.assertEquals(Money.ZERO, mil.getDiscountEachIncTax(), "assuming discount each inc");
		Assertions.assertEquals(new Money("110"), mil.getPriceEachIncTax(), "assuming price each inc");

		// testing total values ex tax
		Assertions.assertEquals(new Money("300"), mil.getPriceTotalExTax(), "assuming total price ex");
		Assertions.assertEquals(Money.ZERO, mil.getDiscountTotalExTax(), "assuming total discount ex");
		Assertions.assertEquals(new Money("300"), mil.getDiscountedPriceTotalExTax(), "assuming total discounted price ex");

		// testing total values inc tax
		Assertions.assertEquals(new Money("330"), mil.getPriceTotalIncTax(), "assuming total price inc");
		Assertions.assertEquals(Money.ZERO, mil.getDiscountTotalIncTax(), "assuming total discount inc");

		// testing tax values
		Assertions.assertEquals(new Money("30"), mil.getDiscountedPriceTotalTax(), "assuming total tax on discounted price");
		Assertions.assertEquals(new Money("10"), mil.getDiscountedTaxOnPriceEach(), "assuming tax on each discounted price");
		Assertions.assertEquals(new Money("30"), mil.getDiscountedPriceTotalTax(), "assuming total tax on discounted price");
		Assertions.assertEquals(new Money("30"), mil.getTotalTax(), "assuming total tax");

		Assertions.assertEquals(new Money("330"), mil.getFinalPriceToPayIncTax(), "assuming final price to pay");
	}

	@Test
	public void testInvoiceLine8() {
		// 10% tax, discount , quantity 3, price null
		MockInvoiceLine mil = new MockInvoiceLine(null, new Money("10"), new BigDecimal("3"), new BigDecimal("0.1"));

		// testing simple values ex tax
		Assertions.assertEquals(new Money("10"), mil.getDiscountEachExTax(), "assuming discount each ex");
		Assertions.Assertions.assertNull(mil.getPriceEachExTax(), "assuming price each ex");

		// testing simple values inc tax
		Assertions.assertEquals(new Money("11"), mil.getDiscountEachIncTax(), "assuming discount each inc");
		Assertions.assertEquals(Money.ZERO, mil.getPriceEachIncTax(), "assuming price each inc");

		// testing total values ex tax
		Assertions.assertEquals(Money.ZERO, mil.getPriceTotalExTax(), "assuming total price ex");
		Assertions.assertEquals(new Money("30"), mil.getDiscountTotalExTax(), "assuming total discount ex");
		Assertions.assertEquals(new Money("-30"), mil.getDiscountedPriceTotalExTax(), "assuming total discounted price ex");

		// testing total values inc tax
		Assertions.assertEquals(Money.ZERO, mil.getPriceTotalIncTax(), "assuming total price inc");
		Assertions.assertEquals(new Money("33"), mil.getDiscountTotalIncTax(), "assuming total discount inc");

		// testing tax values
		Assertions.assertEquals(new Money("-3"), mil.getDiscountedPriceTotalTax(), "assuming total tax on discounted price");
		Assertions.assertEquals(Money.ZERO, mil.getDiscountedTaxOnPriceEach(), "assuming tax on each discounted price");
		Assertions.assertEquals(new Money("-3"), mil.getDiscountedPriceTotalTax(), "assuming total tax on discounted price");
		Assertions.assertEquals(new Money("-3"), mil.getTotalTax(), "assuming total tax");

		Assertions.assertEquals(new Money("-33"), mil.getFinalPriceToPayIncTax(), "assuming final price to pay");
	}

	@Test
	public void testInvoiceLine9() {
		// tax null, discount , quantity 3, price null
		MockInvoiceLine mil = new MockInvoiceLine(new Money("100"), new Money("10"), new BigDecimal("3"), null);

		// testing simple values ex tax
		Assertions.assertEquals(new Money("10"), mil.getDiscountEachExTax(), "assuming discount each ex");
		Assertions.assertEquals(new Money("100"), mil.getPriceEachExTax(), "assuming price each ex");

		// testing simple values inc tax
		Assertions.assertEquals(Money.ZERO, mil.getDiscountEachIncTax(), "assuming discount each inc");
		Assertions.assertEquals(Money.ZERO, mil.getPriceEachIncTax(), "assuming price each inc");

		// testing total values ex tax
		Assertions.assertEquals(new Money("300"), mil.getPriceTotalExTax(), "assuming total price ex");
		Assertions.assertEquals(Money.ZERO, mil.getDiscountTotalExTax(), "assuming total discount ex");
		Assertions.assertEquals(new Money("300"), mil.getDiscountedPriceTotalExTax(), "assuming total discounted price ex");

		// testing total values inc tax
		Assertions.assertEquals(Money.ZERO, mil.getPriceTotalIncTax(), "assuming total price inc");
		Assertions.assertEquals(Money.ZERO, mil.getDiscountTotalIncTax(), "assuming total discount inc");

		// testing tax values
		Assertions.assertEquals(Money.ZERO, mil.getDiscountedPriceTotalTax(), "assuming total tax on discounted price");
		Assertions.assertEquals(Money.ZERO, mil.getDiscountedTaxOnPriceEach(), "assuming tax on each discounted price");
		Assertions.assertEquals(Money.ZERO, mil.getDiscountedPriceTotalTax(), "assuming total tax on discounted price");
		Assertions.assertEquals(Money.ZERO, mil.getTotalTax(), "assuming total tax");

		Assertions.assertEquals(Money.ZERO, mil.getFinalPriceToPayIncTax(), "assuming final price to pay");
	}

}
