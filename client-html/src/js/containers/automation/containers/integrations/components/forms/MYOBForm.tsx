/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import { reduxForm, initialize } from "redux-form";
import Typography from "@material-ui/core/Typography";
import Button from "../../../../../../common/components/buttons/Button";
import FormField from "../../../../../../common/components/form/form-fields/FormField";
import CustomAppBar from "../../../../../../common/components/layout/CustomAppBar";
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
     handleSubmit, onSubmit, appBarContent, dirty, item, form
    } = this.props;
    const configured = item && item.id;

    const configuredLabel = "MYOB access has been set up. Press ‘Configure’ to restart the configuration process.";
    const unconfiguredLabel = ' Press "Configure" to proceed with authorising onCourse to access your MYOB account.';

    return (
      <form onSubmit={handleSubmit(onSubmit)}>
        {dirty && <RouteChangeConfirm form={form} when={dirty} />}
        <CustomAppBar>{appBarContent}</CustomAppBar>
        <FormField name="fields.baseUrl" label="Base URL" type="text" fullWidth />
        <FormField name="fields.user" label="User" type="text" fullWidth />
        <FormField name="fields.password" type="password" label="Password" fullWidth />

        <Typography variant="caption" component="div">
          {configured ? configuredLabel : unconfiguredLabel}
        </Typography>

        <a href="myobAuthAccess" className="link">
          <Button text="Configure" variant="contained" className="mt-1" />
        </a>
      </form>
    );
  }
}

export const MYOBForm = reduxForm({
  form: "MYOBForm",
  onSubmitFail
})(MYOBBaseForm);
