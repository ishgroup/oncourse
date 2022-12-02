/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import { connect } from "react-redux";
import { initialize, reduxForm } from "redux-form";
import FormField from "../../../../../../common/components/form/formFields/FormField";
import { onSubmitFail } from "../../../../../../common/utils/highlightFormErrors";

class MailchimpBaseForm extends React.Component<any, any> {
  constructor(props) {
    super(props);

    // Initializing form with values
    props.dispatch(initialize("MailchimpForm", props.item));
  }

  componentDidUpdate(prevProps) {
    if (prevProps.item.id !== this.props.item.id) {
      // Reinitializing form with values
      this.props.dispatch(initialize("MailchimpForm", this.props.item));
    }
  }

  render() {
    const {
     handleSubmit, onSubmit, AppBarContent
    } = this.props;

    return (
      <form onSubmit={handleSubmit(onSubmit)}>
        <AppBarContent>
          <FormField name="fields.apiKey" label="API key" type="text" className="mb-2"/>
          <FormField name="fields.listId" label="Audience ID" type="text" className="mb-2" />
        </AppBarContent>
      </form>
    );
  }
}

export const MailchimpForm = reduxForm({
  form: "MailchimpForm",
  onSubmitFail
})(connect<any, any, any>(null, null)(MailchimpBaseForm));
