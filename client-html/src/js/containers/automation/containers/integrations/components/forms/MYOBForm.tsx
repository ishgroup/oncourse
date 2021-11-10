/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import { initialize, reduxForm } from "redux-form";
import Typography from "@mui/material/Typography";
import Button from "@mui/material/Button";
import FormField from "../../../../../../common/components/form/formFields/FormField";
import RouteChangeConfirm from "../../../../../../common/components/dialog/confirm/RouteChangeConfirm";
import { onSubmitFail } from "../../../../../../common/utils/highlightFormClassErrors";

class MYOBBaseForm extends React.Component<any, any> {
  constructor(props) {
    super(props);

    // Initializing form with values
    props.dispatch(initialize("MYOBForm", props.item));
  }

  componentDidUpdate(prevProps) {
    if (prevProps.item.id !== this.props.item.id) {
      // Reinitializing form with values
      this.props.dispatch(initialize("MYOBForm", this.props.item));
    }
  }

  render() {
    const {
     handleSubmit, onSubmit, AppBarContent, dirty, item, form
    } = this.props;
    const configured = item && item.id;

    const configuredLabel = "MYOB access has been set up. Press ‘Configure’ to restart the configuration process.";
    const unconfiguredLabel = ' Press "Configure" to proceed with authorising onCourse to access your MYOB account.';

    return (
      <form onSubmit={handleSubmit(onSubmit)}>
        {dirty && <RouteChangeConfirm form={form} when={dirty} />}

        <AppBarContent>
          <FormField name="fields.baseUrl" label="Base URL" type="text" fullWidth />
          <FormField name="fields.user" label="User" type="text" fullWidth />
          <FormField name="fields.password" type="password" label="Password" fullWidth />

          <Typography variant="caption" component="div">
            {configured ? configuredLabel : unconfiguredLabel}
          </Typography>

          <a href="myobAuthAccess" className="link">
            <Button variant="contained" className="mt-1">Configure</Button>
          </a>
        </AppBarContent>
      </form>
    );
  }
}

export const MYOBForm = reduxForm({
  form: "MYOBForm",
  onSubmitFail
})(MYOBBaseForm);
