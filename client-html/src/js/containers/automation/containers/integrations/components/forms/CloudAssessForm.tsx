/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import { initialize, reduxForm } from "redux-form";
import { connect } from "react-redux";
import FormField from "../../../../../../common/components/form/formFields/FormField";
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
     handleSubmit, onSubmit, AppBarContent
    } = this.props;

    return (
      <form onSubmit={handleSubmit(onSubmit)}>
        <AppBarContent>
          <FormField type="text" name="fields.username" label="Username" className="mb-2" fullWidth />
          <FormField type="text" name="fields.apiKey" label="API key" className="mb-2" fullWidth />
          <FormField type="text" name="fields.orgId" label="Organisation ID" className="mb-2" fullWidth />
        </AppBarContent>
      </form>
    );
  }
}

export const CloudAssessForm = reduxForm({
  form: "CloudAssessForm",
  onSubmitFail
})(connect<any, any, any>(null, null)(CloudAssessBaseForm));
