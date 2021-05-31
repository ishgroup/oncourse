/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import {
  reduxForm, initialize, getFormValues
} from "redux-form";
import { connect } from "react-redux";
import FormField from "../../../../../../common/components/form/form-fields/FormField";
import CustomAppBar from "../../../../../../common/components/layout/CustomAppBar";
import RouteChangeConfirm from "../../../../../../common/components/dialog/confirm/RouteChangeConfirm";
import { onSubmitFail } from "../../../../../../common/utils/highlightFormClassErrors";
import { State } from "../../../../../../reducers/state";

class CoassembleBaseForm extends React.Component<any, any> {
  constructor(props) {
    super(props);

    // Initializing form with values
    props.dispatch(initialize("CoassembleForm", props.item));
  }

  componentDidUpdate(prevProps) {
    if (prevProps.item.id !== this.props.item.id) {
      // Reinitializing form with values
      this.props.dispatch(initialize("CoassembleForm", this.props.item));
    }
  }

  render() {
    const {
      handleSubmit, onSubmit, AppBarContent, dirty, form
    } = this.props;

    return (
      <form onSubmit={handleSubmit(onSubmit)}>
        {dirty && <RouteChangeConfirm form={form} when={dirty} />}
        <CustomAppBar>
          <AppBarContent />
        </CustomAppBar>

        <FormField name="fields.baseUrl" label="Base Url" type="text" fullWidth />
        <FormField name="fields.apiKey" label="API key" type="text" fullWidth />
        <FormField name="fields.userId" label="User Id" type="text" fullWidth />
      </form>
    );
  }
}

const mapStateToProps = (state: State) => ({
  values: getFormValues("CoassembleForm")(state)
});

export const CoassembleForm = reduxForm({
  form: "CoassembleForm",
  onSubmitFail
})(connect<any, any, any>(mapStateToProps, null)(CoassembleBaseForm));

