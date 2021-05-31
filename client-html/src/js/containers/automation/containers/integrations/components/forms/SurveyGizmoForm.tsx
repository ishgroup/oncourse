/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import { FormControlLabel } from "@material-ui/core";
import { reduxForm, initialize } from "redux-form";
import { connect } from "react-redux";
import FormField from "../../../../../../common/components/form/form-fields/FormField";
import CustomAppBar from "../../../../../../common/components/layout/CustomAppBar";
import RouteChangeConfirm from "../../../../../../common/components/dialog/confirm/RouteChangeConfirm";
import { onSubmitFail } from "../../../../../../common/utils/highlightFormClassErrors";

class SurveyGizmoBaseForm extends React.Component<any, any> {
  constructor(props) {
    super(props);

    // Initializing form with values
    props.dispatch(initialize("SurveyGizmoForm", props.item));
  }

  componentDidUpdate(prevProps) {
    if (prevProps.item.id !== this.props.item.id) {
      // Reinitializing form with values
      this.props.dispatch(initialize("SurveyGizmoForm", this.props.item));
    }
  }

  render() {
    const {
     AppBarContent, handleSubmit, onSubmit, dirty, form
    } = this.props;

    return (
      <form onSubmit={handleSubmit(onSubmit)}>
        {dirty && <RouteChangeConfirm form={form} when={dirty} />}
        <CustomAppBar>
          <AppBarContent />
        </CustomAppBar>

        <FormField name="fields.user" label="User" type="text" fullWidth />
        <FormField name="fields.password" label="Password" type="password" fullWidth />
        <FormField name="fields.surveyId" label="Survey ID" type="text" fullWidth />
        <FormField
          name="fields.courseTag"
          label="Only activate for enrolments in courses tagged with"
          type="text"
          fullWidth
        />

        <div className="flex-column pt-2">
          <FormControlLabel
            control={<FormField name="fields.sendOnEnrolmentSuccess" color="primary" type="checkbox" />}
            label="Send on successful enrolment"
          />
          <FormControlLabel
            control={<FormField name="fields.sendOnEnrolmentCompletion" color="primary" type="checkbox" />}
            label="Send on completion"
          />
        </div>
      </form>
    );
  }
}

export const SurveyGizmoForm = reduxForm({
  form: "SurveyGizmoForm",
  onSubmitFail
})(connect<any, any, any>(null, null)(SurveyGizmoBaseForm));
