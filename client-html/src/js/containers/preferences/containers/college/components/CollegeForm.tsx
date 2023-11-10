/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import Grid from "@mui/material/Grid";
import Typography from "@mui/material/Typography";
import isEmpty from "lodash.isempty";
import * as React from "react";
import { Form, initialize, reduxForm } from "redux-form";
import RouteChangeConfirm from "../../../../../common/components/dialog/RouteChangeConfirm";
import FormField from "../../../../../common/components/form/formFields/FormField";
import AppBarContainer from "../../../../../common/components/layout/AppBarContainer";
import { getManualLink } from "../../../../../common/utils/getManualLink";
import { onSubmitFail } from "../../../../../common/utils/highlightFormErrors";
import { validateMultipleMandatoryFields } from "../../../../../common/utils/validation";
import { COMMON_PLACEHOLDER } from "../../../../../constants/Forms";
import * as Model from "../../../../../model/preferences/College";
import { FormModelSchema } from "../../../../../model/preferences/FormModelShema";
import { PREFERENCES_AUDITS_LINK } from "../../../constants";

const manualUrl = getManualLink("generalPrefs_college");

class CollegeBaseForm extends React.Component<any, any> {
  private formModel: FormModelSchema;

  constructor(props) {
    super(props);

    // Initializing form with values
    if (!isEmpty(props.formData)) {
      props.dispatch(initialize("CollegeForm", props.formData));
    }

    this.formModel = props.formatModel(Model);
  }

  // eslint-disable-next-line camelcase
  UNSAFE_componentWillReceiveProps(nextProps) {
    // Initializing form with values
    if (!isEmpty(nextProps.formData) && !this.props.initialized) {
      this.props.dispatch(initialize("CollegeForm", nextProps.formData));
    }
  }

  render() {
    const {
     classes, handleSubmit, onSave, dirty, secKey, timezones, data, invalid, form, formRoleName
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
          title="College"
          disableInteraction
          createdOn={values => values.created}
          modifiedOn={values => values.modified}
        >
          <Grid container columnSpacing={3}>
            <Grid item xs={12} sm={6} lg={4}>
              <FormField
                type="text"
                name={this.formModel.CollegeName.uniqueKey}
                label="College Name"
                className="mb-2"
              />

              <FormField
                type="text"
                name={this.formModel.CollegeWebsite.uniqueKey}
                label="College website URL"
                className="mb-2"
              />
              {timezones && (
                <>
                  <FormField
                    type="select"
                    name={this.formModel.CollegeTimezone.uniqueKey}
                    label="Default server time zone"
                    items={timezones}
                    className="mb-2"
                  />
                </>
              )}
            </Grid>
            <Grid item xs={12} sm={4} lg={3}>
              <FormField
                type="text"
                name={this.formModel.CollegeABN.uniqueKey}
                label="College ABN"
                className="mb-2"
              />

              <Typography variant="caption" color="textSecondary">
                Security key
              </Typography>
              <Typography variant="body1">
                {secKey || <span className={classes.placeholderContent}>{COMMON_PLACEHOLDER}</span>}
              </Typography>
            </Grid>
          </Grid>
        </AppBarContainer>
      </Form>
    );
  }
}

export default reduxForm({
  form: "CollegeForm",
  onSubmitFail,
  validate: validateMultipleMandatoryFields
})(CollegeBaseForm);
