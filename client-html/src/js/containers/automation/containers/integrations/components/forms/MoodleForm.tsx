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

class MoodleBaseForm extends React.Component<any, any> {
  constructor(props) {
    super(props);

    // Initializing form with values
    props.dispatch(initialize("MoodleForm", props.item));
  }

  componentDidUpdate(prevProps) {
    if (prevProps.item.id !== this.props.item.id) {
      // Reinitializing form with values
      this.props.dispatch(initialize("MoodleForm", this.props.item));
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

        <FormField name="fields.baseUrl" label="Base URL" type="text" fullWidth />
        <FormField name="fields.username" label="Username" type="text" fullWidth />
        <FormField name="fields.password" label="Password" type="password" fullWidth />
        <FormField name="fields.serviceName" label="Service name" type="text" fullWidth />
        <FormField
          name="fields.courseTag"
          label="Only activate for enrolments in courses tagged with"
          listSpacing
          type="text"
          fullWidth
        />
      </form>
    );
  }
}

export const MoodleForm = reduxForm({
  form: "MoodleForm",
  onSubmitFail
})(connect<any, any, any>(null, null)(MoodleBaseForm));
