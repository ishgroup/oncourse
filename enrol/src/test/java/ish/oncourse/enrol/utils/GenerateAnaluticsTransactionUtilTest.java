package ish.oncourse.enrol.utils;

import static org.junit.Assert.*;
import ish.math.Money;
import ish.oncourse.analytics.Item;
import ish.oncourse.analytics.Transaction;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.Test;

public class GenerateAnaluticsTransactionUtilTest {
	
	@Test
	public void generateTransactionTest() {
		Long paymentId = 1l;
		BigDecimal tax = Money.ONE.toBigDecimal();
		BigDecimal amount = new Money("10").toBigDecimal();
		String city = "payerSuburb", state = "payerState";
		Transaction result = GenerateAnaluticsTransactionUtil.generateTransaction(city, state, paymentId, tax, amount, 
			Arrays.asList(new Item()));
		assertNotNull("Analitics transaction should not be empty", result);
		assertEquals("Default affilation should be set", Transaction.DEFAULT_WEB_AFFILIATION, result.getAffiliation());
		assertEquals("Default country value should be set", GenerateAnaluticsTransactionUtil.DEFAULT_COUNTRY_AUSTRALIA, result.getCountry());
		assertFalse("Items list should not be empty", result.getItems().isEmpty());
		assertEquals("Items list should contain 1 element in this test", 1, result.getItems().size());
		assertEquals("Order number should match the format", GenerateAnaluticsTransactionUtil.WILLOW_ENROLMENT_IDENTIFIER + paymentId, result.getOrderNumber());
		assertEquals("Transaction city should match the passed data", city, result.getCity());
		assertEquals("Transaction state should match the passed data", state, result.getState());
		assertNull("Shipping amount should not be set", result.getShippingAmount());
		assertEquals("Tax amount should match the passed data", tax, result.getTax());
		assertEquals("Total amount should match the passed data", amount, result.getTotal());
	}
	
	@Test
	public void generateTransactionItemTest() {
		String tagPath = "/tagFirstPath/tagSecondPath+tagAddition+path", courseCode = "courseCode", courseName = "courseName", classCode = "classCode";
		BigDecimal unitPrice = Money.ONE.toBigDecimal();
		Item result = GenerateAnaluticsTransactionUtil.generateTransactionItem(tagPath, courseCode, courseName, classCode, unitPrice);
		assertNotNull("Generated item should not be empty", result);
		assertEquals("Quantity for item should be 1", 1, result.getQuantity());
		assertEquals("unit price should not be updated", unitPrice, result.getUnitPrice());
		assertEquals("Sky code should match the pattern", 
			String.format("%s%s%s", courseCode, GenerateAnaluticsTransactionUtil.SKY_CODE_DELIMITER, classCode), result.getSkuCode());
		assertEquals("Product name should match the pattern", 
			String.format("%s%s%s", courseCode, GenerateAnaluticsTransactionUtil.PRODUCT_CODE_DELIMITER, courseName), result.getProductName());
		String expectedCategoryName = tagPath.replace(GenerateAnaluticsTransactionUtil.LEFT_SLASH_CHAR, GenerateAnaluticsTransactionUtil.DOT_CHAR).substring(1)
			.replaceAll(GenerateAnaluticsTransactionUtil.PLUS_CHARACTER_MATCH_PATTERN, GenerateAnaluticsTransactionUtil.SPACE_CHARACTER);
		assertEquals("Category name should match the pattern", expectedCategoryName, result.getCategoryName());
	}

}
