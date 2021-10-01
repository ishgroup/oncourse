/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React from "react";
import { initialize, reduxForm } from "redux-form";
import RouteChangeConfirm from "../../../../../../common/components/dialog/confirm/RouteChangeConfirm";
import FormField from "../../../../../../common/components/form/formFields/FormField";
import CustomAppBar from "../../../../../../common/components/layout/CustomAppBar";
import { onSubmitFail } from "../../../../../../common/utils/highlightFormClassErrors";

class AzureFormBase extends React.Component<any, any> {
  constructor(props) {
    super(props);

    // Initializing form with values
    props.dispatch(initialize("AzureForm", props.item));
  }

  componentDidUpdate(prevProps) {
    if (prevProps.item.id !== this.props.item.id) {
      // Reinitializing form with values
      this.props.dispatch(initialize("AzureForm", this.props.item));
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

        <FormField type="text" name="fields.account" label="Account" required fullWidth />
        <FormField type="text" name="fields.key" label="Key" required fullWidth />
        <FormField type="text" name="fields.container" label="Container" required fullWidth />
      </form>
    );
  }
}

export const AzureForm = reduxForm({
  form: "AzureForm",
  onSubmitFail
})(AzureFormBase);
