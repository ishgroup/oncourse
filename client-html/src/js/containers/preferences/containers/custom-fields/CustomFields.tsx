/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { getFormValues } from "redux-form";
import { CustomFieldType } from "@api/model";
import { updateCustomFields, deleteCustomField, getCustomFields } from "../../actions";
import { State } from "../../../../reducers/state";
import { Fetch } from "../../../../model/common/Fetch";
import CustomFieldsForm from "./components/CustomFieldsForm";
import getTimestamps from "../../../../common/utils/timestamps/getTimestamps";
import { showConfirm } from "../../../../common/actions";

interface Props {
  getFields: () => void;
  updateCustomFields: (customFields: CustomFieldType[]) => void;
  deleteCustomField: (id: string) => void;
  customFields: CustomFieldType[];
  data: CustomFieldType[];
  timestamps: Date[];
  fetch: Fetch;
  openConfirm?: (onConfirm: any, confirmMessage?: string) => void;
}

class CustomFields extends React.Component<Props, any> {
  componentDidMount() {
    this.props.getFields();
  }

  render() {
    const {
      customFields, data, updateCustomFields, deleteCustomField, fetch, timestamps, openConfirm
    } = this.props;

    const created = timestamps && timestamps[0];
    const modified = timestamps && timestamps[1];

    const form = <CustomFieldsForm />;

    const componentForm = React.cloneElement(form, {
      created,
      modified,
      openConfirm,
      customFields,
      data,
      fetch,
      onUpdate: updateCustomFields,
      onDelete: deleteCustomField
    });

    return <div>{customFields && componentForm}</div>;
  }
}

const mapStateToProps = (state: State) => ({
  data: getFormValues("CustomFieldsForm")(state),
  customFields: state.preferences.customFields,
  timestamps: state.preferences.customFields && getTimestamps(state.preferences.customFields),
  fetch: state.fetch
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  getFields: () => dispatch(getCustomFields()),
  updateCustomFields: (customFields: CustomFieldType[]) => dispatch(updateCustomFields(customFields)),
  deleteCustomField: (id: string) => dispatch(deleteCustomField(id)),
  openConfirm: (onConfirm: any, confirmMessage?: string) => dispatch(showConfirm(onConfirm, confirmMessage))
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(CustomFields);
