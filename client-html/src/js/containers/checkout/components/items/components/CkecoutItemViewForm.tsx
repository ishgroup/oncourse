/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
import React from "react";
import { connect } from "react-redux";
import { InjectedFormProps, reduxForm } from "redux-form";
import { State } from "../../../../../reducers/state";

export const CHECKOUT_ITEM_EDIT_VIEW_FORM = "CheckoutItemEditViewForm";

interface CheckoutItemViewFormProps extends Partial<InjectedFormProps>{
  EditViewComponent?: any;
  checkoutItemRecord?: any;
  classes?: any;
}

const CkecoutItemViewForm = React.memo<CheckoutItemViewFormProps>(({ EditViewComponent, checkoutItemRecord, ...rest }) => (
  <form autoComplete="off" className="w-100">
    <EditViewComponent values={checkoutItemRecord} {...rest} />
  </form>
));

const mapStateToProps = (state: State) => ({
  checkoutItemRecord: state.checkout.itemEditRecord
});

export default reduxForm<any, any>({
  form: CHECKOUT_ITEM_EDIT_VIEW_FORM
})(connect<any, any, any>(mapStateToProps, null)(CkecoutItemViewForm));
