/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import { Form, reduxForm, initialize } from "redux-form";
import isEmpty from "lodash.isempty";
import { connect } from "react-redux";
import Grid from "@mui/material/Grid";
import FormField from "../../../../../common/components/form/formFields/FormField";
import { validateMultipleMandatoryFields } from "../../../../../common/utils/validation";
import { FormModelSchema } from "../../../../../model/preferences/FormModelShema";
import * as Model from "../../../../../model/preferences/ClassDefaults";
import RouteChangeConfirm from "../../../../../common/components/dialog/RouteChangeConfirm";
import { getManualLink } from "../../../../../common/utils/getManualLink";
import { PREFERENCES_AUDITS_LINK } from "../../../constants";
import { onSubmitFail } from "../../../../../common/utils/highlightFormErrors";
import AppBarContainer from "../../../../../common/components/layout/AppBarContainer";

const manualUrl = getManualLink("generalPrefs_classdefaults");

class ClassDefaultsBaseForm extends React.Component<any, any> {
  private formModel: FormModelSchema;

  constructor(props) {
    super(props);

    // Initializing form with values
    if (!isEmpty(props.formData)) {
      props.dispatch(initialize("ClassDefaultsForm", props.formData));
    }

    this.formModel = props.formatModel(Model);
  }

  // eslint-disable-next-line camelcase
  UNSAFE_componentWillReceiveProps(nextProps) {
    // Initializing form with values
    if (!isEmpty(nextProps.formData) && !this.props.initialized) {
      this.props.dispatch(initialize("ClassDefaultsForm", nextProps.formData));
    }
  }

  render() {
    const {
     handleSubmit, onSave, dirty, enums, data, form, invalid, formRoleName
    } = this.props;

    return (
      <Form className="container" onSubmit={handleSubmit(onSave)} role={formRoleName}>
        <RouteChangeConfirm form={form} when={dirty} />

        <AppBarContainer
          values={data}
          manualUrl={manualUrl}
          getAuditsUrl={PREFERENCES_AUDITS_LINK}
          disabled={!dirty}
          invalid={invalid}
          title="Class Defaults"
          disableInteraction
          createdOn={values => values.created}
          modifiedOn={values => values.modified}
        >
          <Grid container columnSpacing={3} rowSpacing={2}>
            <Grid container item columnSpacing={3} rowSpacing={2}>
              <Grid item xs={12} sm={3}>
                <FormField
                  type="number"
                  name={this.formModel.ClassMinPlaces.uniqueKey}
                  label="Minimum places"
                  parse={val => val || "0"}
                  debounced={false}
                />
              </Grid>

              <Grid item xs={12} sm={4}>
                <FormField
                  type="select"
                  name={this.formModel.ClassDeliveryMode.uniqueKey}
                  label="Delivery mode"
                  items={enums.DeliveryMode}
                />
              </Grid>
            </Grid>

            <Grid container item columnSpacing={3} rowSpacing={2}>
              <Grid item xs={12} sm={3}>
                <FormField
                  type="number"
                  name={this.formModel.ClassMaxPlaces.uniqueKey}
                  label="Maximum places"
                  parse={val => val || "0"}
                  debounced={false}
                />
              </Grid>

              <Grid item xs={12} sm={4}>
                <FormField
                  type="select"
                  name={this.formModel.ClassFundingSourcePreference.uniqueKey}
                  label="Funding source"
                  items={enums.ClassFundingSource}
                />
              </Grid>
            </Grid>
          </Grid>

        </AppBarContainer>
      </Form>
    );
  }
}

const ClassDefaultsForm = reduxForm({
  form: "ClassDefaultsForm",
  validate: validateMultipleMandatoryFields,
  onSubmitFail
})(
  connect<any, any, any>(null, null, null, { forwardRef: true })(ClassDefaultsBaseForm)
);

export default ClassDefaultsForm;
