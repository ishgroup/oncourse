/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import Grid from "@material-ui/core/Grid";
import Divider from "@material-ui/core/Divider";
import Hidden from "@material-ui/core/Hidden";
import { FormControlLabel } from "@material-ui/core";
import Typography from "@material-ui/core/Typography";
import Checkbox from "@material-ui/core/Checkbox";
import {
  reduxForm, getFormValues, initialize
} from "redux-form";
import { connect } from "react-redux";
import isEmpty from "lodash.isempty";
import ConfirmBase from "../../../../../common/components/dialog/confirm/ConfirmBase";
import Button from "../../../../../common/components/buttons/Button";
import FormField from "../../../../../common/components/form/form-fields/FormField";
import * as Model from "../../../../../model/preferences/Ldap";
import { validateMultipleMandatoryFields } from "../../../../../common/utils/validation";
import { State } from "../../../../../reducers/state";
import { FormModelSchema } from "../../../../../model/preferences/FormModelShema";
import CustomAppBar from "../../../../../common/components/layout/CustomAppBar";
import RouteChangeConfirm from "../../../../../common/components/dialog/confirm/RouteChangeConfirm";
import AppBarHelpMenu from "../../../../../common/components/form/AppBarHelpMenu";
import { getManualLink } from "../../../../../common/utils/getManualLink";
import { PREFERENCES_AUDITS_LINK } from "../../../constants";

