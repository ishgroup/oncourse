/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import { FormControlLabel } from "@material-ui/core";
import Grid from "@material-ui/core/Grid";
import Hidden from "@material-ui/core/Hidden";
import { ExitToApp } from "@material-ui/icons";
import Typography from "@material-ui/core/Typography";
import Divider from "@material-ui/core/Divider";
import {
  reduxForm, initialize, getFormValues
} from "redux-form";
import { connect } from "react-redux";
import isEmpty from "lodash.isempty";
import Button from "../../../../../common/components/buttons/Button";
import FormField from "../../../../../common/components/form/form-fields/FormField";
import FormSubmitButton from "../../../../../common/components/form/FormSubmitButton";
import { onSubmitFail } from "../../../../../common/utils/highlightFormClassErrors";
import { openInternalLink } from "../../../../../common/utils/links";
import * as Model from "../../../../../model/preferences/Messaging";
import { FormModelSchema } from "../../../../../model/preferences/FormModelShema";
import { validateSingleMandatoryField, validateEmail } from "../../../../../common/utils/validation";
import { State } from "../../../../../reducers/state";
import CustomAppBar from "../../../../../common/components/layout/CustomAppBar";
import RouteChangeConfirm from "../../../../../common/components/dialog/confirm/RouteChangeConfirm";
import AppBarHelpMenu from "../../../../../common/components/form/AppBarHelpMenu";
import DynamicText from "./DynamicText";
import { getManualLink } from "../../../../../common/utils/getManualLink";
import { PREFERENCES_AUDITS_LINK } from "../../../constants";

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

  componentWillReceiveProps(nextProps) {
    // Initializing form with values
    if (!isEmpty(nextProps.formData) && !this.props.initialized) {
      this.props.dispatch(initialize("MessagingForm", nextProps.formData));
    }
  }

  validateEmailFromAddress(value) {
    return value ? undefined : "Field is mandatory";
  }

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
      form
    } = this.props;

    const emailBounceEnabled = values && values[this.formModel.EmailBounceEnabled.uniqueKey] === "true";

    return (
      <form className="container" onSubmit={handleSubmit(onSave)}>
        <RouteChangeConfirm form={form} when={dirty} />

        <CustomAppBar>
          <Grid container>
            <Grid item xs={12} className="centeredFlex">
              <Typography className="appHeaderFontSize" color="inherit" noWrap>
                Messaging
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

        <Typography variant="body1" className="heading mb-2">
          Outgoing Emails
        </Typography>

        <Grid container>
          <Grid item xs={12} sm={5} lg={4}>
            <FormField
              type="text"
              name={this.formModel.EmailFromAddress.uniqueKey}
              label="Email"
              validate={[this.validateEmailFromAddress, validateEmail]}
              fullWidth
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
              fullWidth
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
              fullWidth
            />
          </Grid>

          <Grid item xs={12} className="mb-1">
            <Typography variant="subtitle1" className="centeredFlex">
              <DynamicText defaultValue="0" text=" Emails queued" value={emailCount} function={emailQueued} />
              <Button
                size="small"
                variant="text"
                className={classes.subheadingButton}
                onClick={() => openInternalLink("/message?filter=@Email&search=messagePersons.status is QUEUED")}
                rightIcon={() => <ExitToApp color="secondary" className={classes.buttonIcon} />}
              />
            </Typography>
          </Grid>

          <Grid container spacing={5}>
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
                  value="true"
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
              fullWidth
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
              fullWidth
            />
          </Grid>
        </Grid>

        <Grid container className="mb-1">
          <Grid item xs={12} sm={5} lg={4}>
            <FormField
              type="text"
              name={this.formModel.EmailPop3Account.uniqueKey}
              label="Account"
              validate={emailBounceEnabled ? validateSingleMandatoryField : null}
              fullWidth
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
              fullWidth
            />
          </Grid>
        </Grid>

        <Grid container spacing={5} className="mb-1">
          <Grid item xs={12} lg={8}>
            <Divider />
          </Grid>
        </Grid>

        <Typography variant="body1" className="heading">
          SMS
        </Typography>

        <Grid container>
          <Grid item xs={12} sm={5} lg={4}>
            <Typography variant="subtitle1" className="centeredFlex">
              <DynamicText defaultValue="0" text=" SMS queued" value={smsCount} function={smsQueued} />
              <Button
                size="small"
                variant="text"
                className={classes.subheadingButton}
                onClick={() => openInternalLink("/message?filter=@SMS&search=messagePersons.status is QUEUED")}
                rightIcon={() => <ExitToApp color="secondary" className={classes.buttonIcon} />}
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
              fullWidth
            />
          </Grid>
        </Grid>
      </form>
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
