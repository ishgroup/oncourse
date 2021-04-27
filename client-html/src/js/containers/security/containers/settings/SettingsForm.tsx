/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import Grid from "@material-ui/core/Grid";
import Typography from "@material-ui/core/Typography";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import FormGroup from "@material-ui/core/FormGroup";
import {
  Form, change, reduxForm, initialize, getFormValues, Field
} from "redux-form";
import { connect } from "react-redux";
import isEmpty from "lodash.isempty";
import FormField from "../../../../common/components/form/form-fields/FormField";
import * as Model from "../../../../model/preferences/security/SecuritySettings";
import CustomAppBar from "../../../../common/components/layout/CustomAppBar";
import AppBarHelpMenu from "../../../../common/components/form/AppBarHelpMenu";
import Button from "../../../../common/components/buttons/Button";
import { Switch } from "../../../../common/components/form/form-fields/Switch";
import FormRadioButtons from "../../../../common/components/form/form-fields/FormRadioButtons";
import RouteChangeConfirm from "../../../../common/components/dialog/confirm/RouteChangeConfirm";
import { FormModelSchema } from "../../../../model/preferences/FormModelShema";
import { State } from "../../../../reducers/state";
import { getManualLink } from "../../../../common/utils/getManualLink";
import { PREFERENCES_AUDITS_LINK } from "../../../preferences/constants";

const manualUrl = getManualLink("users_Users");

class SettingsForm extends React.Component<any, any> {
  private formModel: FormModelSchema;

  constructor(props) {
    super(props);

    this.formModel = props.formatModel(Model);

    // Initializing form with values
    if (!isEmpty(props.formData)) {
      props.dispatch(initialize("SecuritySettingsForm", props.formData));
    }

    this.state = {
      enablePasswordScheduleField: Boolean(
        props.formData && props.formData[this.formModel.SecurityPasswordExpiryPeriod.uniqueKey]
      ),
      enableTOTPScheduleField: Boolean(
        props.formData && props.formData[this.formModel.SecurityTFAExpiryPeriod.uniqueKey]
      )
    };
  }

  componentDidUpdate(prevProps) {
    const { initialized, formData, submitSucceeded } = this.props;

    // Initializing form with values
    if ((!isEmpty(formData) && (!prevProps.initialized && !initialized)) || submitSucceeded) {
      this.props.dispatch(initialize("SecuritySettingsForm", formData));

      if (formData[this.formModel.SecurityPasswordExpiryPeriod.uniqueKey]) {
        this.setState({
          enablePasswordScheduleField: true
        });
      }

      if (formData[this.formModel.SecurityTFAExpiryPeriod.uniqueKey]) {
        this.setState({
          enableTOTPScheduleField: true
        });
      }
    }
  }

  onEnablePasswordSchedule = (e, checked) => {
    this.setState({
      enablePasswordScheduleField: checked
    });

    const scheduleKey = this.formModel.SecurityPasswordExpiryPeriod.uniqueKey;
    const scheduleValue = this.props.values[scheduleKey];

    if (checked && !scheduleValue) {
      this.props.dispatch(change("SecuritySettingsForm", scheduleKey, "30"));
    }

    if (!checked) {
      this.props.dispatch(change("SecuritySettingsForm", scheduleKey, null));
    }
  };

  onEnableTOTPSchedule = (e, checked) => {
    this.setState({
      enableTOTPScheduleField: checked
    });

    const scheduleKey = this.formModel.SecurityTFAExpiryPeriod.uniqueKey;
    const scheduleValue = this.props.values[scheduleKey];

    if (checked && !scheduleValue) {
      this.props.dispatch(change("SecuritySettingsForm", scheduleKey, "16"));
    }

    if (!checked) {
      this.props.dispatch(change("SecuritySettingsForm", scheduleKey, null));
    }
  };

