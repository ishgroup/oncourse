/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { NoArgFunction } from "ish-ui";
import React from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { checkPermissions, getUserPreferences } from "../../common/actions";
import { State } from "../../reducers/state";
import { getActiveFundingContracts } from "../avetmiss-export/actions";
import {
  getContactsConcessionTypes,
  getContactsRelationTypes,
  getContactsTaxTypes,
  getContactTags
} from "../entities/contacts/actions";
import { getCustomFieldTypes } from "../entities/customFieldTypes/actions";
import { getDefaultInvoiceTerms } from "../entities/invoices/actions";
import { getCountries, getLanguages } from "../preferences/actions";
import { changeStep, checkoutClearState } from "./actions";
import CheckoutSelection from "./components/CheckoutSelection";
import { getCheckoutCurrentStep } from "./utils";

export const FORM: string = "QUICK_ENROL_FORM";

interface Props {
  onInit?: NoArgFunction;
  clearState?: NoArgFunction;
  changeStep?: (step: number) => void;
  checkoutStep?: number;
  match?: any;
}

const Checkout = React.memo<Props>(props => {
  const {
    onInit,
    changeStep,
    checkoutStep,
    clearState,
  } = props;

  const [paymentStatus, setPaymentStatus] = React.useState("");

  React.useEffect(() => {

    onInit();

    const query = new URLSearchParams(window.location.search);
    const paymentStatus = query.get("paymentStatus");

    if (paymentStatus) {
      setPaymentStatus(paymentStatus);
    }

    return clearState;
  }, []);

  const onChangeStep = React.useCallback(step => {
    changeStep(getCheckoutCurrentStep(step));
  }, []);

  return (
    <>
      {!paymentStatus && <CheckoutSelection onChangeStep={onChangeStep} checkoutStep={checkoutStep} />}
    </>
  );
});

const mapStateToProps = (state: State) => ({
  checkoutStep: state.checkout.step
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  onInit: () => {
    dispatch(getCustomFieldTypes("Contact"));
    dispatch(getContactTags());
    dispatch(getCountries());
    dispatch(getLanguages());
    dispatch(getContactsRelationTypes());
    dispatch(getContactsConcessionTypes());
    dispatch(getDefaultInvoiceTerms());
    dispatch(getContactsTaxTypes());
    dispatch(getActiveFundingContracts(true));
    dispatch(getUserPreferences(["payment.gateway.type"]));
    dispatch(checkPermissions({ keyCode: "ENROLMENT_CREATE" }));
    dispatch(checkPermissions({ path: "/a/v1/list/plain?entity=Enrolment", method: "GET" }));
    dispatch(checkPermissions({ path: "/a/v1/list/plain?entity=PriorLearning", method: "GET" }));
    dispatch(checkPermissions({ path: "/a/v1/list/plain?entity=PaymentIn", method: "GET" }));
    dispatch(checkPermissions({ path: "/a/v1/list/plain?entity=Outcome", method: "GET" }));
    dispatch(checkPermissions({ path: "/a/v1/list/plain?entity=Certificate", method: "GET" }));
  },
  changeStep: (step: number) => dispatch(changeStep(step)),
  clearState: () => {
    dispatch(checkoutClearState());
  },
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(Checkout);