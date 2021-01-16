/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { ComponentClass } from "react";
import {
 withStyles, FormControlLabel, FormGroup, Typography, Grid, Paper, Collapse
} from "@material-ui/core";
import clsx from "clsx";
import {
  Form, getFormValues, startAsyncValidation, initialize, reduxForm, change
} from "redux-form";
import { withRouter } from "react-router";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { format as formatDate } from "date-fns";
import IconPhoneLocked from "@material-ui/icons/ScreenLockPortrait";
import debounce from "lodash.debounce";
import { User, UserRole } from "@api/model";
import Button from "../../../../../common/components/buttons/Button";
import FormField from "../../../../../common/components/form/form-fields/FormField";
import FormSubmitButton from "../../../../../common/components/form/FormSubmitButton";
import { onSubmitFail } from "../../../../../common/utils/highlightFormClassErrors";
import AppBarHelpMenu from "../../../../../common/components/form/AppBarHelpMenu";
import CustomAppBar from "../../../../../common/components/layout/CustomAppBar";
import { State } from "../../../../../reducers/state";
import {
 updateUser, validateNewUserPassword, resetUserPassword, disableUser2FA
} from "../../../actions";
import RouteChangeConfirm from "../../../../../common/components/dialog/confirm/RouteChangeConfirm";
import Message from "../../../../../common/components/dialog/message/Message";
import { SelectItemDefault } from "../../../../../model/entities/common";
import { getManualLink } from "../../../../../common/utils/getManualLink";
import { III_DD_MMM_YYYY_HH_MM_SPECIAL } from "../../../../../common/utils/dates/format";
import { setNextLocation, showConfirm } from "../../../../../common/actions";
import Uneditable from "../../../../../common/components/form/Uneditable";

const manualUrl = getManualLink("users");

const styles = theme => ({
  paperPadding: {
    padding: "26px"
  },
  paperBottomMargin: {
    marginBottom: "26px"
  },
  inputField: {
    marginTop: "20px"
  },
  passwordUpdateSection: {
    marginTop: "24px",
    marginBottom: "26px"
  },
  passwordCopy: {
    width: "35px",
    height: "35px",
    padding: theme.spacing(0.5)
  },
  resetSection: {
    marginBottom: "30px"
  },
  lockedIcon: {
    fontSize: "1.3em"
  },
  cardLabel: {
    justifyContent: "flex-end",
    marginLeft: 0
  },
  cardSwitch: {
    marginLeft: 0
  },
  placeholder: {
    color: "#e0e0e0"
  },
  loader: {
    marginBottom: "5px",
    marginLeft: "-10px"
  },
  button: {
    display: "block"
  }
});

interface Props {
  user?: User;
  userRoles?: UserRole[];
  sites?: SelectItemDefault[];
  validateUniqueNames?: any;
  passwordComplexityFlag?: string;
  isNew?: boolean;
  oldEmail?: string;
  openConfirm?: (onConfirm: any, confirmMessage?: string, confirmButtonText?: string) => void;
}

interface FormProps extends Props {
  values: User;
  classes: any;
  dispatch: any;
  className: string;
  form: string;
  updateUser: (user: User) => void;
  resetUserPassword: (id: number) => void;
  disableUser2FA: (id: number) => void;
  handleSubmit: any;
  dirty: boolean;
  asyncValidating: boolean;
  invalid: boolean;
  submitSucceeded: boolean;
  fetch: any;
  asyncErrors: any;
  history: any;
  nextLocation: string;
  setNextLocation: (nextLocation: string) => void;
}

class UsersFormBase extends React.PureComponent<FormProps, any> {
  state = {
    showMessage: false,
    messageText: ""
  };

  private resolvePromise;

  private rejectPromise;

  private isPending;

  constructor(props) {
    super(props);

    // Initializing form with values
    if (props.user) {
      props.dispatch(initialize("UsersForm", props.user));
    }
  }

  UNSAFE_componentWillReceiveProps(nextProps) {
    const {
     user, submitSucceeded, isNew, fetch
    } = nextProps;

    if (user && (!this.props.user || this.props.user.id !== user.id || (submitSucceeded && !isNew))) {
      this.props.dispatch(initialize("UsersForm", user));
    }

    if (this.isPending && fetch && fetch.success === false) {
      this.isPending = false;
      this.rejectPromise();
    }
    if (this.isPending && fetch && fetch.success) {
      this.isPending = false;
      this.resolvePromise();
    }
  }

