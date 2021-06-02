/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import { connect } from "react-redux";
import { reduxForm, initialize } from "redux-form";
import FormField from "../../../../../../common/components/form/form-fields/FormField";
import CustomAppBar from "../../../../../../common/components/layout/CustomAppBar";
import RouteChangeConfirm from "../../../../../../common/components/dialog/confirm/RouteChangeConfirm";
import { onSubmitFail } from "../../../../../../common/utils/highlightFormClassErrors";

class MicropowerBaseForm extends React.Component<any, any> {
  constructor(props) {
    super(props);

    // Initializing form with values
    props.dispatch(initialize("MicropowerForm", props.item));
  }

  componentDidUpdate(prevProps) {
    if (prevProps.item.id !== this.props.item.id) {
      // Reinitializing form with values
      this.props.dispatch(initialize("MicropowerForm", this.props.item));
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

        <FormField name="fields.identity" label="Identity" type="text" fullWidth />
        <FormField name="fields.signature" label="Signature" type="text" fullWidth />
        <FormField name="fields.clientId" label="Client Id" type="text" fullWidth />
        <FormField name="fields.productSku" label="Product Sku" type="text" fullWidth />
      </form>
    );
  }
}

export const MicropowerForm = reduxForm({
  form: "MicropowerForm",
  onSubmitFail
})(connect<any, any, any>(null, null)(MicropowerBaseForm));
