/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Discount } from '@api/model';
import { getTotalAndDeductionsByPrice } from '../../../js/common/utils/financial';

const feeOverride: Discount = {
  name: 'Fee override',
  discountType: 'Fee override',
  rounding: 'Nearest dollar',
  discountValue: 454.54
};

describe('getTotalAndDeductionsByPrice', () => {
  it('calculates ex tax discount and tax for Fee override so that price minus discount plus tax equals total', () => {
    const { total, discountEach, taxEach } = getTotalAndDeductionsByPrice(995.45, 0.1, feeOverride);

    expect(discountEach).toBe(540.9);
    expect(taxEach).toBe(45.45);
    expect(total).toBe(500);
  });

  it('calculates negative ex tax discount when Fee override is greater than price', () => {
    const { total, discountEach, taxEach } = getTotalAndDeductionsByPrice(100, 0.1, feeOverride);

    expect(discountEach).toBe(-354.55);
    expect(taxEach).toBe(45.45);
    expect(total).toBe(500);
  });

  it('calculates Fee override without tax', () => {
    const { total, discountEach, taxEach } = getTotalAndDeductionsByPrice(995.45, 0, feeOverride);

    expect(discountEach).toBe(540.45);
    expect(taxEach).toBe(0);
    expect(total).toBe(455);
  });

  it('keeps Dollar discount calculation unchanged', () => {
    const { total, discountEach, taxEach } = getTotalAndDeductionsByPrice(100, 0.1, { discountType: 'Dollar', discountValue: 10, rounding: 'No Rounding' });

    expect(discountEach).toBe(10);
    expect(taxEach).toBe(9);
    expect(total).toBe(99);
  });
});
