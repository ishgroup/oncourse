/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import Help from '@mui/icons-material/Help';
import Divider from '@mui/material/Divider';
import FormControlLabel from '@mui/material/FormControlLabel';
import Grid from '@mui/material/Grid';
import Hidden from '@mui/material/Hidden';
import IconButton from '@mui/material/IconButton';
import Typography from '@mui/material/Typography';
import $t from '@t';
import { isEmpty } from 'es-toolkit/compat'
import * as React from 'react';
import { Form, initialize, reduxForm } from 'redux-form';
import RouteChangeConfirm from '../../../../../common/components/dialog/RouteChangeConfirm';
import FormField from '../../../../../common/components/form/formFields/FormField';
import AppBarContainer from '../../../../../common/components/layout/AppBarContainer';
import { getManualLink } from '../../../../../common/utils/getManualLink';
import { onSubmitFail } from '../../../../../common/utils/highlightFormErrors';
import { validateEmail } from '../../../../../common/utils/validation';
import * as Model from '../../../../../model/preferences/Avetmiss';
import { FormModelSchema } from '../../../../../model/preferences/FormModelShema';
import { PREFERENCES_AUDITS_LINK } from '../../../constants';

const manualUrl = getManualLink("setting-your-general-preferences#avetmiss");

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
          title={$t('avetmiss')}
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
                                      />
                )}
                label={$t('show_rto_related_screens_and_menus')}
              />
            </Grid>

            <Grid item xs={12}>
              <Typography variant="subtitle1" className="heading mb-2 mt-2">
                {$t('training_organisation')}
              </Typography>
            </Grid>

            <Grid item xs={12} sm={6} md={4}>
              <FormField
                type="text"
                name={this.formModel.AvetmissCollegeName.uniqueKey}
                label={$t('training_organisation_name')}
                              />
            </Grid>

            <Grid item xs={12} sm={6} md={4}>
              <FormField
                type="select"
                name={this.formModel.Jurisdiction.uniqueKey}
                label={$t('avetmiss_jurisdiction')}
                items={enums.ExportJurisdiction}
                              />
            </Grid>

            <Hidden smDown>
              <Grid item md={4} />
            </Hidden>

            <Grid item xs={12} sm={6} md={4}>
              <FormField
                type="text"
                name={this.formModel.Id.uniqueKey}
                label={$t('rto_identifier')}
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
                              />
            </Grid>

            <Grid item xs={12} sm={6} md={4}>
              <FormField
                type="select"
                name={this.formModel.Type.uniqueKey}
                label={$t('type')}
                items={enums.TrainingOrg_Types}
                              />
            </Grid>

            <Hidden smDown>
              <Grid item md={4} />
            </Hidden>

            <Grid item xs={12} sm={6} md={4}>
              <FormField type="text" name={this.formModel.Address1.uniqueKey} label={$t('address_1')}  />
            </Grid>

            <Grid item xs={12} sm={6} md={4}>
              <FormField type="text" name={this.formModel.Suburb.uniqueKey} label={$t('suburb')}  />
            </Grid>

            <Hidden smDown>
              <Grid item md={4} />
            </Hidden>

            <Grid item xs={12} sm={6} md={4}>
              <FormField type="text" name={this.formModel.Address2.uniqueKey} label={$t('address_2')} />
            </Grid>

            <Grid item xs={12} sm={6} md={4}>
              <FormField type="text" name={this.formModel.Postcode.uniqueKey} label={$t('postcode')} />
            </Grid>

            <Hidden smDown>
              <Grid item md={4} />
            </Hidden>

            <Grid item xs={12} sm={6} md={4}>
              <FormField
                type="select"
                name={this.formModel.State.uniqueKey}
                label={$t('state')}
                items={enums.AddressStates}
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
                {$t('optional')}
              </Typography>
            </Grid>

            <Grid item xs={12} sm={6} md={4}>
              <FormField
                type="text"
                name={this.formModel.ContactName.uniqueKey}
                label={$t('contact_name')}
                              />
            </Grid>

            <Grid item xs={12} sm={6} md={4}>
              <FormField
                type="text"
                name={this.formModel.Email.uniqueKey}
                label={$t('email2')}
                validate={validateEmail}
                              />
            </Grid>

            <Hidden smDown>
              <Grid item md={4} />
            </Hidden>

            <Grid item xs={12} sm={6} md={4}>
              <FormField type="text" name={this.formModel.Phone.uniqueKey} label={$t('telephone')} />
            </Grid>

            <Grid item xs={12} sm={6} md={4}>
              <FormField type="text" name={this.formModel.Fax.uniqueKey} label={$t('fax2')} />
            </Grid>

            <Hidden smDown>
              <Grid item md={4} />
            </Hidden>

            <Grid item xs={12} sm={6} md={4}>
              <FormField
                type="text"
                name={this.formModel.CertSignatoryName.uniqueKey}
                label={$t('full_certificates_signatory_name_ie_dr_joe_bloggs')}
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
                    stringValue
                  />
                )}
                label={$t('only_show_offered_qualifications_and_modules')}
              />
            </Grid>

            <Hidden smDown>
              <Grid item md={4} />
            </Hidden>

            <Grid item xs={12} sm={6} md={4}>
              <FormField
                type="text"
                name={this.formModel.QldIdentifier.uniqueKey}
                label={$t('queensland_rto_id')}
                              />
            </Grid>

            <Grid item xs={12} sm={6} md={4}>
              <FormField
                type="text"
                name={this.formModel.FeeHelpProviderCode.uniqueKey}
                label={$t('fee_help_provider_code')}
                validate={this.providerCodeLengthValidation}
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