const manualUrl = getManualLink("generalPrefs_ldap");

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
  }

  componentWillReceiveProps(nextProps) {
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
      handleSubmit, onSave, values, licence, testLdapConnection, dirty, data, form
    } = this.props;

    const simpleAuthEnabled = values && values[this.formModel.LdapSimpleAuthentication.uniqueKey] === "true";
    const saslAuthEnabled = values && values[this.formModel.LdapSaslAuthentication.uniqueKey] === "true";

    return (
      <form className="container" onSubmit={handleSubmit(onSave)}>
        <RouteChangeConfirm form={form} when={dirty} />

        <ConfirmBase
          title=""
          confirmMessage="This feature is not enabled, please contact ish to enable it"
          open={this.state.licenceWarning}
          onCancel={this.closeLicenceWarning}
          cancelButtonText="OK"
        />

        <CustomAppBar>
          <Grid container>
            <Grid item xs={12} className="centeredFlex">
              <Typography className="appHeaderFontSize" color="inherit" noWrap>
                LDAP
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

              <Button
                text="Save"
                type="submit"
                size="small"
                variant="text"
                disabled={!dirty}
                rootClasses="whiteAppBarButton"
                disabledClasses="whiteAppBarButtonDisabled"
              />
            </Grid>
          </Grid>
        </CustomAppBar>

        <Grid container spacing={2}>
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
              label="Enable LDAP/AD authentication (ignored for admin users)"
            />
          </Grid>
        </Grid>

        <Typography variant="body1" className="heading mb-2">
          LDAP server parameter
        </Typography>

        <Grid container>
          <Grid item xs={12} sm={4}>
            <FormField
              type="text"
              name={this.formModel.LdapHost.uniqueKey}
              label="Host"
              disabled={!licence || !simpleAuthEnabled}
              fullWidth
            />
          </Grid>

          <Hidden xsDown>
            <Grid item sm={1} />
          </Hidden>

          <Grid item xs={12} sm={4}>
            <FormField
              type="text"
              name={this.formModel.LdapBindUserDN.uniqueKey}
              label="Bind user DN"
              disabled={!licence || !simpleAuthEnabled}
              fullWidth
            />
          </Grid>

          <Hidden xsDown>
            <Grid item sm={3} />
          </Hidden>

          <Grid item xs={12} sm={4} className="mb-1">
            <FormField
              type="text"
              name={this.formModel.LdapServerPort.uniqueKey}
              label="Port, e.g. 389"
              disabled={!licence || !simpleAuthEnabled}
              fullWidth
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
                label="SSL"
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
              label="Bind user password"
              disabled={!licence || !simpleAuthEnabled}
              fullWidth
            />
          </Grid>

          <Hidden xsDown>
            <Grid item sm={1} />
          </Hidden>

          <Grid item sm={4}>
            <FormField
              type="text"
              name={this.formModel.LdapBaseDN.uniqueKey}
              label="Base DN"
              disabled={!licence || !simpleAuthEnabled}
              fullWidth
            />
          </Grid>

          <Hidden xsDown>
            <Grid item sm={3} />
          </Hidden>

          <Grid item xs={12} className="mb-2 mt-2">
            <Button
              text="Test connection"
              className="m-0"
              onClick={testLdapConnection}
              disabled={!licence || !simpleAuthEnabled}
            />
          </Grid>
        </Grid>

        <Grid container spacing={5}>
          <Grid item xs={12} sm={8}>
            <Divider className="mb-1 mt-1" />
          </Grid>
        </Grid>

        <Typography variant="body1" className="heading mt-2 mb-2">
          Users
        </Typography>

        <Grid container>
          <Grid item xs={12} sm={4}>
            <FormField
              type="text"
              name={this.formModel.LdapUsernameAttribute.uniqueKey}
              label="Username attribute"
              disabled={!licence || !simpleAuthEnabled}
              fullWidth
            />
          </Grid>

          <Hidden xsDown>
            <Grid item sm={1} />
          </Hidden>

          <Grid item xs={12} sm={4}>
            <FormField
              type="text"
              name={this.formModel.LdapUserSearchFilter.uniqueKey}
              label="User search filter"
              disabled={!licence || !simpleAuthEnabled}
              fullWidth
            />
          </Grid>

          <Hidden xsDown>
            <Grid item sm={3} />
          </Hidden>

          <Grid item xs={12} className="mt-1">
            {!licence || !simpleAuthEnabled ? (
              <Button
                text="Test user"
                href="ldapUserAccess"
                className="m-0"
                disabled={!licence || !simpleAuthEnabled}
              />
            ) : (
              <a href="ldapUserAccess" className="link">
                <Button
                  text="Test user"
                  href="ldapUserAccess"
                  className="m-0"
                  disabled={!licence || !simpleAuthEnabled}
                />
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
                  fullWidth
                  stringValue
                />
              )}
              label="Enable LDAP/AD authorisation"
            />
          </Grid>
        </Grid>

        <Typography variant="body1" className="heading mb-2">
          Roles
        </Typography>

        <Grid container>
          <Grid item xs={12} sm={4}>
            <FormField
              type="text"
              name={this.formModel.LdapGroupMemberAttribute.uniqueKey}
              label="Group member attribute"
              disabled={!licence || !saslAuthEnabled || !simpleAuthEnabled}
              fullWidth
            />
          </Grid>

          <Hidden xsDown>
            <Grid item sm={1} />
          </Hidden>

          <Grid item xs={12} sm={4}>
            <FormField
              type="text"
              name={this.formModel.LdapGroupAttribute.uniqueKey}
              label="Group name attribute"
              disabled={!licence || !saslAuthEnabled || !simpleAuthEnabled}
              fullWidth
            />
          </Grid>

          <Hidden xsDown>
            <Grid item sm={3} />
          </Hidden>

          <Grid item xs={12} sm={4}>
            <FormField
              type="text"
              name={this.formModel.LdapGroupSearchFilter.uniqueKey}
              label="Group search filter"
              disabled={!licence || !saslAuthEnabled || !simpleAuthEnabled}
              fullWidth
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
              label="Posix style groups"
            />
          </Grid>

          <Hidden xsDown>
            <Grid item sm={3} />
          </Hidden>
        </Grid>
      </form>
    );
  }
}

const mapStateToProps = (state: State) => ({
  values: getFormValues("LDAPForm")(state)
});

const LDAPForm = reduxForm({
  form: "LDAPForm",
  validate: validateMultipleMandatoryFields
})(
  connect<any, any, any>(
    mapStateToProps,
    null
  )(LDAPBaseForm)
);

export default LDAPForm;
