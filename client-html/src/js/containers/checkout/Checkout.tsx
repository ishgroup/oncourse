/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React from "react";
import { Dispatch } from "redux";
import { connect } from "react-redux";
import { NoArgFunction } from "../../model/common/CommonFunctions";
import { State } from "../../reducers/state";
import { getActiveFundingContracts } from "../avetmiss-export/actions";
import { getCountries, getLanguages } from "../preferences/actions";
import {
  getContactsConcessionTypes,
  getContactsRelationTypes,
  getContactsTaxTypes,
  getContactTags
} from "../entities/contacts/actions";
import { checkPermissions } from "../../common/actions";
import { getDefaultInvoiceTerms } from "../entities/invoices/actions";
import { changeStep, checkoutClearState } from "./actions";
import CheckoutSelection from "./components/CheckoutSelection";
import { getCheckoutCurrentStep } from "./utils";

export const FORM: string = "QUICK_ENROL_FORM";
export const CONTACT_ENTITY_NAME: string = "Contact";

interface Props {
  getContactTags?: NoArgFunction;
  getCountries?: NoArgFunction;
  getLanguages?: NoArgFunction;
  getContactsRelationTypes?: NoArgFunction;
  getContactsConcessionTypes?: NoArgFunction;
  getActiveFundingContracts?: NoArgFunction;
  getQePermissions?: () => void;
  getTaxTypes?: NoArgFunction;
  getDefaultTerms?: NoArgFunction;
  clearState?: NoArgFunction;
  changeStep?: (step: number) => void;
  checkoutStep?: number;
  match?: any;
}

const Checkout = React.memo<Props>(props => {
  const {
    getContactTags,
    getCountries,
    getLanguages,
    getContactsRelationTypes,
    getContactsConcessionTypes,
    getTaxTypes,
    changeStep,
    checkoutStep,
    getDefaultTerms,
    clearState,
    getActiveFundingContracts,
    getQePermissions
  } = props;

  const [paymentStatus, setPaymentStatus] = React.useState("");

  React.useEffect(() => {
    getContactTags();
    getCountries();
    getLanguages();
    getContactsRelationTypes();
    getContactsConcessionTypes();
    getTaxTypes();
    getDefaultTerms();
    getActiveFundingContracts();
    getQePermissions();

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
  getContactTags: () => dispatch(getContactTags()),
  getCountries: () => dispatch(getCountries()),
  getLanguages: () => dispatch(getLanguages()),
  getContactsRelationTypes: () => dispatch(getContactsRelationTypes()),
  getContactsConcessionTypes: () => dispatch(getContactsConcessionTypes()),
  getDefaultTerms: () => dispatch(getDefaultInvoiceTerms()),
  getTaxTypes: () => dispatch(getContactsTaxTypes()),
  changeStep: (step: number) => dispatch(changeStep(step)),
  clearState: () => {
    dispatch(checkoutClearState());
  },
  getActiveFundingContracts: () => dispatch(getActiveFundingContracts(true)),
  getQePermissions: () => {
    dispatch(checkPermissions({ keyCode: "ENROLMENT_CREATE" }));
    dispatch(checkPermissions({ path: "/a/v1/list/plain?entity=Enrolment", method: "GET" }));
    dispatch(checkPermissions({ path: "/a/v1/list/plain?entity=PriorLearning", method: "GET" }));
    dispatch(checkPermissions({ path: "/a/v1/list/plain?entity=Outcome", method: "GET" }));
    dispatch(checkPermissions({ path: "/a/v1/list/plain?entity=Certificate", method: "GET" }));
  },
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(Checkout);
