/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Discount, DiscountType, MoneyRounding } from '@api/model';
import Decimal, { Numeric } from 'decimal.js-light';
import { openInternalLink } from 'ish-ui';
import { NestedListItem } from '../../../../common/components/form/nestedList/NestedList';

export const plainDiscountToAPIModel = (plain: any):Discount => {
  const {
    name, discountType, discountDollar, discountPercent, rounding, discountMin, discountMax
  } = plain;

  return  {
    name,
    discountType,
    rounding,
    discountPercent,
    discountValue: discountDollar && parseFloat(discountDollar),
    discountMin: discountMin && parseFloat(discountMin),
    discountMax: discountMax && parseFloat(discountMax)
  };
};

export const openDiscountsLink = (id: number) => openInternalLink(`/discount/${id}`);

/**
 * Mirrors Money.round(MoneyRounding) on the server
 */
export const getRoundingByType = (type: MoneyRounding, value: Decimal): number => {
  switch (type) {
    default:
    case "No Rounding":
      return value.toNumber();
    case "Nearest 10 cents":
      return value.toDecimalPlaces(1, Decimal.ROUND_HALF_UP).toNumber();
    case "Nearest 50 cents": {
      const whole = value.toDecimalPlaces(0, Decimal.ROUND_DOWN);
      const cents = value.minus(whole).abs();

      if (cents.isZero() || cents.eq(0.5)) {
        return value.toNumber();
      }
      if (cents.lt(0.25)) {
        return whole.toNumber();
      }
      if (cents.lt(0.75)) {
        return whole.plus(value.lt(0) ? -0.5 : 0.5).toNumber();
      }
      return value.toDecimalPlaces(0, Decimal.ROUND_UP).toNumber();
    }
    case "Nearest dollar":
      return value.toDecimalPlaces(0, Decimal.ROUND_HALF_UP).toNumber();
  }
};

// server Money keeps intermediate amounts with 4 decimal places
const toMoney = (value: Numeric): Decimal => new Decimal(value).toDecimalPlaces(4, Decimal.ROUND_HALF_EVEN);

export const getDiscountAmountByFeeIncTaxAndDiscount = (discount: Discount, feeIncDiscountExTax: Decimal): Decimal => {
  switch (discount.discountType) {
    case 'Percent':
      const divider = new Decimal(1).sub(discount.discountPercent);
      return divider.isZero() ? new Decimal(feeIncDiscountExTax) : new Decimal(feeIncDiscountExTax).div(divider).mul(discount.discountPercent);
    case 'Dollar':
      return new Decimal(discount.discountValue);
    case 'Fee override':
      return new Decimal(0);
    default:
      return feeIncDiscountExTax;
  }
};

// min and max are applied only when they are greater than zero, the same as on server
export const applyDiscountMinMax = (discount: Discount, amount: number): number => (
  (discount.discountMax > 0 && amount > discount.discountMax)
    ? discount.discountMax
    : (discount.discountMin > 0 && amount < discount.discountMin)
      ? discount.discountMin
      : amount
);

const getDiscountAmountByType = (discount: Discount, priceExTax: Decimal): Decimal => {
  switch (discount.discountType) {
    case "Percent":
      return toMoney(priceExTax.mul(discount.discountPercent || 0));
    case "Dollar":
      return new Decimal(discount.discountValue || 0);
    case "Fee override":
      return priceExTax.minus(discount.discountValue || 0);
    default:
      return new Decimal(0);
  }
};

/**
 * Discount amount ex tax before rounding, limited by discount min/max and by price.
 * Class discount override replaces the amount and is not limited by min/max.
 * Mirrors private DiscountUtils.discountValue(classDiscount, price) on the server
 */
const getUnroundedDiscountExTax = (discount: Discount, priceExTax: Decimal, discountOverride?: number): Decimal => {
  if (priceExTax.isZero()) {
    return priceExTax;
  }

  const amount = typeof discountOverride === 'number'
    ? new Decimal(discountOverride)
    : new Decimal(applyDiscountMinMax(discount, getDiscountAmountByType(discount, priceExTax).toNumber()));

  // server never gets negative price here, but manual invoice credit line has it and keeps its discount
  return priceExTax.gt(0) && priceExTax.lt(amount) ? priceExTax : amount;
};

/**
 * Final discounted price inc tax, rounded by discount rounding.
 * Rounding is applied to the total inc tax, not to the ex tax amounts.
 * Mirrors DiscountUtils.getDiscountedFee on the server, discountOverride is the class discount override ex tax
 */
export const getDiscountedFee = (discount: Discount, taxRate: number, priceExTax: number = 0, discountOverride?: number): Decimal => {
  const price = new Decimal(priceExTax || 0);
  const taxMul = new Decimal(1).plus(taxRate || 0);
  const priceIncTax = price.mul(taxMul).toDecimalPlaces(2, Decimal.ROUND_HALF_EVEN);

  if (!discount || price.isZero()) {
    return priceIncTax;
  }

  const discountAmount = getUnroundedDiscountExTax(discount, price, discountOverride);

  if (price.gt(0) && discountAmount.gte(price)) {
    return new Decimal(0);
  }

  return new Decimal(getRoundingByType(discount.rounding, toMoney(priceIncTax.minus(toMoney(discountAmount.mul(taxMul))))));
};

/**
 * Discount amount ex tax which gives rounded discounted price inc tax (see getDiscountedFee).
 * Mirrors DiscountUtils.discountValue(classDiscount, priceExTax, taxRate) on the server
 */
export const getDiscountAmountExTax = (discount: Discount, taxRate: number, priceExTax: number = 0): Decimal => {
  if (!discount) {
    return new Decimal(0);
  }
  const taxMul = new Decimal(1).plus(taxRate || 0);
  return toMoney(new Decimal(priceExTax || 0).minus(toMoney(getDiscountedFee(discount, taxRate, priceExTax).div(taxMul))));
};

const secondaryDiscountText = (discountType: DiscountType, discountValue: number, discountPercent: number) => {
  switch (discountType) {
    case "Percent":
      return `${discountPercent * 100}% discount`;
    case "Dollar":
      return `$${discountValue} discount`;
    case "Fee override":
      return `Fix price to $${discountValue}`;
    default: {
      throw Error("Unknown discountType");
    }
  }
};

export const transformDiscountForNestedList = (discount: Discount): NestedListItem => ({
  id: discount.id.toString(),
  entityId: discount.id,
  primaryText: discount.name,
  secondaryText: secondaryDiscountText(discount.discountType, discount.discountValue, discount.discountPercent),
  link: `/discount/${discount.id}`,
  active: true
});

export const discountSort = (a, b) => (a.name.toLowerCase() > b.name.toLowerCase() ? 1 : -1);

export const mapPlainDiscounts = d => ({
  id: Number(d.id),
  name: d.name,
  discountType: d.discountType,
  discountValue: d.discountDollar ? Number(d.discountDollar) : d.discountDollar,
  discountPercent: d.discountPercent ? Number(d.discountPercent) : d.discountPercent
});

export const mapPlainDiscountClasses = c => ({
  id: Number(c.id),
  name: c["course.name"],
  code: c.uniqueCode,
  type: "Class",
  active: c.isActive
});
