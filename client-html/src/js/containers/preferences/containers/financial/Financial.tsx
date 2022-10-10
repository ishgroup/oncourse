/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import * as React from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { State } from "../../../../reducers/state";
import { Categories } from "../../../../model/preferences";
import FormContainer from "../FormContainer";
import FinancialForm from "./components/FinancialForm";
import { Account } from "@api/model";
import { getPlainAccounts } from "../../../entities/accounts/actions";

interface Props {
  financial: any;
  accounts: Account[];
  onInit: () => void;
}

class Financial extends React.Component<Props, any> {
  componentDidMount() {
    this.props.onInit();
  }

  render() {
    const { financial, accounts } = this.props;

    return (
      <div>
        <FormContainer
          data={financial}
          accounts={accounts}
          category={Categories.financial}
          form={formRoleName => <FinancialForm formRoleName={formRoleName} />}
          formName="FinancialForm"
        />
      </div>
    );
  }
}

const mapStateToProps = (state: State) => ({
  financial: state.preferences.financial,
  accounts: state.plainSearchRecords.Account.items
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    onInit: () => getPlainAccounts(dispatch)
  };
};

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(Financial);
