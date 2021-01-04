/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { getPaymentTypes, updatePaymentTypes, deletePaymentType } from "../../actions";
import { getFormMeta, getFormValues } from "redux-form";
import { State } from "../../../../reducers/state";
import { Account, PaymentMethod } from "@api/model";
import { Fetch } from "../../../../model/common/Fetch";
import PaymentTypesForm from "./components/PaymentTypesForm";
import getTimestamps from "../../../../common/utils/timestamps/getTimestamps";
import { sortDefaultSelectItems } from "../../../../common/utils/common";
import { getPlainAccounts } from "../../../entities/accounts/actions";
import { showConfirm } from "../../../../common/actions";

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
  openConfirm?: (onConfirm: any, confirmMessage?: string) => void;
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

    const assetAccounts =
      accounts &&
      accounts
        .filter(account => account.type === "asset")
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
  accounts: state.accounts.items,
  fetch: state.fetch
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    getTypes: () => dispatch(getPaymentTypes()),
    getAccounts: () => dispatch(getPlainAccounts()),
    updatePaymentTypes: (paymentTypes: PaymentMethod[]) => dispatch(updatePaymentTypes(paymentTypes)),
    deletePaymentType: (id: string) => dispatch(deletePaymentType(id)),
    openConfirm: (onConfirm: any, confirmMessage?: string, confirmButtonText?: string) => dispatch(showConfirm(onConfirm, confirmMessage, confirmButtonText))
  };
};

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(PaymentTypes);
