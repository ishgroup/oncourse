/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { getTaxTypes, updateTaxTypes, deleteTaxType } from "../../actions";
import { getFormValues } from "redux-form";
import { State } from "../../../../reducers/state";
import { Tax, Account } from "@api/model";
import { Fetch } from "../../../../model/common/Fetch";
import TaxTypesForm from "./components/TaxTypesForm";
import getTimestamps from "../../../../common/utils/timestamps/getTimestamps";
import { sortDefaultSelectItems } from "../../../../common/utils/common";
import { getPlainAccounts } from "../../../entities/accounts/actions";
import { setNextLocation, showConfirm } from "../../../../common/actions";

interface Props {
  getTypes: () => void;
  getAccounts: () => void;
  updateTaxTypes: (taxTypes: Tax[]) => void;
  deleteTaxType: (id: string) => void;
  taxTypes: Tax[];
  data: Tax[];
  accounts: Account[];
  timestamps: Date[];
  fetch: Fetch;
  openConfirm: (onConfirm: any, confirmMessage?: string, confirmButtonText?: string) => void;
  history: any;
  nextLocation: string;
  setNextLocation: (nextLocation: string) => void;
}

class TaxTypes extends React.Component<Props, any> {
  componentDidMount() {
    this.props.getTypes();
    this.props.getAccounts();
  }

  render() {
    const { taxTypes, data, accounts, updateTaxTypes, deleteTaxType, fetch, timestamps, openConfirm,
      nextLocation, setNextLocation } = this.props;
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

    const liabilityAccounts =
      accounts &&
      accounts
        .filter(account => account.type === "liability")
        .map(item => ({
          value: Number(item.id),
          label: `${item.description} ${item.accountCode}`
        }));

    liabilityAccounts.sort(sortDefaultSelectItems);

    const form = <TaxTypesForm />;

    const componentForm = React.cloneElement(form, {
      created,
      modified,
      assetAccounts,
      liabilityAccounts,
      openConfirm,
      data,
      fetch,
      taxTypes,
      nextLocation,
      setNextLocation,
      onUpdate: updateTaxTypes,
      onDelete: deleteTaxType
    });

    return <div>{taxTypes && accounts && componentForm}</div>;
  }
}

const mapStateToProps = (state: State) => ({
  timestamps: state.preferences.taxTypes && getTimestamps(state.preferences.taxTypes),
  data: getFormValues("TaxTypesForm")(state),
  taxTypes: state.preferences.taxTypes,
  accounts: state.accounts.items,
  fetch: state.fetch,
  nextLocation: state.nextLocation,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    getTypes: () => dispatch(getTaxTypes()),
    getAccounts: () => dispatch(getPlainAccounts()),
    updateTaxTypes: (taxTypes: Tax[]) => dispatch(updateTaxTypes(taxTypes)),
    deleteTaxType: (id: string) => dispatch(deleteTaxType(id)),
    setNextLocation: (nextLocation: string) => dispatch(setNextLocation(nextLocation)),
    openConfirm: (onConfirm: any, confirmMessage?: string, confirmButtonText?: string) => dispatch(showConfirm(onConfirm, confirmMessage, confirmButtonText))
  };
};

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(TaxTypes);
