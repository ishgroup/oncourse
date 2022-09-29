/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import * as React from "react";
import isEmpty from "lodash.isempty";
import Hidden from "@mui/material/Hidden";
import Help from "@mui/icons-material/Help";
import FormControlLabel from "@mui/material/FormControlLabel";
import Grid from "@mui/material/Grid";
import IconButton from "@mui/material/IconButton";
import Typography from "@mui/material/Typography";
import Divider from "@mui/material/Divider";
import { Form, reduxForm, initialize } from "redux-form";
import FormField from "../../../../../common/components/form/formFields/FormField";
import { onSubmitFail } from "../../../../../common/utils/highlightFormErrors";
import * as Model from "../../../../../model/preferences/Avetmiss";
import { validateEmail } from "../../../../../common/utils/validation";
import { FormModelSchema } from "../../../../../model/preferences/FormModelShema";
import RouteChangeConfirm from "../../../../../common/components/dialog/confirm/RouteChangeConfirm";
import { getManualLink } from "../../../../../common/utils/getManualLink";
import { PREFERENCES_AUDITS_LINK } from "../../../constants";
import AppBarContainer from "../../../../../common/components/layout/AppBarContainer";

const manualUrl = getManualLink("generalPrefs_avetmiss");

class AvetmissBaseForm extends React.Component<any, any> {
  private formModel: FormModelSchema;

  constructor(props) {
    super(props);

    // Initializing form with values
    if (!isEmpty(props.formData)) {
      props.dispatch(initialize("AvetmissForm", props.formData));
    }

    this.formModel = props.formatModel(Model);
  }

  // eslint-disable-next-line camelcase
  UNSAFE_componentWillReceiveProps(nextProps) {
    // Initializing form with values
    if (!isEmpty(nextProps.formData) && !this.props.initialized) {
      this.props.dispatch(initialize("AvetmissForm", nextProps.formData));
    }
  }

  providerCodeLengthValidation = value => (
    value && value.length !== 4 ? "AVETMISS FEE HELP Provider code should be 4 characters" : undefined
  );

