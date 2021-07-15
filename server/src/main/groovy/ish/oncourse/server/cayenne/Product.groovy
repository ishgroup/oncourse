/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.cayenne

import ish.common.types.ExpiryType
import ish.math.Money
import ish.oncourse.API
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.server.api.dao.EntityRelationDao
import ish.oncourse.server.cayenne.glue._Product
import ish.util.MoneyFormatter
import ish.util.MoneyUtil
import org.apache.cayenne.query.Ordering
import org.apache.cayenne.query.SelectById
import org.apache.cayenne.query.SortOrder
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.annotation.Nonnull
import javax.swing.text.DefaultFormatter

/**
 * Product is an abstract entity describing product type which can be sold through onCourse.
 * Currently this includes article, membership and voucher.
 */
@API
@QueueableEntity
class Product extends _Product implements Queueable {

	private static final Logger logger = LogManager.getLogger()

	public static final Integer STATUS_DISABLED = 0
	public static final Integer STATUS_ENABLED = 1
	public static final Integer STATUS_ENABLED_AND_WEB = 2

	public static final String PRICE_INC_TAX_PROPERTY = "price_with_tax"

	static Class<?> getDataTypeForKey(String key) {
		if (PRICE_INC_TAX_PROPERTY == key) {
			return Money.class
		}
		return _Product.getDataTypeForKey(key)
	}

	/**
	 * @return GST tax amount for this product type
	 */
	@API
	Money getFeeGST() {
		if (getTax() != null) {
			return MoneyUtil.getPriceTax(getPriceExTax(), getTax().getRate(), getTaxAdjustment())
		}
		return MoneyUtil.getPriceTax(getPriceExTax(), null, getTaxAdjustment())
	}

	@Override
	void setValueForKey(String key, Object value) {
		if (PRICE_INC_TAX_PROPERTY == key) {
			if (value == null || value instanceof Money) {
				setPriceIncTax((Money) value)
			}
		} else {
			super.setValueForKey(key, value)
		}
	}

	@Override
	Object getValueForKey(String key) {
		if (PRICE_INC_TAX_PROPERTY == key) {
			return getPriceIncTax()
		}
		return super.getValueForKey(key)
	}

	Money getPriceIncTax() {
		if (getTax() != null) {
			return MoneyUtil.getPriceIncTax(getPriceExTax(), getTax().getRate(), getTaxAdjustment())
		}
		return MoneyUtil.getPriceIncTax(getPriceExTax(), null, getTaxAdjustment())
	}

	static DefaultFormatter getDefaultFormatterForKey(String key) {
		if (PRICE_INC_TAX_PROPERTY == key || PRICE_EX_TAX.getName() == key) {
			return MoneyFormatter.getInstance()
		}
		return null
	}

	static List<Ordering> getOrderingsForKey(String key, Boolean ascending) {
		if (PRICE_INC_TAX_PROPERTY == key) {
			return Collections.singletonList(new Ordering(PRICE_EX_TAX.getName(), SortOrder.ASCENDING))
		}

		return null
	}

	void setPriceIncTax(Money value) {
		Money priceIncTax = value == null ? Money.ZERO : value
		if (getTax() == null || getTax().getRate() == null) {
			setPriceExTax(priceIncTax)
			setTaxAdjustment(Money.ZERO)
		}
		BigDecimal taxRate = BigDecimal.ONE.add(getTax().getRate())
		setPriceExTax(priceIncTax.divide(taxRate))

		setTaxAdjustment(MoneyUtil.calculateTaxAdjustment(value, getPriceExTax(), getTax().getRate()))
	}

	/**
	 * @return the date and time this record was created
	 */
	@API
	@Override
	Date getCreatedOn() {
		return super.getCreatedOn()
	}

	/**
	 * @return description of this product type
	 */
	@API
	@Override
	String getDescription() {
		return super.getDescription()
	}

	/**
	 * @return number of days after purchase product is valid
	 */
	@API
	@Override
	Integer getExpiryDays() {
		return super.getExpiryDays()
	}

	/**
	 * @return expiry type of this product type
	 */
	@API
	@Override
	ExpiryType getExpiryType() {
		return super.getExpiryType()
	}


	/**
	 * @return true if this product can be currently purchased
	 */
	@Nonnull
	@API
	@Override
	Boolean getIsOnSale() {
		return super.getIsOnSale()
	}

	/**
	 * @return true if this product type can be purchased on website
	 */
	@Nonnull
	@API
	@Override
	Boolean getIsWebVisible() {
		return super.getIsWebVisible()
	}

	/**
	 * @return the date and time this record was modified
	 */
	@API
	@Override
	Date getModifiedOn() {
		return super.getModifiedOn()
	}

	/**
	 * @return name of this product type
	 */
	@API
	@Override
	String getName() {
		return super.getName()
	}

	/**
	 * @return private notes associated with product types
	 */
	@API
	@Override
	String getNotes() {
		return super.getNotes()
	}

	/**
	 * @return price for this product type excluding tax
	 */
	@API
	@Override
	Money getPriceExTax() {
		return super.getPriceExTax()
	}

	/**
	 * @return SKU of this product type
	 */
	@Nonnull
	@API
	@Override
	String getSku() {
		return super.getSku()
	}

	/**
	 * @return tax adjustment value (used for rounding)
	 */
	@Nonnull
	@API
	@Override
	Money getTaxAdjustment() {
		return super.getTaxAdjustment()
	}

	/**
	 * @return
	 */
	@Nonnull
	@Override
	Integer getType() {
		return super.getType()
	}

	/**
	 * @return courses related to this product type
	 */
	@Nonnull
	@API
	List<Course> getRelatedCourses() {
		List<Course> courses = new ArrayList<>()
		EntityRelationDao.getRelatedFrom(context, Product.simpleName, id)
				.findAll { it -> Course.simpleName == it.fromEntityIdentifier}
				.each { it -> courses.add(SelectById.query(Course, it.fromRecordId).selectOne(context)) }
		EntityRelationDao.getRelatedTo(context, Product.simpleName, id)
				.findAll { it -> Course.simpleName == it.toEntityIdentifier}
				.each { it -> courses.add(SelectById.query(Course, it.toRecordId).selectOne(context)) }
		return courses
	}

	/**
	 * @return income account linked to this product type
	 */
	@Nonnull
	@API
	@Override
	Account getIncomeAccount() {
		return super.getIncomeAccount()
	}

	/**
	 * @return product items of this product type
	 */
	@Nonnull
	@API
	@Override
	List<ProductItem> getProductItems() {
		return super.getProductItems()
	}

	/**
	 * @return tax linked to this product type
	 */
	@Nonnull
	@API
	@Override
	Tax getTax() {
		return super.getTax()
	}
}
