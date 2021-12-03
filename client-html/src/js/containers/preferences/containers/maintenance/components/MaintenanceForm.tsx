/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import Grid from "@mui/material/Grid";
import Typography from "@mui/material/Typography";
import {
  Form, getFormValues, initialize, reduxForm
} from "redux-form";
import isEmpty from "lodash.isempty";
import { connect } from "react-redux";
import FormField from "../../../../../common/components/form/formFields/FormField";
import { onSubmitFail } from "../../../../../common/utils/highlightFormClassErrors";
import * as Model from "../../../../../model/preferences/Maintenance";
import { validateSingleMandatoryField } from "../../../../../common/utils/validation";
import { FormModelSchema } from "../../../../../model/preferences/FormModelShema";
import { State } from "../../../../../reducers/state";
import RouteChangeConfirm from "../../../../../common/components/dialog/confirm/RouteChangeConfirm";
import { getManualLink } from "../../../../../common/utils/getManualLink";
import { PREFERENCES_AUDITS_LINK } from "../../../constants";
import AppBarContainer from "../../../../../common/components/layout/AppBarContainer";

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

  componentDidUpdate() {
    // Initializing form with values
    if (!isEmpty(this.props.formData) && !this.props.initialized) {
      this.props.dispatch(initialize("MaintenanceForm", this.props.formData));
    }
  }

  validateTimeoutRange = value => (+value > 360 || +value < 1 ? "Please enter a valid number between 1 and 360" : undefined);

  render() {
    const {
      handleSubmit, onSave, dirty, data, invalid, form
    } = this.props;

    return (
      <Form className="container" onSubmit={handleSubmit(onSave)}>
        <RouteChangeConfirm form={form} when={dirty} />

        <AppBarContainer
          values={data}
          manualUrl={manualUrl}
          getAuditsUrl={PREFERENCES_AUDITS_LINK}
          disabled={!dirty}
          invalid={invalid}
          title="Maintenance"
          disableInteraction
          createdOn={values => values.created}
          modifiedOn={values => values.modified}
        >
          <Typography className="heading mb-2" variant="subtitle1">
            Automatic logout
          </Typography>

          <Grid container columnSpacing={3}>
            <Grid item xs={12} sm={6}>
              <FormField
                type="number"
                name={this.formModel.LogoutTimeout.uniqueKey}
                label="Minutes of inactivity until automatic logout"
                parse={val => val || "0"}
                validate={[validateSingleMandatoryField, this.validateTimeoutRange]}
              />
            </Grid>
          </Grid>
        </AppBarContainer>
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
