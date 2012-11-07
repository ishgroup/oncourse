package ish.oncourse.analytics;

import java.math.BigDecimal;

public class Item {


	private String categoryName;
	private String productName;
	private int quantity;
	private String skuCode;
	private BigDecimal unitPrice;

	/**
	 * @param categoryName
	 * @param productName
	 * @param quantity
	 * @param skuCode
	 * @param unitPrice
	 */
	public Item(String categoryName, String productName, int quantity, String skuCode,
				BigDecimal unitPrice) {
		this.categoryName = categoryName;
		this.productName = productName;
		this.quantity = quantity;
		this.skuCode = skuCode;
		this.unitPrice = unitPrice;
	}

	public Item() {

	}

	/**
	 * @return the categoryName
	 */
	public String getCategoryName() {
		return this.categoryName;
	}

	/**
	 * @return the productName
	 */
	public String getProductName() {
		return this.productName;
	}

	/**
	 * @return the quantity
	 */
	public int getQuantity() {
		return this.quantity;
	}

	/**
	 * @return the skuCode
	 */
	public String getSkuCode() {
		return this.skuCode;
	}

	/**
	 * @return the unitPrice
	 */
	public BigDecimal getUnitPrice() {
		if (this.unitPrice == null) {
			this.unitPrice = BigDecimal.ZERO;
		}
		return this.unitPrice;
	}

	/**
	 * @param categoryName
	 *            the categoryName to set
	 */
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	/**
	 * @param productName
	 *            the productName to set
	 */
	public void setProductName(String productName) {
		this.productName = productName;
	}

	/**
	 * @param quantity
	 *            the quantity to set
	 */
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	/**
	 * @param skuCode
	 *            the skuCode to set
	 */
	public void setSkuCode(String skuCode) {
		this.skuCode = skuCode;
	}

	/**
	 * @param unitPrice
	 *            the unitPrice to set
	 */
	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}
}
