package ish.oncourse.analytics;

import java.math.BigDecimal;
import java.util.List;

public class Transaction {
	public static final String DEFAULT_WEB_AFFILIATION = "web";
		private String affiliation;
		private String city;
		private String country;
		private List<Item> items;
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
						   String country, List<Item> items) {
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
		public List<Item> getItems() {
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
		public void setItems(List<Item> items) {
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
