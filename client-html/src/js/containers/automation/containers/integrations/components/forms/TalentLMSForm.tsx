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

class TalentLMSFormBase extends React.Component<any, any> {
  constructor(props) {
    super(props);

    // Initializing form with values
    props.dispatch(initialize("TalentLMSForm", props.item));
  }

  componentDidUpdate(prevProps) {
    if (prevProps.item.id !== this.props.item.id) {
      // Reinitializing form with values
      this.props.dispatch(initialize("TalentLMSForm", this.props.item));
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

        <FormField name="fields.baseUrl" label="Base url" type="text" fullWidth />
        <FormField name="fields.apiKey" label="API key" type="text" fullWidth />
      </form>
    );
  }
}

export const TalentLMSForm = reduxForm({
  form: "TalentLMSForm",
  onSubmitFail
})(connect<any, any, any>(null, null)(TalentLMSFormBase));
