/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { BooleanArgFunction, NumberArgFunction, usePrevious } from 'ish-ui';
import debounce from 'lodash.debounce';
import React from 'react';
import { connect } from 'react-redux';
import LoadingIndicator from '../../../../common/components/progress/LoadingIndicator';
import { CheckoutSummaryListItem as SummaryList } from '../../../../model/checkout';
import {
  checkoutGetPreviousCredit,
  checkoutGetPreviousOwing,
  checkoutUpdateSummaryClassesDiscounts,
  checkoutUpdateSummaryPrices
} from '../../actions/checkoutSummary';
import { CheckoutPage } from '../../constants';
import CheckoutSummaryList from './CheckoutSummaryList';
import CheckoutDiscountEditView from './promocode/CheckoutDiscountEditView';

interface Props {
  checkoutStep?: number;
  activeField: any;
  selectedDiscount?: any;
  openDiscountView?: boolean;
  selectedContacts?: any[];
  summaryList?: SummaryList[];
  setPreviousCreditSearch?: NumberArgFunction;
  getPreviousCreditRecords?: NumberArgFunction;
  getPreviousOwingRecords?: NumberArgFunction;
  checkoutUpdateSummaryPrices?: () => void;
  checkoutUpdateSummaryClassesDiscounts?: BooleanArgFunction;
}

const CheckoutSummary = React.memo<Props>(props => {
  const {
    activeField,
    openDiscountView,
    selectedDiscount,
    selectedContacts,
    summaryList,
    getPreviousCreditRecords,
    getPreviousOwingRecords,
    checkoutUpdateSummaryPrices,
    checkoutUpdateSummaryClassesDiscounts
  } = props;

  const prevPayer = usePrevious(summaryList.find(l => l.payer));

  React.useEffect(() => {
    const currentPayerIndex = summaryList.findIndex(l => l.payer);
    const currentPayer = summaryList[currentPayerIndex];

    const query = new URLSearchParams(window.location.search);
    const transactionId = query.get("payment_intent");

    if (!transactionId && (!prevPayer && currentPayer) || (prevPayer && currentPayer && prevPayer.contact.id !== currentPayer.contact.id)) {
      getPreviousCreditRecords(currentPayer.contact.id);
      getPreviousOwingRecords(currentPayer.contact.id);
    }
  }, [selectedContacts, summaryList]);

  const debounceUpdatePrices = React.useCallback<any>(debounce(updateOnlyPrice => {
    if (updateOnlyPrice) {
      checkoutUpdateSummaryPrices();
    } else {
      checkoutUpdateSummaryClassesDiscounts(true);
    }
  }, 500), []);

  return (
    <div className="w-100">
      <LoadingIndicator />
      {!openDiscountView
      && !selectedDiscount
      && activeField === CheckoutPage.summary
      && <CheckoutSummaryList updatePrices={debounceUpdatePrices} />}
      {openDiscountView && selectedDiscount && (
        <CheckoutDiscountEditView type="discount" selectedDiscount={selectedDiscount} />
      )}
    </div>
  );
});

const mapDispatchToProps = dispatch => ({
  getPreviousOwingRecords: id => dispatch(checkoutGetPreviousOwing(id)),
  getPreviousCreditRecords: id => dispatch(checkoutGetPreviousCredit(id)),
  checkoutUpdateSummaryPrices: () => dispatch(checkoutUpdateSummaryPrices()),
  checkoutUpdateSummaryClassesDiscounts: forcePricesUpdate => dispatch(checkoutUpdateSummaryClassesDiscounts(forcePricesUpdate))
});

export default connect<any, any, any>(null, mapDispatchToProps)(CheckoutSummary);