  componentDidUpdate() {
    const { dirty, nextLocation, setNextLocation, history } = this.props;

    if (nextLocation && !dirty) {
      history.push(nextLocation);
      setNextLocation('');
    }
  }

  onSave = values => {
    this.isPending = true;

    return new Promise((resolve, reject) => {
      this.resolvePromise = resolve;
      this.rejectPromise = reject;

      this.props.updateUser(values);
    });
  };

  onPasswordChange = debounce(() => {
    const {
      dispatch,
      values: { password },
      passwordComplexityFlag
    } = this.props;

    if (password && passwordComplexityFlag === "true") {
      dispatch(startAsyncValidation("UsersForm"));
      dispatch(validateNewUserPassword(password));
    }
  }, 500);

  validatePassword = value => {
    const { passwordComplexityFlag, values } = this.props;

    if (passwordComplexityFlag === "true") {
      return undefined;
    }

    if (value && values.login && value.trim() === values.login.trim()) {
      return "You must enter password which is different to login";
    }

    return value && value.trim().length < 5 ? "Password minimum length is 5 symbols" : undefined;
  };

  copyToClipBoard = value => {
    const $tempInput: any = document.createElement("INPUT");
    document.body.appendChild($tempInput);
    $tempInput.setAttribute("value", value);
    $tempInput.select();
    document.execCommand("copy");
    document.body.removeChild($tempInput);
    this.setState({
      showMessage: true,
      messageText: "Password Copied"
    });
  };

  onResetPassword = () => {
    const {
      user: { id },
      resetUserPassword,
      openConfirm
    } = this.props;

    openConfirm(() => {
      resetUserPassword(id);
    }, "Remove existing password and send the user an invite to reset their password.",
     "Send invite");
  };

  onDisable2FA = () => {
    const {
      user: { id },
      disableUser2FA,
      openConfirm
    } = this.props;

    openConfirm(() => {
      disableUser2FA(id);
    }, "Current password will be changed to generated one");
  };

  clearMessage = () => {
    this.setState({
      showMessage: false,
      messageText: ""
    });
  };

