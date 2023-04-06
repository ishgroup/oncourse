/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import Grid from "@mui/material/Grid";
import Typography from "@mui/material/Typography";
import FormControlLabel from "@mui/material/FormControlLabel";
import FormGroup from "@mui/material/FormGroup";
import {
  Form, change, reduxForm, initialize, getFormValues, Field
} from "redux-form";
import { connect } from "react-redux";
import isEmpty from "lodash.isempty";
import FormField from "../../../../common/components/form/formFields/FormField";
import * as Model from "../../../../model/preferences/security/SecuritySettings";
import { Switch } from "../../../../common/components/form/formFields/Switch";
import FormRadioButtons from "../../../../common/components/form/formFields/FormRadioButtons";
import RouteChangeConfirm from "../../../../common/components/dialog/confirm/RouteChangeConfirm";
import { FormModelSchema } from "../../../../model/preferences/FormModelShema";
import { State } from "../../../../reducers/state";
import { getManualLink } from "../../../../common/utils/getManualLink";
import { PREFERENCES_AUDITS_LINK } from "../../../preferences/constants";
import { onSubmitFail } from "../../../../common/utils/highlightFormErrors";
import AppBarContainer from "../../../../common/components/layout/AppBarContainer";

const manualUrl = getManualLink("users_Users");

class SettingsForm extends React.Component<any, any> {
  private formModel: FormModelSchema;

  constructor(props) {
    super(props);

    // Initializing form with values
    if (!isEmpty(props.formData)) {
      props.dispatch(initialize("SecuritySettingsForm", props.formData));
    }

    this.formModel = props.formatModel(Model);

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
      enums, handleSubmit, onSave, dirty, data, form, invalid, formRoleName
    } = this.props;
    const { enablePasswordScheduleField, enableTOTPScheduleField } = this.state;

    return (
      <Form className="container" onSubmit={handleSubmit(onSave)} role={formRoleName}>
        <RouteChangeConfirm form={form} when={dirty} />

        <AppBarContainer
          values={data}
          manualUrl={manualUrl}
          getAuditsUrl={PREFERENCES_AUDITS_LINK}
          disabled={!dirty}
          invalid={invalid}
          title="Settings"
          disableInteraction
          createdOn={v => v.created}
          modifiedOn={v => v.modified}
          containerClass="p-3"
        >
          <Grid container>
            <Grid item xs={12} sm={8} className="d-flex">
              <FormGroup>
                <FormControlLabel
                  classes={{
                    root: "switchWrapper",
                    label: "switchLabel switchLabelMargin"
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
                        inline
                        min="1"
                        max="999"
                        placeholder="30"
                        onChange={(e, v) => {
                          if (Number(v) === 0) e.preventDefault();
                        }}
                        debounced={false}
                        onKeyPress={ev => {
                          if (ev.key.match(/[+\-e]/)) {
                            ev.preventDefault();
                          }
                        }}
                        disabled={!enablePasswordScheduleField}
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
                        inline
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
                        debounced={false}
                        disabled={!enableTOTPScheduleField}
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
                  inline
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
                  debounced={false}
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
        </AppBarContainer>
      </Form>
    );
  }
}

const mapStateToProps = (state: State) => ({
  values: getFormValues("SecuritySettingsForm")(state)
});

const SettingsFormWrapped = reduxForm({
  form: "SecuritySettingsForm",
  onSubmitFail
})(
  connect<any, any, any>(
    mapStateToProps,
    null
  )(SettingsForm)
);

export default SettingsFormWrapped;
