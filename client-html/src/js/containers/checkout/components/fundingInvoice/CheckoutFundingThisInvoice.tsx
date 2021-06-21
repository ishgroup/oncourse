/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import { connect } from "react-redux";
import { change, getFormValues } from "redux-form";
import { Dispatch } from "redux";
import { CheckoutFundingInvoice } from "../../../../model/checkout/fundingInvoice";
import { State } from "../../../../reducers/state";
import { getContactName } from "../../../entities/contacts/utils";
import { HeaderFieldTypo } from "../HeaderField";
import { CHECKOUT_FUNDING_INVOICE_SUMMARY_LIST_FORM } from "./CheckoutFundingInvoiceSummaryList";
import { CheckoutPage } from "../../constants";

interface Props {
  currencySymbol?: any;
  dispatch?: Dispatch;
  selectedCompanies?: any[];
  trackAmountOwing?: boolean;
  fundingInvoiceValues?: { fundingInvoices: CheckoutFundingInvoice[] };
}

const CheckoutFundingThisInvoice: React.FC<Props> = (
  {
    currencySymbol, fundingInvoiceValues, dispatch
  }
 ) => {
  const setActive = index => {
    dispatch(change(CHECKOUT_FUNDING_INVOICE_SUMMARY_LIST_FORM,
      "fundingInvoices",
        fundingInvoiceValues.fundingInvoices.map((f, ind) => ({
          ...f,
          active: index === ind
        }))));
  };

  return (
    <div className="pl-2 pr-2">
      {fundingInvoiceValues.fundingInvoices.map((f, index) => (
        <HeaderFieldTypo
          key={f.fundingProviderId}
          title={f.company ? getContactName(f.company) : <span className="errorColor">Not set</span>}
          activeField={f.active ? CheckoutPage.fundingInvoiceSummary : null}
          field={CheckoutPage.fundingInvoiceSummary}
          onClick={() => setActive(index)}
          amount={f.trackAmountOwing ? f.total || 0 : 0}
          currencySymbol={currencySymbol}
          caption=""
          className="pt-1 pb-2"
        />
      ))}
    </div>
);
};

const mapStateToProps = (state: State) => ({
  currencySymbol: state.currency && state.currency.shortCurrencySymbol,
  fundingInvoiceValues: getFormValues(CHECKOUT_FUNDING_INVOICE_SUMMARY_LIST_FORM)(state)
});

export default connect<any, any, any>(mapStateToProps, null)(CheckoutFundingThisInvoice);
