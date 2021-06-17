/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { useDispatch } from "react-redux";
import React from "react";
import Button from "@material-ui/core/Button";
import { reset } from "redux-form";
import { checkoutClearState } from "../actions";
import { checkoutGetActivePaymentMethods } from "../actions/checkoutPayment";
import { CHECKOUT_SUMMARY_FORM as SUMMARRY_FORM } from "./summary/CheckoutSummaryList";
import { CHECKOUT_FUNDING_INVOICE_SUMMARY_LIST_FORM } from "./fundingInvoice/CheckoutFundingInvoiceSummaryList";

const RestartButton: React.FC<any> = () => {
  const dispatch = useDispatch();

  return (
    <Button
      classes={{
      root: "whiteAppBarButton",
      disabled: "whiteAppBarButtonDisabled"
    }}
      onClick={() => {
      dispatch(checkoutClearState());
      dispatch(checkoutGetActivePaymentMethods());
      dispatch(reset(SUMMARRY_FORM));
      dispatch(reset(CHECKOUT_FUNDING_INVOICE_SUMMARY_LIST_FORM));
    }}
    >
      Start new checkout
    </Button>
);
};

export default RestartButton;