  render() {
    const {
      classes,
      className,
      user: {
       tfaEnabled = false, created = "", modified = "", lastLoggedIn = ""
      } = {},
      values = {},
      userRoles,
      sites,
      dispatch,
      handleSubmit,
      dirty,
      validateUniqueNames,
      isNew,
      invalid,
      form
    } = this.props;

    const { showMessage, messageText } = this.state;

    return (
      <Form onSubmit={handleSubmit(this.onSave)} className={className}>
        {!isNew && dirty && <RouteChangeConfirm form={form} when={dirty} />}

        <Message opened={showMessage} isSuccess text={messageText} clearMessage={this.clearMessage} />

        <CustomAppBar>
          <Grid container>
            <Grid item xs={12} className="centeredFlex">
              <Typography color="inherit" className="appHeaderFontSize pl-2" noWrap>
                {values.email ? values.email : "No email"}
              </Typography>

              <div className="flex-fill" />

              <AppBarHelpMenu
                created={created ? new Date(created) : null}
                modified={modified ? new Date(modified) : null}
                auditsUrl={'audit?search=~"SystemUser"'}
                manualUrl={manualUrl}
              />

              <FormSubmitButton
                disabled={!dirty && !isNew && !values.inviteAgain}
                invalid={invalid}
                text={isNew ? "Invite" : values.inviteAgain ? "Resend invite" : "Save"}
              />
            </Grid>
          </Grid>
        </CustomAppBar>

        <FormControlLabel
          control={<FormField type="switch" name="active" color="primary" />}
          label="Active"
          labelPlacement="start"
          classes={{
            root: "switchWrapper mb-2"
          }}
        />

        <Grid container>
          <Grid item xs={12} sm={5} lg={5} xl={3}>
            <FormField
              type="text"
              name="firstName"
              label="First name"
              required
              fullWidth
            />
            <FormField
              type="text"
              name="lastName"
              label="Last name"
              required
              fullWidth
            />

            <FormField
              type="text"
              name="email"
              label="Email"
              validate={validateUniqueNames}
              required
              fullWidth
            />

            <FormField
              type="select"
              name="administrationCentre"
              label="Bank cash/cheques to site"
              fullWidth
              autoWidth={false}
              items={sites || []}
              required
            />

            {!isNew && (
              <Uneditable
                value={lastLoggedIn}
                format={v => formatDate(new Date(v), III_DD_MMM_YYYY_HH_MM_SPECIAL)}
                label="Last logged in"
              />
            )}

            {!isNew && !values.inviteAgain && (
              <FormControlLabel
                control={<FormField type="switch" name="passwordUpdateRequired" color="primary" />}
                label="Require password update"
                labelPlacement="start"
                className={classes.passwordUpdateSection}
                classes={{
                  root: "switchWrapper mb-0"
                }}
              />
            )}

            {!isNew && (
              <div className={classes.resetSection}>
                {!values.inviteAgain && (
                  <Button variant="outlined" color="secondary" className={classes.button} onClick={this.onResetPassword}>
                    Reset password
                  </Button>
                )}
              </div>
            )}

            {!isNew && (
              <div className={classes.resetSection}>
                <Typography
                  variant="caption"
                  color={tfaEnabled ? "textPrimary" : "textSecondary"}
                  className="mb-1 centeredFlex"
                >
                  <IconPhoneLocked className={classes.lockedIcon} />
                  {' '}
                  Two factor authentication is
                  {" "}
                  {tfaEnabled ? "enabled" : "disabled"}
                </Typography>
                {tfaEnabled && (
                  <Button variant="outlined" color="secondary" className={classes.button} onClick={this.onDisable2FA}>
                    Disable 2FA
                  </Button>
                )}
              </div>
            )}
          </Grid>
          <Grid item sm={false} md={1} lg={1} xl={false} />
          <Grid item xs={12} sm={5} lg={5} xl={3}>
            <Paper className={clsx(classes.paperPadding, classes.paperBottomMargin)}>
              <FormGroup>
                <FormControlLabel
                  classes={{
                    root: classes.cardLabel
                  }}
                  control={(
                    <FormField
                      type="switch"
                      name="admin"
                      color="primary"
                      className={classes.cardSwitch}
                      onChange={(e, v) => {
                        if (v) {
                          dispatch(change("UsersForm", "role", null));
                        }
                      }}
                    />
                  )}
                  label="Admin"
                  labelPlacement="start"
                />

                <Collapse in={!values.admin}>
                  <FormField
                    type="select"
                    name="role"
                    label="Role"
                    className={classes.inputField}
                    selectValueMark="id"
                    selectLabelMark="name"
                    items={userRoles || []}
                    fullWidth
                    required={!values.admin}
                    sort
                  />
                </Collapse>

                <FormControlLabel
                  classes={{
                    root: classes.cardLabel
                  }}
                  control={
                    <FormField type="switch" name="accessEditor" color="primary" className={classes.cardSwitch} />
                  }
                  label="Can access #editor"
                  labelPlacement="start"
                />
              </FormGroup>
            </Paper>
          </Grid>
          <Grid item sm={false} md={false} lg={1} xl={6} />
        </Grid>
      </Form>
    );
  }
}

const mapStateToProps = (state: State) => ({
  values: getFormValues("UsersForm")(state),
  fetch: state.fetch,
  nextLocation: state.nextLocation,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  updateUser: (user: User) => dispatch(updateUser(user)),
  resetUserPassword: (id: number) => dispatch(resetUserPassword(id)),
  disableUser2FA: (id: number) => dispatch(disableUser2FA(id)),
  openConfirm: (onConfirm: any, confirmMessage?: string, confirmButtonText?: string) => dispatch(showConfirm(onConfirm, confirmMessage, confirmButtonText)),
  setNextLocation: (nextLocation: string) => dispatch(setNextLocation(nextLocation)),
});

const UsersForm = reduxForm({
  form: "UsersForm",
  onSubmitFail
})(connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withStyles(styles)(withRouter(UsersFormBase))));

export default UsersForm as ComponentClass<Props>;
