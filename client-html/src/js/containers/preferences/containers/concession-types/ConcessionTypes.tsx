/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { getConcessionTypes, updateConcessionTypes, deleteConcessionType } from "../../actions";
import { getFormValues } from "redux-form";
import { State } from "../../../../reducers/state";
import { ConcessionType } from "@api/model";
import { Fetch } from "../../../../model/common/Fetch";
import ConcessionTypesForm from "./components/ConcessionTypesForm";
import getTimestamps from "../../../../common/utils/timestamps/getTimestamps";
import { showConfirm } from "../../../../common/actions";

interface Props {
  getTypes: () => void;
  updateConcessionTypes: (taxTypes: ConcessionType[]) => void;
  deleteConcessionType: (id: string) => void;
  concessionTypes: ConcessionType[];
  data: ConcessionType[];
  timestamps: Date[];
  fetch: Fetch;
  openConfirm?: (onConfirm: any, confirmMessage?: string, confirmButtonText?: string) => void;
}

class ConcessionTypes extends React.Component<Props, any> {
  componentDidMount() {
    this.props.getTypes();
  }

  render() {
    const {
      concessionTypes,
      data,
      updateConcessionTypes,
      deleteConcessionType,
      fetch,
      timestamps,
      openConfirm
    } = this.props;

    const created = timestamps && timestamps[0];
    const modified = timestamps && timestamps[1];

    const form = <ConcessionTypesForm />;

    const componentForm = React.cloneElement(form, {
      created,
      modified,
      openConfirm,
      concessionTypes,
      data,
      fetch,
      onUpdate: updateConcessionTypes,
      onDelete: deleteConcessionType
    });

    return <div>{concessionTypes && componentForm}</div>;
  }
}

const mapStateToProps = (state: State) => ({
  data: getFormValues("ConcessionTypesForm")(state),
  concessionTypes: state.preferences.concessionTypes,
  timestamps: state.preferences.concessionTypes && getTimestamps(state.preferences.concessionTypes),
  fetch: state.fetch
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    getTypes: () => dispatch(getConcessionTypes()),
    updateConcessionTypes: (taxTypes: ConcessionType[]) => dispatch(updateConcessionTypes(taxTypes)),
    deleteConcessionType: (id: string) => dispatch(deleteConcessionType(id)),
    openConfirm: (onConfirm: any, confirmMessage?: string, confirmButtonText?: string) => dispatch(showConfirm(onConfirm, confirmMessage, confirmButtonText))
  };
};

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(ConcessionTypes);
