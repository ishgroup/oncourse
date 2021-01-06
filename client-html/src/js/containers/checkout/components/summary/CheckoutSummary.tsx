/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useEffect } from "react";
import { connect } from "react-redux";
import debounce from "lodash.debounce";
import { Invoice } from "@api/model";
import { usePrevious } from "../../../../common/utils/hooks";
import { BooleanArgFunction, NumberArgFunction } from "../../../../model/common/CommonFunctions";
import { State } from "../../../../reducers/state";
import { CheckoutSummaryListItem as SummaryList } from "../../../../model/checkout";
import {
  checkoutGetPreviousOwing, checkoutSetPreviousCredit,
  checkoutUpdateSummaryClassesDiscounts,
  checkoutUpdateSummaryPrices,
  getPlainPreviousCreditRecords,
  setPlainPreviousCreditSearch
} from "../../actions/checkoutSummary";
import CheckoutDiscountEditView from "./promocode/CheckoutDiscountEditView";
import { CheckoutPage } from "../CheckoutSelection";
import CheckoutSummaryList from "./CheckoutSummaryList";

interface Props {
  checkoutStep?: number;
  activeField: any;
  selectedDiscount?: any;
  checkoutSetPreviousCredit?: any;
  openDiscountView?: boolean;
  selectedContacts?: any[];
  summaryList?: SummaryList[];
  plainInvoices?: Invoice[];
  setPreviousCreditSearch?: NumberArgFunction;
  getPreviousCreditRecords?: () => void;
  getPreviousOwingRecords?: NumberArgFunction;
  checkoutUpdateSummaryPrices?: () => void;
  checkoutUpdateSummaryClassesDiscounts?: BooleanArgFunction;
}

const CheckoutSummary = React.memo<Props>(props => {
  const {
    checkoutStep,
    activeField,
    openDiscountView,
    selectedDiscount,
    selectedContacts,
    summaryList,
    plainInvoices,
    setPreviousCreditSearch,
    checkoutSetPreviousCredit,
    getPreviousCreditRecords,
    getPreviousOwingRecords,
    checkoutUpdateSummaryPrices,
    checkoutUpdateSummaryClassesDiscounts
  } = props;

  useEffect(() => {
    if (plainInvoices.length) {
      checkoutSetPreviousCredit(plainInvoices);
    }
  }, [plainInvoices]);

  const getPreviousInvoices = React.useCallback((payerIndex = -1) => {
    if (selectedContacts.length > 0) {
      summaryList.forEach((list, i) => {
        if ((list.payer && payerIndex === -1) || (list.payer && payerIndex === i)) {
          const contactId = list.contact.id;
          setPreviousCreditSearch(contactId);
          getPreviousCreditRecords();
          getPreviousOwingRecords(contactId);
        }
      });
    }
  }, [summaryList, selectedContacts, checkoutStep]);

  const prevPayer = usePrevious(summaryList.find(l => l.payer));

  React.useEffect(() => {
    const currentPayerIndex = summaryList.findIndex(l => l.payer);
    const currentPayer = summaryList[currentPayerIndex];

    if ((!prevPayer && currentPayer) || (prevPayer && currentPayer && prevPayer.contact.id !== currentPayer.contact.id)) {
      getPreviousInvoices(currentPayerIndex);
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

const mapStateToProps = (state: State) => ({
  summaryList: state.checkout.summary.list,
  plainInvoices: state?.plainSearchRecords["Invoice"]?.items || []
});

const mapDispatchToProps = dispatch => ({
  setPreviousCreditSearch: id => dispatch(setPlainPreviousCreditSearch(id)),
  checkoutSetPreviousCredit: items => dispatch(checkoutSetPreviousCredit(items)),
  getPreviousCreditRecords: () => dispatch(getPlainPreviousCreditRecords()),
  getPreviousOwingRecords: id => dispatch(checkoutGetPreviousOwing(id)),
  checkoutUpdateSummaryPrices: () => dispatch(checkoutUpdateSummaryPrices()),
  checkoutUpdateSummaryClassesDiscounts: forcePricesUpdate => dispatch(checkoutUpdateSummaryClassesDiscounts(forcePricesUpdate))
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(CheckoutSummary);
