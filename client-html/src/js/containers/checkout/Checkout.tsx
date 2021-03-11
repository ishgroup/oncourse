/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import { Dispatch } from "redux";
import { connect } from "react-redux";
import createStyles from "@material-ui/core/styles/createStyles";
import withStyles from "@material-ui/core/styles/withStyles";
import { NoArgFunction } from "../../model/common/CommonFunctions";
import { State } from "../../reducers/state";
import { AppTheme } from "../../model/common/Theme";
import { getActiveFundingContracts } from "../avetmiss-export/actions";
import { getCountries, getLanguages } from "../preferences/actions";
import {
  getContactsConcessionTypes,
  getContactsRelationTypes,
  getContactsTaxTypes,
  getContactTags
} from "../entities/contacts/actions";
import { checkPermissions, showConfirm } from "../../common/actions";
import { getCustomFieldTypes } from "../entities/customFieldTypes/actions";
import { getDefaultInvoiceTerms } from "../entities/invoices/actions";
import { changeStep, checkoutClearState } from "./actions";
import CheckoutSelection from "./components/CheckoutSelection";
import { getCheckoutCurrentStep } from "./utils";
import { clearPlainPreviousCreditRecords } from "./actions/checkoutSummary";

export const FORM: string = "QUICK_ENROL_FORM";
export const CONTACT_ENTITY_NAME: string = "Contact";

interface Props {
  getContactTags?: NoArgFunction;
  getCountries?: NoArgFunction;
  getLanguages?: NoArgFunction;
  getContactsRelationTypes?: NoArgFunction;
  getContactsConcessionTypes?: NoArgFunction;
  getCustomFieldTypes?: NoArgFunction;
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
    getCustomFieldTypes,
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
    getCustomFieldTypes();
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
  getCustomFieldTypes: () => dispatch(getCustomFieldTypes(CONTACT_ENTITY_NAME)),
  getDefaultTerms: () => dispatch(getDefaultInvoiceTerms()),
  getTaxTypes: () => dispatch(getContactsTaxTypes()),
  openConfirm: (onConfirm: any, confirmMessage?: string, confirmButtonText?: string) =>
    dispatch(showConfirm(onConfirm, confirmMessage, confirmButtonText)),
  changeStep: (step: number) => dispatch(changeStep(step)),
  clearState: () => {
    dispatch(checkoutClearState());
    dispatch(clearPlainPreviousCreditRecords());
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
