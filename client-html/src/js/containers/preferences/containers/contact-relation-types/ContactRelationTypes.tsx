/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { getFormValues } from "redux-form";
import { ContactRelationType } from "@api/model";
import { updateContactRelationTypes, deleteContactRelationType, getContactRelationTypes } from "../../actions";
import { State } from "../../../../reducers/state";
import { Fetch } from "../../../../model/common/Fetch";
import ContactRelationTypesForm from "./components/ContactRelationTypesForm";
import getTimestamps from "../../../../common/utils/timestamps/getTimestamps";
import { showConfirm } from "../../../../common/actions";
import { ShowConfirmCaller } from "../../../../model/common/Confirm";

interface Props {
  getTypes: () => void;
  updateContactRelationTypes: (contactRelationTypes: ContactRelationType[]) => void;
  deleteContactRelationType: (id: string) => void;
  contactRelationTypes: ContactRelationType[];
  data: ContactRelationType[];
  timestamps: Date[];
  fetch: Fetch;
  openConfirm?: ShowConfirmCaller;
}

class ContactRelationTypes extends React.Component<Props, any> {
  componentDidMount() {
    this.props.getTypes();
  }

  render() {
    const {
      updateContactRelationTypes,
      deleteContactRelationType,
      data,
      contactRelationTypes,
      fetch,
      timestamps,
      openConfirm
    } = this.props;

    const created = timestamps && timestamps[0];
    const modified = timestamps && timestamps[1];

    const form = <ContactRelationTypesForm />;

    const componentForm = React.cloneElement(form, {
      created,
      modified,
      openConfirm,
      contactRelationTypes,
      data,
      fetch,
      onUpdate: updateContactRelationTypes,
      onDelete: deleteContactRelationType
    });

    return <div>{contactRelationTypes && componentForm}</div>;
  }
}

const mapStateToProps = (state: State) => ({
  data: getFormValues("ContactRelationTypesForm")(state),
  contactRelationTypes: state.preferences.contactRelationTypes,
  timestamps: state.preferences.contactRelationTypes && getTimestamps(state.preferences.contactRelationTypes),
  fetch: state.fetch
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
    getTypes: () => dispatch(getContactRelationTypes()),
    updateContactRelationTypes: (contactRelationTypes: ContactRelationType[]) =>
      dispatch(updateContactRelationTypes(contactRelationTypes)),
    deleteContactRelationType: (id: string) => dispatch(deleteContactRelationType(id)),
    openConfirm: props => dispatch(showConfirm(props))
  });

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(ContactRelationTypes);
