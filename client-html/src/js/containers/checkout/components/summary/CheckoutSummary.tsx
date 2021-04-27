/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import { connect } from "react-redux";
import debounce from "lodash.debounce";
import { usePrevious } from "../../../../common/utils/hooks";
import { BooleanArgFunction, NumberArgFunction } from "../../../../model/common/CommonFunctions";
import { CheckoutSummaryListItem as SummaryList } from "../../../../model/checkout";
import {
  checkoutGetPreviousCredit,
  checkoutGetPreviousOwing,
  checkoutUpdateSummaryClassesDiscounts,
  checkoutUpdateSummaryPrices
} from "../../actions/checkoutSummary";
import CheckoutDiscountEditView from "./promocode/CheckoutDiscountEditView";
import { CheckoutPage } from "../CheckoutSelection";
import CheckoutSummaryList from "./CheckoutSummaryList";

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

    if ((!prevPayer && currentPayer) || (prevPayer && currentPayer && prevPayer.contact.id !== currentPayer.contact.id)) {
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
    <div>
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
