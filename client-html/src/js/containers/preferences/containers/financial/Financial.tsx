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
        <FormContainer data={financial} accounts={accounts} category={Categories.financial} form={<FinancialForm />} />
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
