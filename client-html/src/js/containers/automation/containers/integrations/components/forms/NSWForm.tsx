/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React from "react";
import { reduxForm, initialize } from "redux-form";
import FormField from "../../../../../../common/components/form/form-fields/FormField";
import CustomAppBar from "../../../../../../common/components/layout/CustomAppBar";
import RouteChangeConfirm from "../../../../../../common/components/dialog/confirm/RouteChangeConfirm";
import { onSubmitFail } from "../../../../../../common/utils/highlightFormClassErrors";
import { ServiceNSWVoucherTypes } from "../../../../../../model/automation/integrations/IntegrationsFields";

class NSWBaseForm extends React.Component<any, any> {
  constructor(props) {
    super(props);
    // Initializing form with values
    props.dispatch(initialize("NSWForm", props.item));
  }

  componentDidUpdate(prevProps) {
    if (prevProps.item.id !== this.props.item.id) {
      // Reinitializing form with values
      this.props.dispatch(initialize("NSWForm", this.props.item));
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
        <FormField name="fields.voucher" label="Voucher type" type="select" items={ServiceNSWVoucherTypes} required fullWidth />
        <FormField name="fields.chanelCode" label="Store channel code" type="text" required fullWidth />
        <FormField name="fields.terminalId" label="POS terminal id" type="text" required fullWidth />
        <FormField name="fields.programme" label="Programme" type="text" required fullWidth />
        <FormField name="fields.apiKey" label="API key" type="password" required fullWidth />
      </form>
    );
  }
}

export const NSWForm = reduxForm({
  form: "NSWForm",
  onSubmitFail
})(NSWBaseForm);
