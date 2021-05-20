/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import { reduxForm, initialize } from "redux-form";
import { connect } from "react-redux";
import FormField from "../../../../../../common/components/form/form-fields/FormField";
import CustomAppBar from "../../../../../../common/components/layout/CustomAppBar";
import RouteChangeConfirm from "../../../../../../common/components/dialog/confirm/RouteChangeConfirm";
import { onSubmitFail } from "../../../../../../common/utils/highlightFormClassErrors";

class CloudAssessBaseForm extends React.Component<any, any> {
  constructor(props) {
    super(props);

    // Initializing form with values
    props.dispatch(initialize("CloudAssessForm", props.item));
  }

  componentDidUpdate(prevProps) {
    if (prevProps.item.id !== this.props.item.id) {
      // Reinitializing form with values
      this.props.dispatch(initialize("CloudAssessForm", this.props.item));
    }
  }

  render() {
    const {
     handleSubmit, onSubmit, appBarContent, dirty, form
    } = this.props;

    return (
      <form onSubmit={handleSubmit(onSubmit)}>
        {dirty && <RouteChangeConfirm form={form} when={dirty} />}
        <CustomAppBar>{appBarContent}</CustomAppBar>

        <FormField type="text" name="fields.username" label="Username" fullWidth />
        <FormField type="text" name="fields.apiKey" label="API key" fullWidth />
        <FormField type="text" name="fields.orgId" label="Organisation ID" fullWidth />
      </form>
    );
  }
}

export const CloudAssessForm = reduxForm({
  form: "CloudAssessForm",
  onSubmitFail
})(connect<any, any, any>(null, null)(CloudAssessBaseForm));
