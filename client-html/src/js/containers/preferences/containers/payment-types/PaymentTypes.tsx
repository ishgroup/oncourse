/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Account, PaymentMethod } from "@api/model";
import { ShowConfirmCaller, sortDefaultSelectItems } from "ish-ui";
import * as React from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { getFormMeta, getFormValues } from "redux-form";
import { showConfirm } from "../../../../common/actions";
import getTimestamps from "../../../../common/utils/timestamps/getTimestamps";
import { Fetch } from "../../../../model/common/Fetch";
import { State } from "../../../../reducers/state";
import { getPlainAccounts } from "../../../entities/accounts/actions";
import { deletePaymentType, getPaymentTypes, updatePaymentTypes } from "../../actions";
import PaymentTypesForm from "./components/PaymentTypesForm";

interface Props {
  getTypes: () => void;
  getAccounts: () => void;
  updatePaymentTypes: (paymentTypes: PaymentMethod[]) => void;
  deletePaymentType: (id: string) => void;
  paymentTypes: PaymentMethod[];
  data: PaymentMethod[];
  accounts: Account[];
  timestamps: Date[];
  fetch: Fetch;
  touched: any;
  openConfirm?: ShowConfirmCaller;
}

class PaymentTypes extends React.Component<Props, any> {
  componentDidMount() {
    this.props.getTypes();
    this.props.getAccounts();
  }

  render() {
    const {
      paymentTypes,
      data,
      accounts,
      updatePaymentTypes,
      deletePaymentType,
      fetch,
      touched,
      timestamps,
      openConfirm
    } = this.props;
    const created = timestamps && timestamps[0];
    const modified = timestamps && timestamps[1];

    const assetAccounts = accounts
      && accounts
        .map(item => ({
          value: Number(item.id),
          label: `${item.description} ${item.accountCode}`
        }));

    assetAccounts.sort(sortDefaultSelectItems);

    const form = <PaymentTypesForm />;

    const componentForm = React.cloneElement(form, {
      created,
      modified,
      assetAccounts,
      openConfirm,
      data,
      fetch,
      touched,
      paymentTypes,
      onUpdate: updatePaymentTypes,
      onDelete: deletePaymentType
    });

    return <div>{paymentTypes && accounts && componentForm}</div>;
  }
}

const mapStateToProps = (state: State) => ({
  timestamps: state.preferences.paymentTypes && getTimestamps(state.preferences.paymentTypes),
  data: getFormValues("PaymentTypesForm")(state),
  touched: getFormMeta("PaymentTypesForm")(state),
  paymentTypes: state.preferences.paymentTypes,
  accounts: state.plainSearchRecords.Account.items,
  fetch: state.fetch
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
    getTypes: () => dispatch(getPaymentTypes()),
    getAccounts: () => getPlainAccounts(dispatch, "asset"),
    updatePaymentTypes: (paymentTypes: PaymentMethod[]) => dispatch(updatePaymentTypes(paymentTypes)),
    deletePaymentType: (id: string) => dispatch(deletePaymentType(id)),
    openConfirm: props => dispatch(showConfirm(props))
  });

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(PaymentTypes);
