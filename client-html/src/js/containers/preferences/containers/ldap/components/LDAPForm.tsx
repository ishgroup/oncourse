/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Button, Divider, FormControlLabel, Grid } from '@mui/material';
import Checkbox from '@mui/material/Checkbox';
import Hidden from '@mui/material/Hidden';
import Typography from '@mui/material/Typography';
import $t from '@t';
import { ConfirmBase } from 'ish-ui';
import { isEmpty } from 'es-toolkit/compat'
import * as React from 'react';
import { connect } from 'react-redux';
import { Form, getFormValues, initialize, reduxForm } from 'redux-form';
import RouteChangeConfirm from '../../../../../common/components/dialog/RouteChangeConfirm';
import FormField from '../../../../../common/components/form/formFields/FormField';
import AppBarContainer from '../../../../../common/components/layout/AppBarContainer';
import { getManualLink } from '../../../../../common/utils/getManualLink';
import { onSubmitFail } from '../../../../../common/utils/highlightFormErrors';
import { validateMultipleMandatoryFields } from '../../../../../common/utils/validation';
import { FormModelSchema } from '../../../../../model/preferences/FormModelShema';
import * as Model from '../../../../../model/preferences/Ldap';
import { State } from '../../../../../reducers/state';
import { PREFERENCES_AUDITS_LINK } from '../../../constants';

const manualUrl = getManualLink("setting-your-general-preferences#ldap");

class LDAPBaseForm extends React.Component<any, any> {
  private formModel: FormModelSchema;

  constructor(props) {
    super(props);

    this.state = {
      licenceWarning: false
    };

    if (!isEmpty(props.formData)) {
      props.dispatch(initialize("LDAPForm", props.formData));
    }

    this.formModel = props.formatModel(Model);

    this.showLicenceWarning = this.showLicenceWarning.bind(this);
    this.closeLicenceWarning = this.closeLicenceWarning.bind(this);
  }

  // eslint-disable-next-line camelcase
  UNSAFE_componentWillReceiveProps(nextProps) {
    // Initializing form with values
    if (!isEmpty(nextProps.formData) && !this.props.initialized) {
      this.props.dispatch(initialize("LDAPForm", nextProps.formData));
    }
  }

  showLicenceWarning(e) {
    e.preventDefault();
    this.setState({
      licenceWarning: true
    });
  }

  closeLicenceWarning() {
    this.setState({
      licenceWarning: false
    });
  }