  render() {
    const {
      enums, handleSubmit, onSave, dirty, data, form
    } = this.props;
    const { enablePasswordScheduleField, enableTOTPScheduleField } = this.state;

    return (
      <Form className="container" onSubmit={handleSubmit(onSave)}>
        <RouteChangeConfirm form={form} when={dirty} />

        <CustomAppBar>
          <Grid container>
            <Grid item xs={12} className="centeredFlex">
              <Typography className="appHeaderFontSize" color="inherit">
                Settings
              </Typography>

              <div className="flex-fill" />

              <AppBarHelpMenu
                created={data && data.created}
                modified={data && data.modified}
                auditsUrl={PREFERENCES_AUDITS_LINK}
                manualUrl={manualUrl}
              />

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
          <Grid item xs={12} sm={8} className="d-flex">
            <FormGroup>
              <FormControlLabel
                classes={{
                  root: "switchWrapper",
                  label: `${"switchLabel"} ${"switchLabelMargin"}`
                }}
                control={(
                  <FormField
                    type="switch"
                    name={this.formModel.SecurityAutoDisableInactiveAccount.uniqueKey}
                    color="primary"
                    stringValue
                  />
                )}
                label="Automatically disable inactive accounts"
              />

              <FormControlLabel
                classes={{
                  root: "switchWrapper",
                  label: "switchLabel"
                }}
                control={(
                  <FormField
                    type="switch"
                    name={this.formModel.SecurityPasswordComplexity.uniqueKey}
                    color="primary"
                    stringValue
                  />
                )}
                label="Require better passwords"
              />

              <FormControlLabel
                classes={{
                  root: "switchWrapper",
                  label: "switchLabel"
                }}
                control={
                  <Switch onChange={this.onEnablePasswordSchedule} checked={enablePasswordScheduleField} />
                }
                label={(
                  <Typography
                    variant="body2"
                    color="inherit"
                    component="span"
                    noWrap
                    onClick={e => e.preventDefault()}
                  >
                    Require password change every
                    <FormField
                      type="number"
                      name={this.formModel.SecurityPasswordExpiryPeriod.uniqueKey}
                      color="primary"
                      formatting="inline"
                      min="1"
                      max="999"
                      placeholder="30"
                      onChange={(e, v) => {
                        if (Number(v) === 0) e.preventDefault();
                      }}
                      onKeyPress={ev => {
                        if (ev.key.match(/[+\-e]/)) {
                          ev.preventDefault();
                        }
                      }}
                      disabled={!enablePasswordScheduleField}
                      hidePlaceholderInEditMode
                    />
                    days
                  </Typography>
                )}
              />

              <FormControlLabel
                classes={{
                  root: "switchWrapper",
                  label: "switchLabel"
                }}
                control={<Switch onChange={this.onEnableTOTPSchedule} checked={enableTOTPScheduleField} />}
                label={(
                  <Typography variant="body2" color="inherit" component="span" onClick={e => e.preventDefault()} noWrap>
                    Require two factor authentication every
                    <FormField
                      type="number"
                      name={this.formModel.SecurityTFAExpiryPeriod.uniqueKey}
                      formatting="inline"
                      min="1"
                      max="999"
                      placeholder="16"
                      step="1"
                      onChange={(e, v) => {
                        if (Number(v) === 0) e.preventDefault();
                      }}
                      onKeyPress={ev => {
                        if (ev.key.match(/[+\-e]/)) {
                          ev.preventDefault();
                        }
                      }}
                      disabled={!enableTOTPScheduleField}
                      hidePlaceholderInEditMode
                    />
                    hours
                  </Typography>
                )}
              />
            </FormGroup>
          </Grid>

          <Grid item xs={12} sm={8} className="mt-3">
            <Typography variant="body2" color="inherit" component="span" onClick={e => e.preventDefault()} noWrap>
              Disable account after
              <FormField
                  type="number"
                  name={this.formModel.SecurityNumberIncorrectLoginAttempts.uniqueKey}
                  formatting="inline"
                  min="1"
                  max="999"
                  placeholder="5"
                  step="1"
                  onChange={(e, v) => {
                    if (Number(v) === 0) e.preventDefault();
                  }}
                  onKeyPress={ev => {
                    if (ev.key.match(/[+\-e]/)) {
                      ev.preventDefault();
                    }
                  }}
                  hidePlaceholderInEditMode
              />
              incorrect login attempts
            </Typography>
          </Grid>

          <Grid item xs={12} sm={8} className="mt-3">
            <Typography className="heading">Two factor authentication</Typography>

            <Field
              name={this.formModel.SecurityTFAStatus.uniqueKey}
              component={FormRadioButtons}
              items={enums.TwoFactorAuthStatus}
              color="primary"
              stringValue
            />
          </Grid>
        </Grid>
      </Form>
    );
  }
}

const mapStateToProps = (state: State) => ({
  values: getFormValues("SecuritySettingsForm")(state)
});

const SettingsFormWrapped = reduxForm({
  form: "SecuritySettingsForm"
})(
  connect<any, any, any>(
    mapStateToProps,
    null
  )(SettingsForm)
);

export default SettingsFormWrapped;
