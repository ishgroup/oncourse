package ish.oncourse.enrol.components;

import ish.oncourse.model.WebSite;
import ish.oncourse.services.site.IWebSiteService;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.List;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

public class AnalyticsTransaction {
	@Inject
	private IWebSiteService webSiteService;

	@Parameter
	@Property
	private Transaction transaction;
	
	@Property
	private String affiliation;
	
	@Property
	private String transactionAccount;
	
	@Property
	private String transactionDomain;
	
	@Property
	private Item transactionItem;
	
	@Property
	private Format moneyFormat = new DecimalFormat("#,##0.00");

	@SetupRender
	void beginRender() {
		if (transaction != null) {
			WebSite currentWebSite = webSiteService.getCurrentWebSite();
			transactionAccount = currentWebSite.getGoogleAnalyticsAccount();
			transactionDomain = currentWebSite.getName();
			affiliation = transaction.getAffiliation();
			
			if (affiliation == null) {
				affiliation = "";
			}
		}
	}

	public boolean isHasOrderNumber() {
		return hasTransactionAndItems() && transaction.getOrderNumber() != null
				&& transaction.getOrderNumber().length() > 0;
	}

	public boolean hasTransactionAndItems() {
		return transaction != null && transaction.getItems() != null
				&& !transaction.getItems().isEmpty();
	}



	public static class Item {

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

	public static class Transaction {

		private String affiliation;
		private String city;
		private String country;
		private List<AnalyticsTransaction.Item> items;
		private String orderNumber;
		private BigDecimal shippingAmount;
		private String state;
		private BigDecimal tax;
		private BigDecimal total;

		/**
		 * @param orderNumber
		 * @param affiliation
		 * @param total
		 * @param tax
		 * @param shippingAmount
		 * @param city
		 * @param state
		 * @param country
		 * @param items
		 */
		public Transaction(String orderNumber, String affiliation, BigDecimal total,
				BigDecimal tax, BigDecimal shippingAmount, String city, String state,
				String country, List<AnalyticsTransaction.Item> items) {
			this.orderNumber = orderNumber;
			this.affiliation = affiliation;
			this.total = total;
			this.tax = tax;
			this.shippingAmount = shippingAmount;
			this.city = city;
			this.state = state;
			this.country = country;
			this.items = items;
		}

		public Transaction() {
		}

		/**
		 * @return the affiliation
		 */
		public String getAffiliation() {
			return this.affiliation;
		}

		/**
		 * @return the city
		 */
		public String getCity() {
			return this.city;
		}

		/**
		 * @return the country
		 */
		public String getCountry() {
			return this.country;
		}

		/**
		 * @return the items
		 */
		public List<AnalyticsTransaction.Item> getItems() {
			return this.items;
		}

		/**
		 * @return the orderNumber
		 */
		public String getOrderNumber() {
			return this.orderNumber;
		}

		/**
		 * @return the shipping
		 */
		public BigDecimal getShippingAmount() {
			return this.shippingAmount;
		}

		/**
		 * @return the state
		 */
		public String getState() {
			return this.state;
		}

		/**
		 * @return the tax
		 */
		public BigDecimal getTax() {
			return this.tax;
		}

		/**
		 * @return the total
		 */
		public BigDecimal getTotal() {
			return this.total;
		}

		/**
		 * @param affiliation
		 *            the affiliation to set
		 */
		public void setAffiliation(String affiliation) {
			this.affiliation = affiliation;
		}

		/**
		 * @param city
		 *            the city to set
		 */
		public void setCity(String city) {
			this.city = city;
		}

		/**
		 * @param country
		 *            the country to set
		 */
		public void setCountry(String country) {
			this.country = country;
		}

		/**
		 * @param items
		 *            the items to set
		 */
		public void setItems(List<AnalyticsTransaction.Item> items) {
			this.items = items;
		}

		/**
		 * @param orderNumber
		 *            the orderNumber to set
		 */
		public void setOrderNumber(String orderNumber) {
			this.orderNumber = orderNumber;
		}

		/**
		 * @param shipping
		 *            the shipping to set
		 */
		public void setShippingAmount(BigDecimal shipping) {
			this.shippingAmount = shipping;
		}

		/**
		 * @param state
		 *            the state to set
		 */
		public void setState(String state) {
			this.state = state;
		}

		/**
		 * @param tax
		 *            the tax to set
		 */
		public void setTax(BigDecimal tax) {
			this.tax = tax;
		}

		/**
		 * @param total
		 *            the total to set
		 */
		public void setTotal(BigDecimal total) {
			this.total = total;
		}

	}

}
