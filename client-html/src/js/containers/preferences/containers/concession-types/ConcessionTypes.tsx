/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { getFormValues } from "redux-form";
import { ConcessionType } from "@api/model";
import { getConcessionTypes, updateConcessionTypes, deleteConcessionType } from "../../actions";
import { State } from "../../../../reducers/state";
import { Fetch } from "../../../../model/common/Fetch";
import ConcessionTypesForm from "./components/ConcessionTypesForm";
import getTimestamps from "../../../../common/utils/timestamps/getTimestamps";
import { showConfirm } from "../../../../common/actions";
import { ShowConfirmCaller } from "../../../../model/common/Confirm";

interface Props {
  getTypes: () => void;
  updateConcessionTypes: (taxTypes: ConcessionType[]) => void;
  deleteConcessionType: (id: string) => void;
  concessionTypes: ConcessionType[];
  data: ConcessionType[];
  timestamps: Date[];
  fetch: Fetch;
  openConfirm?: ShowConfirmCaller;
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

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
    getTypes: () => dispatch(getConcessionTypes()),
    updateConcessionTypes: (taxTypes: ConcessionType[]) => dispatch(updateConcessionTypes(taxTypes)),
    deleteConcessionType: (id: string) => dispatch(deleteConcessionType(id)),
    openConfirm: props => dispatch(showConfirm(props))
  });

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(ConcessionTypes);
