/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import Grid from "@material-ui/core/Grid";
import Typography from "@material-ui/core/Typography";
import {
  Form, reduxForm, initialize, getFormValues
} from "redux-form";
import isEmpty from "lodash.isempty";
import { connect } from "react-redux";
import FormField from "../../../../../common/components/form/form-fields/FormField";
import FormSubmitButton from "../../../../../common/components/form/FormSubmitButton";
import { onSubmitFail } from "../../../../../common/utils/highlightFormClassErrors";
import * as Model from "../../../../../model/preferences/Maintenance";
import { validateSingleMandatoryField } from "../../../../../common/utils/validation";
import { FormModelSchema } from "../../../../../model/preferences/FormModelShema";
import { State } from "../../../../../reducers/state";
import CustomAppBar from "../../../../../common/components/layout/CustomAppBar";
import RouteChangeConfirm from "../../../../../common/components/dialog/confirm/RouteChangeConfirm";
import AppBarHelpMenu from "../../../../../common/components/form/AppBarHelpMenu";
import { getManualLink } from "../../../../../common/utils/getManualLink";
import { PREFERENCES_AUDITS_LINK } from "../../../constants";

const manualUrl = getManualLink("generalPrefs_maintenance");

class MaintenanceBaseForm extends React.Component<any, any> {
  private formModel: FormModelSchema;

  constructor(props) {
    super(props);

    if (!isEmpty(props.formData)) {
      props.dispatch(initialize("MaintenanceForm", props.formData));
    }

    this.formModel = props.formatModel(Model);
  }

  validateTimeoutRange(value) {
    return +value > 360 || +value < 1 ? "Please enter a valid number between 1 and 360" : undefined;
  }

  render() {
    const {
      handleSubmit, onSave, dirty, data, invalid, form
    } = this.props;

    return (
      <Form className="container" onSubmit={handleSubmit(onSave)}>
        <RouteChangeConfirm form={form} when={dirty} />

        <CustomAppBar>
          <Grid container>
            <Grid item xs={12} className="centeredFlex">
              <Typography className="appHeaderFontSize" color="inherit" noWrap>
                Maintenance
              </Typography>

              <div className="flex-fill" />

              {data && (
                <AppBarHelpMenu
                  created={data.created}
                  modified={data.modified}
                  auditsUrl={PREFERENCES_AUDITS_LINK}
                  manualUrl={manualUrl}
                />
              )}

              <FormSubmitButton
                disabled={!dirty}
                invalid={invalid}
              />
            </Grid>
          </Grid>
        </CustomAppBar>

        <Typography className="heading mb-2" variant="subtitle1">
          Automatic logout
        </Typography>

        <Grid container>
          <Grid item xs={12} sm={6}>
            <FormField
              type="number"
              name={this.formModel.LogoutTimeout.uniqueKey}
              label="Minutes of inactivity until automatic logout"
              parse={val => val || "0"}
              validate={[validateSingleMandatoryField, this.validateTimeoutRange]}
              fullWidth
            />
          </Grid>
        </Grid>
      </Form>
    );
  }
}

const mapStateToProps = (state: State) => ({
  values: getFormValues("MaintenanceForm")(state)
});

const MaintenanceForm = reduxForm({
  form: "MaintenanceForm",
  onSubmitFail
})(
  connect<any, any, any>(
    mapStateToProps,
    null
  )(MaintenanceBaseForm)
);

export default MaintenanceForm;
