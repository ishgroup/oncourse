/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { FundingSource } from "@api/model";
import React from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { showConfirm } from "../../../../common/actions";
import { ApiMethods } from "../../../../model/common/apiHandlers";
import { deleteFundingContract, getFundingContracts, saveFundingContracts } from "./actions";
import FundingContractsForm from "./components/FundingContractsForm";

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
