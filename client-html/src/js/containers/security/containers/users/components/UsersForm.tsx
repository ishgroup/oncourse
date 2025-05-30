/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { User, UserRole } from '@api/model';
import IconPhoneLocked from '@mui/icons-material/ScreenLockPortrait';
import { Button, Collapse, FormControlLabel, FormGroup, Grid, Paper, Typography } from '@mui/material';
import $t from '@t';
import clsx from 'clsx';
import { format as formatDate } from 'date-fns';
import { III_DD_MMM_YYYY_HH_MM_SPECIAL, SelectItemDefault, ShowConfirmCaller } from 'ish-ui';
import React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';
import { Dispatch } from 'redux';
import { change, Form, getFormSyncErrors, getFormValues, initialize, reduxForm } from 'redux-form';
import { withStyles } from 'tss-react/mui';
import { showConfirm } from '../../../../../common/actions';
import RouteChangeConfirm from '../../../../../common/components/dialog/RouteChangeConfirm';
import FormField from '../../../../../common/components/form/formFields/FormField';
import Uneditable from '../../../../../common/components/form/formFields/Uneditable';
import AppBarContainer from '../../../../../common/components/layout/AppBarContainer';
import { getManualLink } from '../../../../../common/utils/getManualLink';
import { onSubmitFail } from '../../../../../common/utils/highlightFormErrors';
import { State } from '../../../../../reducers/state';
import { disableUser2FA, resetUserPassword, updateUser } from '../../../actions';

const manualUrl = getManualLink("users");

const styles = theme => ({
  root: {},
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
  openConfirm?: ShowConfirmCaller;
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
  syncErrors: any;
}

class UsersFormBase extends React.PureComponent<FormProps, any> {
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

  // eslint-disable-next-line camelcase
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
    const {
     dirty, nextLocation, history
    } = this.props;

    if (nextLocation && !dirty) {
      history.push(nextLocation);
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

  onResetPassword = () => {
    const {
      user: { id },
      resetUserPassword,
      openConfirm
    } = this.props;

    openConfirm({
      onConfirm: () => {
        resetUserPassword(id);
      },
      confirmMessage: "Remove existing password and send the user an invite to reset their password.",
      confirmButtonText: "Send invite",
      title: null
    });
  };

  onDisable2FA = () => {
    const {
      user: { id },
      disableUser2FA,
      openConfirm
    } = this.props;

    openConfirm({
      onConfirm: () => {
        disableUser2FA(id);
      },
      confirmMessage: "Current password will be changed to generated one",
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
      form,
      syncErrors
    } = this.props;

    return (
      <Form onSubmit={handleSubmit(this.onSave)} className={className}>
        {!isNew && dirty && <RouteChangeConfirm form={form} when={dirty} />}

        <AppBarContainer
          values={values}
          manualUrl={manualUrl}
          getAuditsUrl='audit?search=~"SystemUser"'
          disabled={!dirty && !isNew && !values.inviteAgain}
          invalid={invalid}
          title={values.email ? values.email : "No email"}
          hideHelpMenu={isNew}
          createdOn={() => (created ? new Date(created) : null)}
          modifiedOn={() => (modified ? new Date(modified) : null)}
          containerClass="p-3"
          opened={isNew || Object.keys(syncErrors).includes("email")}
          fields={(
            <Grid item xs={8}>
              <FormField
                type="text"
                name="email"
                label={$t('email2')}
                validate={validateUniqueNames}
                required
              />
            </Grid>
          )}
          submitButtonText={isNew ? "Invite" : values.inviteAgain ? "Resend invite" : "Save"}
        >
          <FormControlLabel
            control={<FormField type="switch" name="active" color="primary" />}
            label={$t('active')}
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
                label={$t('first_name')}
                className="mb-2"
                required
              />
              <FormField
                type="text"
                name="lastName"
                label={$t('last_name')}
                className="mb-2"
                required
              />

              <FormField
                type="select"
                name="administrationCentre"
                label={$t('bank_cashcheques_to_site')}
                className="mb-2"
                items={sites || []}
                required
              />

              {!isNew && (
                <Uneditable
                  value={lastLoggedIn}
                  format={v => formatDate(new Date(v), III_DD_MMM_YYYY_HH_MM_SPECIAL)}
                  label={$t('last_logged_in')}
                  className="mb-2"
                />
              )}

              {!isNew && !values.inviteAgain && (
                <FormControlLabel
                  control={<FormField type="switch" name="passwordUpdateRequired" color="primary" />}
                  label={$t('require_password_update')}
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
                      {$t('reset_password')}
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
                    {$t('two_factor_authentication_is')}
                    {" "}
                    {tfaEnabled ? "enabled" : "disabled"}
                  </Typography>
                  {tfaEnabled && (
                    <Button variant="outlined" color="secondary" className={classes.button} onClick={this.onDisable2FA}>
                      {$t('disable_2fa')}
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
                        debounced={false}
                      />
                    )}
                    label={$t('admin')}
                    labelPlacement="start"
                  />

                  <Collapse in={!values.admin}>
                    <FormField
                      type="select"
                      name="role"
                      label={$t('role')}
                      className={classes.inputField}
                      selectValueMark="id"
                      selectLabelMark="name"
                      items={userRoles || []}
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
                    label={$t('can_access_editor')}
                    labelPlacement="start"
                  />
                </FormGroup>
              </Paper>
            </Grid>
            <Grid item sm={false} md={false} lg={1} xl={6} />
          </Grid>
        </AppBarContainer>
      </Form>
    );
  }
}

const mapStateToProps = (state: State) => ({
  values: getFormValues("UsersForm")(state),
  syncErrors: getFormSyncErrors("UsersForm")(state),
  fetch: state.fetch,
  nextLocation: state.nextLocation,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  updateUser: (user: User) => dispatch(updateUser(user)),
  resetUserPassword: (id: number) => dispatch(resetUserPassword(id)),
  disableUser2FA: (id: number) => dispatch(disableUser2FA(id)),
  openConfirm: props => dispatch(showConfirm(props))
});

const UsersForm = reduxForm({
  form: "UsersForm",
  onSubmitFail
})(connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withStyles(withRouter(UsersFormBase), styles)));

export default UsersForm;
