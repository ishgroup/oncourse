import React from 'react';
import {cleanup, render} from '@testing-library/react';
import AmountComp from '../../../../../js/enrol/components/AmountComp';
import { Tabs } from '../../../../../js/enrol/containers/payment/reducers/State';
import {
  mockAmount,
  mockPromotion,
  mockRedeemVoucherProduct,
  stubFunction
} from '../../../../../dev/mocks/mocks/MockFunctions';

afterEach(cleanup);

describe('Redeem voucher products markup tests', () => {
  it('Redeem voucher products render corect inputs', () => {
    const amount = mockAmount();
    const redeemedVoucherProduct = mockRedeemVoucherProduct();

    const {getByText} = render(
      <AmountComp
        amount={amount}
        onAddCode={stubFunction}
        promotions={[mockPromotion()]}
        redeemVouchers={[]}
        redeemedVoucherProducts={[redeemedVoucherProduct]}
        onUpdatePayNow={stubFunction}
        onToggleVoucher={stubFunction}
        onToggleVoucherProduct={stubFunction}
        currentTab={Tabs.creditCard}
      />,
    );

    const input = getByText(`${redeemedVoucherProduct.name}`).querySelector('input');

    expect(input.checked)
      .toEqual(redeemedVoucherProduct.enabled);
  });
});


