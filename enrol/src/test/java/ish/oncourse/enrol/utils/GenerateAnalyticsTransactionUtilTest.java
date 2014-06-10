package ish.oncourse.enrol.utils;

import ish.math.Money;
import ish.oncourse.analytics.Item;
import ish.oncourse.analytics.Transaction;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.Assert.*;

public class GenerateAnalyticsTransactionUtilTest {

	@Test
	public void generateTransactionTest() {
		Long paymentId = 1l;
		BigDecimal tax = Money.ONE.toBigDecimal();
		BigDecimal amount = new Money("10").toBigDecimal();
		String city = "payerSuburb", state = "payerState";
		Transaction result = GenerateAnalyticsTransactionUtil.generateTransaction(city, state, paymentId, tax, amount,
				Arrays.asList(new Item()));
		assertNotNull("Analitics transaction should not be empty", result);
		assertEquals("Default affilation should be set", Transaction.DEFAULT_WEB_AFFILIATION, result.getAffiliation());
		assertEquals("Default country value should be set", GenerateAnalyticsTransactionUtil.DEFAULT_COUNTRY_AUSTRALIA, result.getCountry());
		assertFalse("Items list should not be empty", result.getItems().isEmpty());
		assertEquals("Items list should contain 1 element in this test", 1, result.getItems().size());
		assertEquals("Order number should match the format", GenerateAnalyticsTransactionUtil.WILLOW_ENROLMENT_IDENTIFIER + paymentId, result.getOrderNumber());
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
		Item result = GenerateAnalyticsTransactionUtil.generateTransactionItem(GenerateAnalyticsTransactionUtil.getCategoryNameBy(tagPath), courseCode, courseName, classCode, unitPrice);
		assertNotNull("Generated item should not be empty", result);
		assertEquals("Quantity for item should be 1", 1, result.getQuantity());
		assertEquals("unit price should not be updated", unitPrice, result.getUnitPrice());
		assertEquals("Sky code should match the pattern",
			String.format("%s%s%s", courseCode, GenerateAnalyticsTransactionUtil.SKY_CODE_DELIMITER, classCode), result.getSkuCode());
		assertEquals("Product name should match the pattern",
			String.format("%s%s%s", courseCode, GenerateAnalyticsTransactionUtil.PRODUCT_CODE_DELIMITER, courseName), result.getProductName());
		String expectedCategoryName = tagPath.replace(GenerateAnalyticsTransactionUtil.LEFT_SLASH_CHAR, GenerateAnalyticsTransactionUtil.DOT_CHAR).substring(1)
			.replaceAll(GenerateAnalyticsTransactionUtil.PLUS_CHARACTER_MATCH_PATTERN, GenerateAnalyticsTransactionUtil.SPACE_CHARACTER);
		assertEquals("Category name should match the pattern", expectedCategoryName, result.getCategoryName());
	}

    @Test
    public void testGetCategoryNameBy() {

        String result = GenerateAnalyticsTransactionUtil.getCategoryNameBy("   ");
        assertEquals("Result should be empty string", StringUtils.EMPTY, result);
        result = GenerateAnalyticsTransactionUtil.getCategoryNameBy(null);
        assertEquals("Result should be empty string", StringUtils.EMPTY, result);
        result = GenerateAnalyticsTransactionUtil.getCategoryNameBy(StringUtils.SPACE);
        assertEquals("Result should be empty string", StringUtils.EMPTY, result);
    }
}
