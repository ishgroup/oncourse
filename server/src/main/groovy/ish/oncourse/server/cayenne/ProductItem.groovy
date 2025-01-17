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

import ish.common.types.ConfirmationStatus
import ish.common.types.ProductStatus
import ish.common.types.ProductType
import ish.math.Money
import ish.oncourse.API
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.server.api.v1.function.CartFunctions
import ish.oncourse.server.cayenne.glue._ProductItem
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.annotation.Nonnull

import static ish.common.types.ProductStatus.*
import static ish.persistence.CommonExpressionFactory.previousMidnight
/**
 * ProductItem is an abstract entity describing product item which has been sold through onCourse.
 * Currently this includes articles, memberships and vouchers.
 */
@API
@QueueableEntity
class ProductItem extends _ProductItem implements Queueable, NotableTrait, ContactActivityTrait {

	private static final Logger logger = LogManager.getLogger()

	public static final String TYPE_STRING_DEFENITION = "typeString"
	public static final String PURCHASE_PRICE_KEY = "purchasePrice"

	public static final String DISPLAYABLE_STATUS = "displayStatus"

	@Override
	protected void postAdd() {
		super.postAdd()
		if (getConfirmationStatus() == null) {
			setConfirmationStatus(ConfirmationStatus.DO_NOT_SEND)
		}
	}

	@Override
	protected void postPersist() {
		removeAbandonedCartsWithThisProduct()
		super.postPersist()
	}

	private void removeAbandonedCartsWithThisProduct(){
		if(contact != null) {
			List<CheckoutContactRelation> checkoutRelations = CartFunctions.checkoutsByContactId(context, contact.willowId)

			def productRelations = checkoutRelations.findAll { it instanceof CheckoutProductRelation && it.relatedObjectId == product.id }
			context.deleteObjects(productRelations.collect {it.checkout}.unique())
			context.commitChanges()
		}
	}

/**
	 * @return confirmation email sending status: not sent, sent or suppressed from sending
	 */
	@Nonnull
	@API
	@Override
	ConfirmationStatus getConfirmationStatus() {
		return super.getConfirmationStatus()
	}

	/**
	 * @return the date and time this record was created
	 */
	@API
	@Override
	Date getCreatedOn() {
		return super.getCreatedOn()
	}

	@Override
	String getInteractionName() {
		return product.name
	}
/**
	 * @return date when product item (e.g. membership) expires
	 */
	@API
	@Override
	Date getExpiryDate() {
		return super.getExpiryDate()
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
	 * @return current status of this product item: active, cancelled, credited, redeemed, expired or delivered
	 */
	@API
	@Override
	ProductStatus getStatus() {
		return super.getStatus()
	}

	/**
	 * @return
	 */
	@Override
	Integer getType() {
		return super.getType()
	}


	/**
	 *
	 * @return dollar value paid for product item
	 */
	Money getPurchasePrice(){
		return invoiceLine.priceEachIncTax
	}



	/**
	 * @return contact who owns this product item
	 */
	@Nonnull
	@API
	@Override
	Contact getContact() {
		return super.getContact()
	}

	/**
	 * @return purchase invoice line for this product item
	 */
	@Nonnull
	@API
	@Override
	InvoiceLine getInvoiceLine() {
		return super.getInvoiceLine()
	}

	/**
	 * @return product type of this item
	 */
	@Nonnull
	@API
	@Override
	Product getProduct() {
		return super.getProduct()
	}

	@Override
	String getSummaryDescription() {
		if (getProduct() == null) {
			return super.getSummaryDescription()
		}
		return getProduct().getName()
	}


	ProductStatus getDisplayStatus() {

		switch (type) {
			case ProductType.VOUCHER.databaseValue:
			case ProductType.ARTICLE.databaseValue:
				return status
			case ProductType.MEMBERSHIP.databaseValue:
				if (status in [ACTIVE, NEW] && previousMidnight(new Date()).after(expiryDate)) {
					return EXPIRED
				} else {
					return status
				}
			default:
				throw new IllegalStateException()
		}

	}

	/**
	 * This methods must be overridden in inheritors classes
	 * @return
	 */
	@Override
	Class<? extends TagRelation> getTagRelationClass() {
		return ProductItemTagRelation
	}
}
