/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
import React from "react";
import { connect } from "react-redux";
import { initialize, reduxForm } from "redux-form";
import RouteChangeConfirm from "../../../../../../common/components/dialog/confirm/RouteChangeConfirm";
import FormField from "../../../../../../common/components/form/form-fields/FormField";
import CustomAppBar from "../../../../../../common/components/layout/CustomAppBar";
import { onSubmitFail } from "../../../../../../common/utils/highlightFormClassErrors";

class LearnDashFormBase extends React.Component<any, any> {
  constructor(props) {
    super(props);

    // Initializing form with values
    props.dispatch(initialize("LearnDashForm", props.item));
  }

  componentDidUpdate(prevProps) {
    if (prevProps.item.id !== this.props.item.id) {
      // Reinitializing form with values
      this.props.dispatch(initialize("LearnDashForm", this.props.item));
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

        <FormField type="text" name="fields.baseUrl" label="Base url" fullWidth />
        <FormField type="text" name="fields.userLogin" label="User login" fullWidth />
        <FormField
          type="password"
          name="fields.userPassword"
          label="User password"
          fullWidth
        />
      </form>
    );
  }
}

export const LearnDashForm = reduxForm({
  form: "LearnDashForm",
  onSubmitFail
})(connect<any, any, any>(null, null)(LearnDashFormBase));
