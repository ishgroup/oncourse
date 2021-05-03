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

import ish.common.types.ProductStatus
import ish.common.types.ProductType
import ish.math.Money
import ish.oncourse.API
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.server.cayenne.glue._ArticleProduct
import ish.util.AccountUtil
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.exp.ExpressionFactory

import javax.annotation.Nullable
/**
 * Represents generic product type which can be sold.
 */
@API
@QueueableEntity
class ArticleProduct extends _ArticleProduct {



	public static final String NUMBER_SOLD_PROPERTY = "number_sold"
	public static final String STATUS_PROP = "status_prop"

	@Override
	void onEntityCreation() {
		super.onEntityCreation()
		if (getType() == null) {
			setType(ProductType.ARTICLE.getDatabaseValue())
		}
		if (getTax() == null) {
			try {
				setTax(Tax.getTaxWithCode("GST", getObjectContext()))
			} catch (Exception ignored) {
			}
		}
		if (getIncomeAccount() == null) {
			try {
				setIncomeAccount(AccountUtil.getDefaultStudentEnrolmentsAccount(getContext(), Account.class))
			} catch (Exception ignored) {
				// ignore
			}
		}
		if (getIsOnSale() == null) {
			setIsOnSale(true)
		}
		if (getIsWebVisible() == null) {
			setIsWebVisible(true)
		}
		if (getTaxAdjustment() == null) {
			setTaxAdjustment(Money.ZERO)
		}
	}

	static Class<?> getDataTypeForKey(String key) {
		if (NUMBER_SOLD_PROPERTY == key) {
			return Integer.class
		}
		Class<?> result = _ArticleProduct.getDataTypeForKey(key)
		if (result != null) {
			return result
		}
		return Product.getDataTypeForKey(key)
	}

	@Override
	Object getValueForKey(String key) {
		if (NUMBER_SOLD_PROPERTY == key) {
			return getActiveArticleCount()
		} else if (STATUS_PROP == key) {
			if (getIsOnSale() == null || !getIsOnSale()) {
				return STATUS_DISABLED
			} else if (getIsWebVisible() == null || !getIsWebVisible()) {
				return STATUS_ENABLED
			} else {
				return STATUS_ENABLED_AND_WEB
			}
		}

		return super.getValueForKey(key)
	}

	@Override
	void setValueForKey(String key, Object value) {
		if (STATUS_PROP == key) {
			if (STATUS_DISABLED == value) {
				setIsWebVisible(false)
				setIsOnSale(false)
			} else if (STATUS_ENABLED == value) {
				setIsWebVisible(false)
				setIsOnSale(true)
			} else if (STATUS_ENABLED_AND_WEB == value) {
				setIsWebVisible(true)
				setIsOnSale(true)
			}
		} else {
			super.setValueForKey(key, value)
		}
	}

	int getActiveArticleCount() {
		Expression activeArticlesExpression = ExpressionFactory.matchExp(ProductItem.STATUS.getName(), ProductStatus.ACTIVE)
		List<ProductItem> list = activeArticlesExpression.filterObjects(getProductItems())
		if (list == null) {
			return 0
		}
		return list.size()
	}

	/**
	 * @return weight of this product
	 */
	@API
	@Override
	@Nullable Double getWeight() {
		return super.getWeight()
	}

	@Override
	Class<? extends CustomField> getCustomFieldClass() {
		return ArticleCustomField
	}
}
