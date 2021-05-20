/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Typography } from "@material-ui/core";
import * as React from "react";
import { connect } from "react-redux";
import {
  reduxForm, initialize, getFormValues, change
} from "redux-form";
import Button from "../../../../../../common/components/buttons/Button";
import FormField from "../../../../../../common/components/form/form-fields/FormField";
import Uneditable from "../../../../../../common/components/form/Uneditable";
import CustomAppBar from "../../../../../../common/components/layout/CustomAppBar";
import RouteChangeConfirm from "../../../../../../common/components/dialog/confirm/RouteChangeConfirm";
import { onSubmitFail } from "../../../../../../common/utils/highlightFormClassErrors";
import { validateSingleMandatoryField } from "../../../../../../common/utils/validation";
import { State } from "../../../../../../reducers/state";

class GoogleClassroomBaseForm extends React.Component<any, any> {
  constructor(props) {
    super(props);

    // Initializing form with values
    props.dispatch(initialize("GoogleClassroomForm", props.item));
  }

  componentDidUpdate(prevProps) {
    if (prevProps.item.id !== this.props.item.id) {
      // Reinitializing form with values
      this.props.dispatch(initialize("GoogleClassroomForm", this.props.item));
    }
  }

  componentDidMount(): void {
    if (window.location.search) {
      const searchParam = new URLSearchParams(window.location.search);

      const state = searchParam.get("state");

      if (state) {
        const parsed = JSON.parse(state);
        this.props.dispatch(change("GoogleClassroomForm", "name", parsed.name));
        this.props.dispatch(change("GoogleClassroomForm", "fields.activationCode", searchParam.get("code")));
        this.props.dispatch(change("GoogleClassroomForm", "fields.clientSecret", parsed.secret));
        this.props.dispatch(change("GoogleClassroomForm", "fields.clientId", parsed.id));
        this.props.dispatch(change("GoogleClassroomForm", "fields.redirectUri", parsed.redirect));
      }
    }
  }

  getToken = () => {
    const { values } = this.props;

    const state = {
      name: values.name,
      secret: values.fields.clientSecret,
      id: values.fields.clientId,
      redirect: window.location.href
    };

    window.open(`https://accounts.google.com/o/oauth2/auth?access_type=offline&client_id=${values.fields.clientId}&redirect_uri=${window.location.href}&state=${JSON.stringify(state)}&response_type=code&scope=https://www.googleapis.com/auth/classroom.announcements.readonly%20https://www.googleapis.com/auth/classroom.courses%20https://www.googleapis.com/auth/classroom.guardianlinks.me.readonly%20https://www.googleapis.com/auth/classroom.push-notifications%20https://www.googleapis.com/auth/classroom.rosters.readonly%20https://www.googleapis.com/auth/classroom.student-submissions.students.readonly%20https://www.googleapis.com/auth/classroom.coursework.me.readonly%20https://www.googleapis.com/auth/classroom.rosters%20https://www.googleapis.com/auth/classroom.student-submissions.me.readonly%20https://www.googleapis.com/auth/classroom.coursework.me%20https://www.googleapis.com/auth/classroom.courses.readonly%20https://www.googleapis.com/auth/classroom.coursework.students%20https://www.googleapis.com/auth/classroom.guardianlinks.students.readonly%20https://www.googleapis.com/auth/classroom.profile.emails%20https://www.googleapis.com/auth/classroom.coursework.students.readonly%20https://www.googleapis.com/auth/classroom.guardianlinks.students%20https://www.googleapis.com/auth/classroom.profile.photos%20https://www.googleapis.com/auth/classroom.announcements`,
      "_self");
  };

  render() {
    const {
     handleSubmit, onSubmit, appBarContent, dirty, values, form
    } = this.props;

    const hasIdAndSecret = values && values.fields.clientId && values.fields.clientSecret;

    return (
      <form onSubmit={handleSubmit(onSubmit)}>
        {dirty && <RouteChangeConfirm form={form} when={dirty} />}

        <CustomAppBar>{appBarContent}</CustomAppBar>

        <FormField name="fields.clientId" label="Client id" type="text" required fullWidth />
        <FormField name="fields.clientSecret" label="Client secret" type="text" required fullWidth />
        <FormField name="fields.activationCode" type="stub" validate={validateSingleMandatoryField} disabled fullWidth />
        <Uneditable value={values && values.fields.activationCode} label="Activation Code" />

        <div>
          <Button
            text="Get activation code"
            variant="contained"
            className="mt-1"
            onClick={this.getToken}
            disabled={!hasIdAndSecret || (values && values.fields.activationCode)}
          />
        </div>

        {!hasIdAndSecret
        && <Typography variant="caption">Please fill client id and client secret fields to be able to get activation code</Typography>}
      </form>
    );
  }
}

const mapStateToProps = (state: State) => ({
  values: getFormValues("GoogleClassroomForm")(state)
});

export const GoogleClassroomForm = reduxForm({
  form: "GoogleClassroomForm",
  onSubmitFail
})(connect<any, any, any>(mapStateToProps, null)(GoogleClassroomBaseForm));