  render() {
    const {
      handleSubmit, onSave, dirty, data, enums, invalid, form, formRoleName
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
          title="AVETMISS"
          disableInteraction
          createdOn={values => values.created}
          modifiedOn={values => values.modified}
        >
          <Grid container columnSpacing={3} rowSpacing={2}>
            <Grid item xs={12}>
              <FormControlLabel
                classes={{
                  root: "checkbox"
                }}
                control={(
                  <FormField
                    type="checkbox"
                    name={this.formModel.ShowGUI.uniqueKey}
                    color="primary"
                    stringValue
                    fullWidth
                  />
                )}
                label="Show RTO related screens and menus"
              />
            </Grid>

            <Grid item xs={12}>
              <Typography variant="subtitle1" className="heading mb-2 mt-2">
                Training organisation
              </Typography>
            </Grid>

            <Grid item xs={12} sm={6} md={4}>
              <FormField
                type="text"
                name={this.formModel.AvetmissCollegeName.uniqueKey}
                label="Training organisation name"
                fullWidth
              />
            </Grid>

            <Grid item xs={12} sm={6} md={4}>
              <FormField
                type="select"
                name={this.formModel.Jurisdiction.uniqueKey}
                label="AVETMISS jurisdiction"
                items={enums.ExportJurisdiction}
                fullWidth
              />
            </Grid>

            <Hidden smDown>
              <Grid item md={4} />
            </Hidden>

            <Grid item xs={12} sm={6} md={4}>
              <FormField
                type="text"
                name={this.formModel.Id.uniqueKey}
                label="RTO Identifier"
                labelAdornment={(
                  <span>
                    <a target="_blank" href="https://training.gov.au/Home/Tga" rel="noreferrer">
                      <IconButton
                        classes={{
                          root: "inputAdornmentButton"
                        }}
                      >
                        <Help className="inputAdornmentIcon" />
                      </IconButton>
                    </a>
                  </span>
                )}
                fullWidth
              />
            </Grid>

            <Grid item xs={12} sm={6} md={4}>
              <FormField
                type="select"
                name={this.formModel.Type.uniqueKey}
                label="Type"
                items={enums.TrainingOrg_Types}
                fullWidth
              />
            </Grid>

            <Hidden smDown>
              <Grid item md={4} />
            </Hidden>

            <Grid item xs={12} sm={6} md={4}>
              <FormField type="text" name={this.formModel.Address1.uniqueKey} label="Address 1" fullWidth />
            </Grid>

            <Grid item xs={12} sm={6} md={4}>
              <FormField type="text" name={this.formModel.Suburb.uniqueKey} label="Suburb" fullWidth />
            </Grid>

            <Hidden smDown>
              <Grid item md={4} />
            </Hidden>

            <Grid item xs={12} sm={6} md={4}>
              <FormField type="text" name={this.formModel.Address2.uniqueKey} label="Address 2" fullWidth />
            </Grid>

            <Grid item xs={12} sm={6} md={4}>
              <FormField type="text" name={this.formModel.Postcode.uniqueKey} label="Postcode" fullWidth />
            </Grid>

            <Hidden smDown>
              <Grid item md={4} />
            </Hidden>

            <Grid item xs={12} sm={6} md={4}>
              <FormField
                type="select"
                name={this.formModel.State.uniqueKey}
                label="State"
                items={enums.AddressStates}
                fullWidth
              />
            </Grid>

            <Hidden smDown>
              <Grid item md={8} />
            </Hidden>

            <Grid item xs={12} md={8} className="mb-2">
              <Divider />
            </Grid>

            <Grid item xs={12}>
              <Typography variant="subtitle1" className="heading mb-2 mt-2">
                Optional
              </Typography>
            </Grid>

            <Grid item xs={12} sm={6} md={4}>
              <FormField
                type="text"
                name={this.formModel.ContactName.uniqueKey}
                label="Contact name"
                fullWidth
              />
            </Grid>

            <Grid item xs={12} sm={6} md={4}>
              <FormField
                type="text"
                name={this.formModel.Email.uniqueKey}
                label="Email"
                validate={validateEmail}
                fullWidth
              />
            </Grid>

            <Hidden smDown>
              <Grid item md={4} />
            </Hidden>

            <Grid item xs={12} sm={6} md={4}>
              <FormField type="text" name={this.formModel.Phone.uniqueKey} label="Telephone" fullWidth />
            </Grid>

            <Grid item xs={12} sm={6} md={4}>
              <FormField type="text" name={this.formModel.Fax.uniqueKey} label="Fax" fullWidth />
            </Grid>

            <Hidden smDown>
              <Grid item md={4} />
            </Hidden>

            <Grid item xs={12} sm={6} md={4}>
              <FormField
                type="text"
                name={this.formModel.CertSignatoryName.uniqueKey}
                label="Full certificates signatory name (i.e. Dr Joe Bloggs MD)"
                fullWidth
              />
            </Grid>

            <Grid item xs={12} sm={6} md={4}>
              <FormControlLabel
                classes={{
                  root: "overflow-initial checkbox"
                }}
                control={(
                  <FormField
                    type="checkbox"
                    name={this.formModel.showOfferedQM.uniqueKey}
                    color="primary"
                    value="true"
                    stringValue
                  />
                )}
                label="Only show 'offered' Qualifications and Modules"
              />
            </Grid>

            <Hidden smDown>
              <Grid item md={4} />
            </Hidden>

            <Grid item xs={12} sm={6} md={4}>
              <FormField
                type="text"
                name={this.formModel.QldIdentifier.uniqueKey}
                label="Queensland RTO id"
                fullWidth
              />
            </Grid>

            <Grid item xs={12} sm={6} md={4}>
              <FormField
                type="text"
                name={this.formModel.FeeHelpProviderCode.uniqueKey}
                label="Fee Help Provider Code"
                validate={this.providerCodeLengthValidation}
                fullWidth
              />
            </Grid>
          </Grid>
        </AppBarContainer>
      </Form>
    );
  }
}

const AvetmissForm = reduxForm({
  form: "AvetmissForm",
  onSubmitFail
})(AvetmissBaseForm);

export default AvetmissForm;
