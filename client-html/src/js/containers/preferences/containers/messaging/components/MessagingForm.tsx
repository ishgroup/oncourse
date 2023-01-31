/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import {
  Form, reduxForm, initialize, getFormValues
} from "redux-form";
import { connect } from "react-redux";
import isEmpty from "lodash.isempty";
import FormControlLabel from "@mui/material/FormControlLabel";
import Grid from "@mui/material/Grid";
import Hidden from "@mui/material/Hidden";
import ExitToApp from "@mui/icons-material/ExitToApp";
import Typography from "@mui/material/Typography";
import Divider from "@mui/material/Divider";
import Button from "@mui/material/Button";
import FormField from "../../../../../common/components/form/formFields/FormField";
import { onSubmitFail } from "../../../../../common/utils/highlightFormErrors";
import { openInternalLink } from "../../../../../common/utils/links";
import * as Model from "../../../../../model/preferences/Messaging";
import { FormModelSchema } from "../../../../../model/preferences/FormModelShema";
import { validateSingleMandatoryField, validateEmail } from "../../../../../common/utils/validation";
import { State } from "../../../../../reducers/state";
import RouteChangeConfirm from "../../../../../common/components/dialog/confirm/RouteChangeConfirm";
import DynamicText from "./DynamicText";
import { getManualLink } from "../../../../../common/utils/getManualLink";
import { PREFERENCES_AUDITS_LINK } from "../../../constants";
import AppBarContainer from "../../../../../common/components/layout/AppBarContainer";

const manualUrl = getManualLink("generalPrefs_messaging");

class MessagingBaseForm extends React.Component<any, any> {
  private formModel: FormModelSchema;

  constructor(props) {
    super(props);

    if (!isEmpty(props.formData)) {
      props.dispatch(initialize("MessagingForm", props.formData));
    }

    this.validateEmailFromAddress = this.validateEmailFromAddress.bind(this);

    this.formModel = props.formatModel(Model);
  }

  // eslint-disable-next-line camelcase
  UNSAFE_componentWillReceiveProps(nextProps) {
    // Initializing form with values
    if (!isEmpty(nextProps.formData) && !this.props.initialized) {
      this.props.dispatch(initialize("MessagingForm", nextProps.formData));
    }
  }

  validateEmailFromAddress = value => (value ? undefined : "Field is mandatory");

