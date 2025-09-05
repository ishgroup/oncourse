/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Discount, DiscountType, MoneyRounding } from '@api/model';
import Decimal from 'decimal.js-light';
import { openInternalLink } from 'ish-ui';
import { NestedListItem } from '../../../../common/components/form/nestedList/NestedList';
import { bankRounding } from '../../../../common/utils/financial';

export const plainDiscountToAPIModel = (plain: any):Discount => {
  const {
    name, discountType, discountDollar, discountPercent, rounding
  } = plain;
  
  return  {
    name,
    discountType,
    rounding,
    discountPercent,
    discountValue: discountDollar && parseFloat(discountDollar)
  };
};

export const openDiscountsLink = (id: number) => openInternalLink(`/discount/${id}`);

export const getRoundingByType = (type: MoneyRounding, value: Decimal): number => {
  switch (type) {
    default:
    case "No Rounding":
      return bankRounding(value);
    case "Nearest 10 cents":
      return value.toDecimalPlaces(1, Decimal.ROUND_UP).toNumber();
    case "Nearest 50 cents": {
      // @ts-ignore
      const cents = new Decimal(value.d[1] || 0).div(100000).toNumber();

      switch (true) {
        case [0, 50].includes(cents):
          return value.toNumber();
        case cents < 25:
          return value.toDecimalPlaces(0, Decimal.ROUND_DOWN).toNumber();
        case cents >= 25 && cents < 75:
          return value
            .toDecimalPlaces(0, Decimal.ROUND_DOWN)
            .plus(0.5)
            .toNumber();
        case cents >= 75:
          return value.toDecimalPlaces(0, Decimal.ROUND_UP).toNumber();
      }
      return value.toNumber();
    }
    case "Nearest dollar":
      return value.toDecimalPlaces(0).toNumber();
  }
};

export const getDiscountedFee = (discount: Discount, taxMul: Decimal, fee: number = 0): Decimal => {
  const dFee = new Decimal(fee);
  switch (discount.discountType) {
    case "Percent":
      return dFee.minus(getRoundingByType(discount.rounding, dFee.mul(new Decimal(1).minus(discount.discountPercent))));
    case "Dollar":
      return dFee.minus(getRoundingByType(discount.rounding, dFee.minus(new Decimal(discount.discountValue).mul(taxMul))));
    case "Fee override":
      return dFee.minus(getRoundingByType(discount.rounding, new Decimal(discount.discountValue).mul(taxMul)));
    default:
      return dFee;
  }
};

export const getDiscountAmountByFeeIncTaxAndDiscount = (discount: Discount, feeIncDiscountExTax: Decimal): Decimal => {
  switch (discount.discountType) {
    case 'Percent':
      return new Decimal(feeIncDiscountExTax).div(new Decimal(1).sub(discount.discountPercent)).mul(discount.discountPercent);
    case 'Dollar':
      return new Decimal(discount.discountValue);
    case 'Fee override':
      return new Decimal(0);
    default:
      return feeIncDiscountExTax;
  }
};

export const getDiscountAmountExTax = (discount: Discount, taxRate: number, fee: number = 0): Decimal => {
  let amountNumber = getDiscountedFee(discount,  new Decimal(1).plus(taxRate), fee).toNumber();
  amountNumber = (typeof discount.discountMin  === 'number' && amountNumber < discount.discountMin)
    ? discount.discountMin
    : (typeof discount.discountMax  === 'number' && amountNumber > discount.discountMax)
      ? discount.discountMax
      : amountNumber;

  return new Decimal(amountNumber);
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