  render() {
    const {
      handleSubmit, onSave, values, licence, testLdapConnection, dirty, data, form, invalid, formRoleName
    } = this.props;

    const simpleAuthEnabled = values && values[this.formModel.LdapSimpleAuthentication.uniqueKey] === "true";
    const saslAuthEnabled = values && values[this.formModel.LdapSaslAuthentication.uniqueKey] === "true";

    return (
      <Form className="container" onSubmit={handleSubmit(onSave)} role={formRoleName}>
        <RouteChangeConfirm form={form} when={dirty} />

        <ConfirmBase
          title=""
          confirmMessage="This feature is not enabled, please contact ish to enable it"
          open={this.state.licenceWarning}
          onCancel={this.closeLicenceWarning}
          cancelButtonText="OK"
        />

        <AppBarContainer
          values={data}
          manualUrl={manualUrl}
          getAuditsUrl={PREFERENCES_AUDITS_LINK}
          disabled={!dirty}
          invalid={invalid}
          title={$t('ldap')}
          disableInteraction
          createdOn={v => v.created}
          modifiedOn={v => v.modified}
        >
          <Grid container>
            <Grid item xs={12}>
              <FormControlLabel
                className="mb-2"
                classes={{
                  root: "checkbox"
                }}
                control={
                  licence ? (
                    <FormField
                      type="checkbox"
                      name={this.formModel.LdapSimpleAuthentication.uniqueKey}
                      color="primary"
                      stringValue
                    />
                  ) : (
                    <Checkbox onClick={this.showLicenceWarning} checked={false} />
                  )
                }
                label={$t('enable_ldapad_authentication_ignored_for_admin_use')}
              />
            </Grid>
          </Grid>

          <Typography variant="body1" className="heading mb-2">
            {$t('ldap_server_parameter')}
          </Typography>

          <Grid container columnSpacing={3}>
            <Grid item xs={12} sm={4}>
              <FormField
                type="text"
                name={this.formModel.LdapHost.uniqueKey}
                label={$t('host')}
                disabled={!licence || !simpleAuthEnabled}
                              />
            </Grid>

            <Hidden xsDown>
              <Grid item sm={1} />
            </Hidden>

            <Grid item xs={12} sm={4}>
              <FormField
                type="text"
                name={this.formModel.LdapBindUserDN.uniqueKey}
                label={$t('bind_user_dn')}
                disabled={!licence || !simpleAuthEnabled}
                              />
            </Grid>

            <Hidden xsDown>
              <Grid item sm={3} />
            </Hidden>

            <Grid item xs={12} sm={4} className="mb-1">
              <FormField
                type="text"
                name={this.formModel.LdapServerPort.uniqueKey}
                label={$t('port_eg_389')}
                disabled={!licence || !simpleAuthEnabled}
                              />
            </Grid>

            <Hidden xsDown>
              <Grid item sm={1} />
            </Hidden>

            <Grid item xs={12} sm={4}>
              <div>
                <FormControlLabel
                  classes={{
                    root: "checkbox"
                  }}
                  control={(
                    <FormField
                      type="checkbox"
                      name={this.formModel.LdapSSL.uniqueKey}
                      color="primary"
                      disabled={!licence || !simpleAuthEnabled}
                      stringValue
                    />
                  )}
                  label={$t('ssl')}
                />
              </div>
            </Grid>

            <Hidden xsDown>
              <Grid item sm={3} />
            </Hidden>

            <Grid item xs={12} sm={4}>
              <FormField
                type="password"
                name={this.formModel.LdapBindUserPass.uniqueKey}
                label={$t('bind_user_password')}
                disabled={!licence || !simpleAuthEnabled}
                              />
            </Grid>

            <Hidden xsDown>
              <Grid item sm={1} />
            </Hidden>

            <Grid item sm={4}>
              <FormField
                type="text"
                name={this.formModel.LdapBaseDN.uniqueKey}
                label={$t('base_dn')}
                disabled={!licence || !simpleAuthEnabled}
                              />
            </Grid>

            <Hidden xsDown>
              <Grid item sm={3} />
            </Hidden>

            <Grid item xs={12} className="mb-2 mt-2">
              <Button
                className="m-0"
                onClick={testLdapConnection}
                disabled={!licence || !simpleAuthEnabled}
              >
                {$t('test_connection')}
              </Button>
            </Grid>
          </Grid>

          <Grid container columnSpacing={3} spacing={5}>
            <Grid item xs={12} sm={8}>
              <Divider className="mb-1 mt-1" />
            </Grid>
          </Grid>

          <Typography variant="body1" className="heading mt-2 mb-2">
            {$t('users')}
          </Typography>

          <Grid container columnSpacing={3}>
            <Grid item xs={12} sm={4}>
              <FormField
                type="text"
                name={this.formModel.LdapUsernameAttribute.uniqueKey}
                label={$t('username_attribute')}
                disabled={!licence || !simpleAuthEnabled}
                              />
            </Grid>

            <Hidden xsDown>
              <Grid item sm={1} />
            </Hidden>

            <Grid item xs={12} sm={4}>
              <FormField
                type="text"
                name={this.formModel.LdapUserSearchFilter.uniqueKey}
                label={$t('user_search_filter')}
                disabled={!licence || !simpleAuthEnabled}
              />
            </Grid>

            <Hidden xsDown>
              <Grid item sm={3} />
            </Hidden>

            <Grid item xs={12} className="mt-1">
              {!licence || !simpleAuthEnabled ? (
                <Button
                  href="ldapUserAccess"
                  className="m-0"
                  disabled={!licence || !simpleAuthEnabled}
                >
                  {$t('test_user')}
                </Button>
              ) : (
                <a href="ldapUserAccess" className="link">
                  <Button
                    href="ldapUserAccess"
                    className="m-0"
                    disabled={!licence || !simpleAuthEnabled}
                  >
                    {$t('test_user')}
                  </Button>
                </a>
              )}
            </Grid>

            <Grid item xs={12} sm={8} className="mb-2 mt-2">
              <Divider className="mt-1" />
            </Grid>

            <Grid item xs={12} className="mb-2">
              <FormControlLabel
                classes={{
                  root: "checkbox"
                }}
                control={(
                  <FormField
                    type="checkbox"
                    name={this.formModel.LdapSaslAuthentication.uniqueKey}
                    color="primary"
                    disabled={!licence || !simpleAuthEnabled}
                    stringValue
                  />
                )}
                label={$t('enable_ldapad_authorisation')}
              />
            </Grid>
          </Grid>

          <Typography variant="body1" className="heading mb-2">
            {$t('roles')}
          </Typography>

          <Grid container columnSpacing={3}>
            <Grid item xs={12} sm={4}>
              <FormField
                type="text"
                name={this.formModel.LdapGroupMemberAttribute.uniqueKey}
                label={$t('group_member_attribute')}
                disabled={!licence || !saslAuthEnabled || !simpleAuthEnabled}
              />
            </Grid>

            <Hidden xsDown>
              <Grid item sm={1} />
            </Hidden>

            <Grid item xs={12} sm={4}>
              <FormField
                type="text"
                name={this.formModel.LdapGroupAttribute.uniqueKey}
                label={$t('group_name_attribute')}
                disabled={!licence || !saslAuthEnabled || !simpleAuthEnabled}
              />
            </Grid>

            <Hidden xsDown>
              <Grid item sm={3} />
            </Hidden>

            <Grid item xs={12} sm={4}>
              <FormField
                type="text"
                name={this.formModel.LdapGroupSearchFilter.uniqueKey}
                label={$t('group_search_filter')}
                disabled={!licence || !saslAuthEnabled || !simpleAuthEnabled}
              />
            </Grid>

            <Hidden xsDown>
              <Grid item sm={1} />
            </Hidden>

            <Grid item xs={12} sm={4}>
              <FormControlLabel
                classes={{
                  root: "checkbox"
                }}
                control={(
                  <FormField
                    type="checkbox"
                    name={this.formModel.LdapGroupPosixStyle.uniqueKey}
                    color="primary"
                    disabled={!licence || !saslAuthEnabled || !simpleAuthEnabled}
                    stringValue
                  />
                )}
                label={$t('posix_style_groups')}
              />
            </Grid>

            <Hidden xsDown>
              <Grid item sm={3} />
            </Hidden>
          </Grid>
        </AppBarContainer>
      </Form>
    );
  }
}

const mapStateToProps = (state: State) => ({
  values: getFormValues("LDAPForm")(state)
});

const LDAPForm = reduxForm({
  form: "LDAPForm",
  validate: validateMultipleMandatoryFields,
  onSubmitFail
})(
  connect<any, any, any>(
    mapStateToProps,
    null
  )(LDAPBaseForm)
);

export default LDAPForm;
