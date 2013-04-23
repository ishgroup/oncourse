package ish.oncourse.enrol.utils;

import java.math.BigDecimal;
import java.util.List;

import ish.oncourse.analytics.Item;
import ish.oncourse.analytics.Transaction;

public class GenerateAnaluticsTransactionUtil {
	static final char LEFT_SLASH_CHAR = '/';
	static final char DOT_CHAR = '.';
	static final String PLUS_CHARACTER_MATCH_PATTERN = "[+]";
	static final String SPACE_CHARACTER = " ";
	static final String PRODUCT_CODE_DELIMITER = ": ";
	static final String SKY_CODE_DELIMITER = "-";
	static final String DEFAULT_COUNTRY_AUSTRALIA = "Australia";
	static final String WILLOW_ENROLMENT_IDENTIFIER = "W";

	public static Transaction generateTransaction(String payerSuburb, String payerState, Long paymentInId, BigDecimal paymentTaxAmount, 
		BigDecimal paymentAmount, List<Item> transactionItems) {
		Transaction transaction = new Transaction();
        transaction.setAffiliation(Transaction.DEFAULT_WEB_AFFILIATION);
        transaction.setCity(payerSuburb);
        transaction.setCountry(DEFAULT_COUNTRY_AUSTRALIA);
        transaction.setItems(transactionItems);
        transaction.setOrderNumber(WILLOW_ENROLMENT_IDENTIFIER + paymentInId);
        transaction.setShippingAmount(null);
        transaction.setState(payerState);
        transaction.setTax(paymentTaxAmount);
        transaction.setTotal(paymentAmount);
        return transaction;
	}
	
	public static Item generateTransactionItem(String tagDefaultPath, String courseCode, String courseName, String classCode, BigDecimal unitPrice) {
		Item item = new Item();
		item.setCategoryName(tagDefaultPath.replace(LEFT_SLASH_CHAR, DOT_CHAR).substring(1).replaceAll(PLUS_CHARACTER_MATCH_PATTERN, SPACE_CHARACTER));
		StringBuilder productName = new StringBuilder(courseCode);
        productName.append(PRODUCT_CODE_DELIMITER).append(courseName);
        item.setProductName(productName.toString());
        item.setQuantity(1);
        StringBuilder skuCode = new StringBuilder(courseCode);
        skuCode.append(SKY_CODE_DELIMITER).append(classCode);
        item.setSkuCode(skuCode.toString());
        item.setUnitPrice(unitPrice);
		return item;
	}

}