  render() {
    const {
      classes,
      handleSubmit,
      onSave,
      values,
      emailQueued,
      emailCount,
      smsQueued,
      smsCount,
      dirty,
      data,
      invalid,
      form,
      formRoleName,
    } = this.props;

    const emailBounceEnabled = values && values[this.formModel.EmailBounceEnabled.uniqueKey] === "true";

    return (
      <Form className="container" onSubmit={handleSubmit(onSave)} role={formRoleName}>
        <RouteChangeConfirm form={form} when={dirty} />

        <AppBarContainer
          values={data}
          manualUrl={manualUrl}
          getAuditsUrl={PREFERENCES_AUDITS_LINK}
          disabled={!dirty}
          invalid={invalid}
          title="Messaging"
          disableInteraction
          createdOn={v => v.created}
          modifiedOn={v => v.modified}
        >
          <Typography variant="body1" className="heading mb-2">
            Outgoing Emails
          </Typography>

          <Grid container columnSpacing={3} rowSpacing={2}>
            <Grid item xs={12} sm={5} lg={4}>
              <FormField
                type="text"
                name={this.formModel.EmailFromAddress.uniqueKey}
                label="Email"
                validate={[this.validateEmailFromAddress, validateEmail]}
              />
            </Grid>

            <Hidden xsDown>
              <Grid item sm={1} lg={1} />
            </Hidden>

            <Grid item xs={12} sm={5} lg={4}>
              <FormField
                type="text"
                name={this.formModel.EmailFromName.uniqueKey}
                label="Email from name (e.g. College ABC)"
              />
            </Grid>

            <Hidden mdDown>
              <Grid item lg={3} />
            </Hidden>

            <Grid item xs={12} sm={5} lg={4}>
              <FormField
                type="text"
                name={this.formModel.EmailAdminAddress.uniqueKey}
                label="System administrator email address"
                validate={validateEmail}
              />
            </Grid>

            <Grid item xs={12} className="mb-1">
              <Typography variant="subtitle1" className="centeredFlex">
                <DynamicText defaultValue="0" text=" Emails queued" value={emailCount} function={emailQueued} />
                <Button
                  size="small"
                  variant="text"
                  className={classes.subheadingButton}
                  onClick={() => openInternalLink("/message?filter=@Email&search=status is QUEUED")}
                  endIcon={<ExitToApp color="secondary" className={classes.buttonIcon} />}
                />
              </Typography>
            </Grid>

            <Grid item xs={12} container>
              <Grid item xs={12} lg={8}>
                <Divider />
              </Grid>
            </Grid>

            <Grid item xs={12} className="mb-2 mt-2">
              <FormControlLabel
                classes={{
                  root: classes.checkbox
                }}
                control={(
                  <FormField
                    type="checkbox"
                    name={this.formModel.EmailBounceEnabled.uniqueKey}
                    color="primary"
                    stringValue
                  />
                )}
                label="Detect and process bounced emails (VERP)"
              />
            </Grid>

            <Grid item xs={12} sm={5} lg={4}>
              <FormField
                type="text"
                name={this.formModel.EmailPop3Host.uniqueKey}
                label="Incoming mail server address (POP3)"
                validate={emailBounceEnabled ? validateSingleMandatoryField : null}
              />
            </Grid>

            <Hidden xsDown>
              <Grid item sm={1} lg={1} />
            </Hidden>

            <Grid item xs={12} sm={5} lg={4}>
              <FormField
                type="text"
                name={this.formModel.EmailBounceAddress.uniqueKey}
                label="Email address to which bounces are sent"
                validate={emailBounceEnabled ? [validateSingleMandatoryField, validateEmail] : validateEmail}
              />
            </Grid>
          </Grid>

          <Grid container columnSpacing={3} className="mb-1 mt-2">
            <Grid item xs={12} sm={5} lg={4}>
              <FormField
                type="text"
                name={this.formModel.EmailPop3Account.uniqueKey}
                label="Account"
                validate={emailBounceEnabled ? validateSingleMandatoryField : null}
              />
            </Grid>

            <Hidden xsDown>
              <Grid item sm={1} lg={1} />
            </Hidden>

            <Grid item xs={12} sm={5} lg={4}>
              <FormField
                type="password"
                name={this.formModel.EmailPop3Password.uniqueKey}
                label="Password"
              />
            </Grid>
          </Grid>

          <Grid container className="mb-1">
            <Grid item xs={12} lg={8}>
              <Divider />
            </Grid>
          </Grid>

          <Typography variant="body1" className="heading">
            SMS
          </Typography>

          <Grid container columnSpacing={3}>
            <Grid item xs={12} sm={5} lg={4}>
              <Typography variant="subtitle1" className="centeredFlex mt-1">
                <DynamicText defaultValue="0" text=" SMS queued" value={smsCount} function={smsQueued} />
                <Button
                  size="small"
                  variant="text"
                  className={classes.subheadingButton}
                  onClick={() => openInternalLink("/message?filter=@SMS&search=status is QUEUED")}
                  endIcon={<ExitToApp color="secondary" className={classes.buttonIcon} />}
                />
              </Typography>
            </Grid>

            <Hidden xsDown>
              <Grid item sm={1} lg={1} />
            </Hidden>

            <Grid item xs={12} sm={5} lg={4}>
              <FormField
                type="text"
                name={this.formModel.SMSFromAddress.uniqueKey}
                label="SMS from"
              />
            </Grid>
          </Grid>
        </AppBarContainer>
      </Form>
    );
  }
}

const mapStateToProps = (state: State) => ({
  values: getFormValues("MessagingForm")(state),
  emailCount: state.preferences.email,
  smsCount: state.preferences.sms
});

const MessagingForm = reduxForm({
  form: "MessagingForm",
  onSubmitFail
})(
  connect<any, any, any>(
    mapStateToProps,
    null
  )(MessagingBaseForm)
);

export default MessagingForm;
