/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import FundingContractsForm from "./components/FundingContractsForm";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { deleteFundingContract, getFundingContracts, saveFundingContracts } from "./actions";
import { FundingSource } from "@api/model";
import { ApiMethods } from "../../../../model/common/apiHandlers";
import { showConfirm } from "../../../../common/actions";

class FundingContracts extends React.Component<any, any> {
  componentDidMount() {
    this.props.onInit();
  }

  render() {
    const { onSave, onDelete, openConfirm } = this.props;
    return (
      <div>
        <FundingContractsForm onSave={onSave} onDelete={onDelete} openConfirm={openConfirm} />
      </div>
    );
  }
}

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    onInit: () => dispatch(getFundingContracts()),
    onSave: (items: FundingSource[], method: ApiMethods) => dispatch(saveFundingContracts(items, method)),
    onDelete: (id: number) => dispatch(deleteFundingContract(id)),
    openConfirm: props => dispatch(showConfirm(props))
  };
};

export default connect<any, any, any>(null, mapDispatchToProps)(FundingContracts);
