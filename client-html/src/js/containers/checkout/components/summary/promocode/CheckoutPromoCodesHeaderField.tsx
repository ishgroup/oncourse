/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import $t from '@t';
import { StringArgFunction } from 'ish-ui';
import debounce from 'lodash.debounce';
import React, { useCallback } from 'react';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import { getFormValues } from 'redux-form';
import { State } from '../../../../../reducers/state';
import {
  checkoutGetPromoCode,
  checkoutRemoveDiscount,
  checkoutRemoveVoucher,
  checkoutUpdateSummaryClassesDiscounts
} from '../../../actions/checkoutSummary';
import { CheckoutPage } from '../../../constants';
import HeaderField from '../../HeaderField';
import SelectedPromoCodesRenderer from './SelectedPromoCodesRenderer';

export interface Props {
  setActiveField?: (field: any) => void;
  discounts?: any[];
  form?: string;
  value?: any;
  handleFocusCallback?: any;
  setPromotionSearch?: (value: string) => void;
  getPromotions?: StringArgFunction
  removeDiscount?: (index: number) => void;
  removeVoucher?: (index: number) => void;
  clearDiscountView?: () => void;
}

const CheckoutPromoCodesHeaderField = React.memo<any>(props => {
  const {
    form,
    dispatch,
    value,
    handleFocusCallback,
    setActiveField,
    getPromotions,
    removeDiscount,
    removeVoucher,
    discounts,
    itemList,
    openDiscountRow,
    selectedDiscount,
    clearDiscountView,
    disabled
  } = props;

  const onGetPromotions = useCallback<any>(debounce((name, value) => {
    setActiveField(name);
    if (value) {
      getPromotions(value);
    }
  }, 800), [itemList, setActiveField]);

  const deleteDiscountHandler = React.useCallback((e, i, row) => {
    setActiveField(CheckoutPage.promocodes);
    if (row.type === "discount") {
      removeDiscount(i);
    } else {
      removeVoucher(i);
    }
    clearDiscountView();
  }, []);

  const clearPromotionSearch = useCallback(() => {
    setActiveField(CheckoutPage.promocodes);
  }, []);

  return (
    <HeaderField
      heading="Discounts"
      name={CheckoutPage.promocodes}
      placeholder={$t('find_promotions')}
      onFocus={handleFocusCallback}
      items={discounts}
      SelectedItemView={(
        <SelectedPromoCodesRenderer
          type="discount"
          selectedDiscount={selectedDiscount}
          items={discounts}
          openRow={openDiscountRow}
          onDelete={deleteDiscountHandler}
          {...props}
        />
)}
      form={form}
      dispatch={dispatch}
      onSearch={onGetPromotions}
      onClearSearch={clearPromotionSearch}
      value={value}
      disabled={disabled}
      showArrowButton
    />
  );
});

const mapStateToProps = (state: State, props: any) => ({
  value: getFormValues(props.form)(state),
  discounts: state.checkout.summary.discounts,
  itemList: state.checkout.items
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
    getPromotions: (value: string) => dispatch(checkoutGetPromoCode(value)),
    removeDiscount: index => {
      dispatch(checkoutRemoveDiscount(index));
      dispatch(checkoutUpdateSummaryClassesDiscounts());
    },
    removeVoucher: index => dispatch(checkoutRemoveVoucher(index))
  });

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(CheckoutPromoCodesHeaderField);
