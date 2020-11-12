/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import { connect } from "react-redux";
import { getFormValues } from "redux-form";
import { CheckoutFundingInvoiceItem } from "../../../../model/checkout/fundingInvoice";
import { State } from "../../../../reducers/state";
import { getContactName } from "../../../entities/contacts/utils";
import { CheckoutPage } from "../CheckoutSelection";
import { HeaderFieldTypo } from "../HeaderField";
import { CHECKOUT_FUNDING_INVOICE_SUMMARY_LIST_FORM } from "./CheckoutFundingInvoiceSummaryList";

interface Props {
  setActiveField: any;
  activeField: string;
  currencySymbol?: any;
  selectedCompanies?: any[];
  trackAmountOwing?: boolean;
  fundingInvoiceValues?: CheckoutFundingInvoiceItem;
}

const CheckoutFundingThisInvoice = React.memo<Props>(props => {
  const {
    setActiveField, activeField, selectedCompanies, currencySymbol, trackAmountOwing, fundingInvoiceValues
  } = props;

  const onClickFundingInvoiceSummary = React.useCallback(() => {
    setActiveField(CheckoutPage.fundingInvoiceSummary);
  }, []);

  return trackAmountOwing && selectedCompanies.length > 0 && (
    <div className="pl-2 pr-2">
      <HeaderFieldTypo
        title={getContactName(selectedCompanies[0])}
        activeField={activeField}
        field={CheckoutPage.fundingInvoiceSummary}
        onClick={onClickFundingInvoiceSummary}
        amount={fundingInvoiceValues ? fundingInvoiceValues.total : 0}
        currencySymbol={currencySymbol}
        caption=""
        className="pt-1 pb-2"
      />
    </div>
  );
});

const mapStateToProps = (state: State) => ({
  currencySymbol: state.currency && state.currency.shortCurrencySymbol,
  selectedCompanies: state.checkout.fundingInvoice.companies,
  trackAmountOwing: state.checkout.fundingInvoice.trackAmountOwing,
  fundingInvoiceValues: getFormValues(CHECKOUT_FUNDING_INVOICE_SUMMARY_LIST_FORM)(state)
});

export default connect<any, any, any>(mapStateToProps, null)(CheckoutFundingThisInvoice);
