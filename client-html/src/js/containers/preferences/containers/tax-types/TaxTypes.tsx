/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Account, Tax } from "@api/model";
import { ShowConfirmCaller, sortDefaultSelectItems } from "ish-ui";
import * as React from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { getFormValues } from "redux-form";
import { showConfirm } from "../../../../common/actions";
import getTimestamps from "../../../../common/utils/timestamps/getTimestamps";
import { Fetch } from "../../../../model/common/Fetch";
import { State } from "../../../../reducers/state";
import { getPlainAccounts } from "../../../entities/accounts/actions";
import { deleteTaxType, getTaxTypes, updateTaxTypes } from "../../actions";
import TaxTypesForm from "./components/TaxTypesForm";

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
  openConfirm: ShowConfirmCaller;
  history: any;
  nextLocation: string;
}

class TaxTypes extends React.Component<Props, any> {
  componentDidMount() {
    this.props.getTypes();
    this.props.getAccounts();
  }

  render() {
    const {
 taxTypes, data, accounts, updateTaxTypes, deleteTaxType, fetch, timestamps, openConfirm,
      nextLocation
} = this.props;
    const created = timestamps && timestamps[0];
    const modified = timestamps && timestamps[1];

    const assetAccounts = accounts
      && accounts
        .filter(account => account.type === "asset")
        .map(item => ({
          value: Number(item.id),
          label: `${item.description} ${item.accountCode}`
        }));

    assetAccounts.sort(sortDefaultSelectItems);

    const liabilityAccounts = accounts
      && accounts
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
  accounts: state.plainSearchRecords.Account.items,
  fetch: state.fetch,
  nextLocation: state.nextLocation,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  getTypes: () => dispatch(getTaxTypes()),
  getAccounts: () => getPlainAccounts(dispatch),
  updateTaxTypes: (taxTypes: Tax[]) => dispatch(updateTaxTypes(taxTypes)),
  deleteTaxType: (id: string) => dispatch(deleteTaxType(id)),
  openConfirm: props => dispatch(showConfirm(props))
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(TaxTypes);
