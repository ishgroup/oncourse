/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import Grid from "@material-ui/core/Grid";
import Typography from "@material-ui/core/Typography";
import { reduxForm, initialize } from "redux-form";
import isEmpty from "lodash.isempty";
import FormField from "../../../../../common/components/form/form-fields/FormField";
import FormSubmitButton from "../../../../../common/components/form/FormSubmitButton";
import { onSubmitFail } from "../../../../../common/utils/highlightFormClassErrors";
import { validateMultipleMandatoryFields } from "../../../../../common/utils/validation";
import * as Model from "../../../../../model/preferences/College";
import { FormModelSchema } from "../../../../../model/preferences/FormModelShema";
import CustomAppBar from "../../../../../common/components/layout/CustomAppBar";
import RouteChangeConfirm from "../../../../../common/components/dialog/confirm/RouteChangeConfirm";
import AppBarHelpMenu from "../../../../../common/components/form/AppBarHelpMenu";
import { getManualLink } from "../../../../../common/utils/getManualLink";
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

  componentWillReceiveProps(nextProps) {
    // Initializing form with values
    if (!isEmpty(nextProps.formData) && !this.props.initialized) {
      this.props.dispatch(initialize("CollegeForm", nextProps.formData));
    }
  }

  render() {
    const {
     classes, handleSubmit, onSave, dirty, secKey, timezones, data, invalid, form
    } = this.props;

    return (
      <form className="container" onSubmit={handleSubmit(onSave)}>
        <RouteChangeConfirm form={form} when={dirty} />

        <CustomAppBar>
          <Grid container>
            <Grid item xs={12} className="centeredFlex">
              <Typography className="appHeaderFontSize" color="inherit" noWrap>
                College
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

        <Grid container spacing={2}>
          <Grid item xs={12} sm={6} lg={4}>
            <FormField
              type="text"
              name={this.formModel.CollegeName.uniqueKey}
              label="College Name"
              fullWidth
            />

            <FormField
              type="text"
              name={this.formModel.CollegeWebsite.uniqueKey}
              label="College website URL"
              fullWidth
            />
            {timezones && (
              <>
                <FormField
                  type="searchSelect"
                  name={this.formModel.CollegeTimezone.uniqueKey}
                  label="Default server time zone"
                  items={timezones}
                />
              </>
            )}
          </Grid>
          <Grid item xs={12} sm={4} lg={3}>
            <FormField
              type="text"
              name={this.formModel.CollegeABN.uniqueKey}
              label="College ABN"
              fullWidth
            />

            <Typography variant="caption" color="textSecondary">
              Security key
            </Typography>
            <Typography variant="body1">
              {secKey || <span className={classes.placeholderContent}>No Value</span>}
            </Typography>
          </Grid>
        </Grid>
      </form>
    );
  }
}

export default reduxForm({
  form: "CollegeForm",
  onSubmitFail,
  validate: validateMultipleMandatoryFields
})(CollegeBaseForm);
