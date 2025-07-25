/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

import { LoginRequest } from '@api/model';
import ExpandLess from '@mui/icons-material/ExpandLess';
import ExpandMore from '@mui/icons-material/ExpandMore';
import LoadingButton from '@mui/lab/LoadingButton';
import { Button, Collapse, darken, Grid, Typography } from '@mui/material';
import IconButton from '@mui/material/IconButton';
import Slide from '@mui/material/Slide';
import { alpha } from '@mui/material/styles';
import $t from '@t';
import { FormTextField } from 'ish-ui';
import QRCode from 'qrcode.react';
import * as React from 'react';
import { useEffect, useRef, useState } from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps } from 'react-router';
import { Action, Dispatch } from 'redux';
import {
  change,
  clearFields,
  DecoratedFormProps,
  Field,
  FieldArray,
  Form,
  initialize,
  reduxForm,
  touch
} from 'redux-form';
import { withStyles } from 'tss-react/mui';
import { setLoginState } from '../../common/actions';
import Logo from '../../common/components/layout/Logo';
import { validateSingleMandatoryField } from '../../common/utils/validation';
import { PASSWORD_PASS_SCORE } from '../../constants/Config';
import { Fetch } from '../../model/common/Fetch';
import { State } from '../../reducers/state';
import { SSOProviders } from '../automation/containers/integrations/components/SSOProviders';
import { isComplexPassRequired } from '../preferences/actions';
import {
  checkPassword,
  createPasswordRequest,
  getEmailByToken,
  getSsoIntegrations,
  postKickOutSsoAuthenticationRequest,
  postLoginRequest,
  updatePasswordRequest
} from './actions';
import AuthCodeFieldRenderer from './components/AuthCodeFieldRenderer';
import Credits from './components/Credits';
import EulaDialog from './components/EulaDialog';
import NewPasswordField from './components/NewPasswordField';
import { LoginState } from './reducers/state';

const FORM_NAME = "LoginForm";

const styles = (theme => ({
  loginFormWrapper: {
    maxWidth: "520px",
    display: "flex",
    margin: "0 auto",
    height: "100%",
    minHeight: "100vh",
    padding: theme.spacing(3)
  },
  loginModalWrapper: {
    padding: theme.spacing(5, 2, 3),
    minHeight: 320,
    borderRadius: "2px",
    color: theme.palette.text.hint,
    fontSize: theme.spacing(2),
    display: "flex",
    flexDirection: "column",
    background: theme.palette.background.paper,
    margin: "0 auto",
    [theme.breakpoints.up("sm")]: {
      padding: theme.spacing(7, 4, 3)
    }
  },
  logoWrapper: {
    marginLeft: "-11px"
  },
  textWrapper: {
    marginBottom: theme.spacing(3)
  },
  code: {
    marginLeft: theme.spacing(0)
  },
  buttonsContainer: {
    display: "flex",
    justifyContent: "flex-end",
    alignItems: "center"
  },
  declineButton: {
    color: theme.palette.primary.main,
    "&:hover": {
      backgroundColor: "rgba(248, 169, 74, 0.2)"
    }
  },
  textFieldWrapper: {
    minHeight: "61px",
    "& input": {
      borderBottom: `1px solid ${theme.palette.divider}`
    }
  },
  extLink: {
    marginLeft: theme.spacing(2),
    color: "#5362b1"
  },
  portField: {
    width: theme.spacing(10),
    marginLeft: theme.spacing(3)
  },
  link: {
    textDecoration: "none"
  },
  loginButton: {
    color: theme.palette.primary.contrastText,
    marginLeft: theme.spacing(2),
    backgroundColor: theme.palette.primary.main,
    "&:hover": {
      backgroundColor: darken(theme.palette.primary.main, 0.1)
    }
  },
  loginButtonDisabled: {
    backgroundColor: alpha(theme.palette.primary.main, 0.5)
  },
  sideImageWrapper: {
    background: 'url("https://ish-oncourse-sttrianians.s3.ap-southeast-2.amazonaws.com/88d2fb9a-0141-4014-be17-9ed898197727") no-repeat',
    backgroundSize: 'cover',
    backgroundPosition: 'center',
    position: "absolute",
    width: "100%",
    height: "100%",
    top: 0,
    left: "-100%",
    overflow: "hidden",
    display: "none",
    [theme.breakpoints.up("md")]: {
      display: "block"
    }
  },
  loginFormRight: {
    position: "relative"
  },
  footerIshLogo: {
    position: "relative",
    top: 10
  },
  creditsWrapper: {
    color: "#6b6a68"
  },
  creditHeader: {
    fontWeight: 500
  },
  versionText: {
    textAlign: "left",
    [theme.breakpoints.up("md")]: {
      textAlign: "right"
    }
  },
  authCodefield: {
    "& input": {
      padding: "9px 13px",
      [theme.breakpoints.up("sm")]: {
        padding: "18px 22px"
      },
      [theme.breakpoints.up("md")]: {
        padding: "15px 19px"
      }
    }
  }
}));

const validatePasswordConfirm = (value, allValues) => {
  if (!value) {
    return "Reenter your new password to confirm";
  }
  if (value !== allValues.newPassword && value !== allValues.newPasswordAsync) {
    return "Passwords do not match";
  }
  return undefined;
};

let resolveAsyncValidate;
let rejectAsyncValidate;

const asyncValidate = (values, dispatch) => new Promise((resolve, reject) => {
    if (values.newPasswordAsync) {
      resolveAsyncValidate = resolve;
      rejectAsyncValidate = reject;
      dispatch(checkPassword(values.newPasswordAsync, values.host, values.port));
      return;
    }
    reject("Enter your new password");
  }).catch(error => {
    throw { newPasswordAsync: error };
  });

const validatePasswordStrengthLight = (value, values) => {
  if (!value) {
    return "Enter your new password";
  }

  if (value && values.user && value.trim() === values.user.trim()) {
    return "You must enter password which is different to login";
  }

  return value && value.trim().length < 5 ? "Password minimum length is 5 symbols" : undefined;
};

interface Props extends LoginState {
  classes: any;
  handleSubmit: any;
  anyTouched: boolean;
  invalid: boolean;
  complexPass: any;
  totpUrl: string;
  postLoginRequest: (body: LoginRequest, host?: string, port?: number) => void;
  updatePasswordRequest: (value: string) => void;
  setLoginState: (value: LoginState) => void;
  isComplexPassRequired: () => void;
  submit: () => void;
  getSSO: () => void;
  resetLoginForm: () => void;
  dispatch: (action: Action) => void;
  getEmailByToken: (value: string) => void;
  createPasswordRequest: (token: string, password: string) => void;
  email?: string;
  eulaUrl?: string;
  ssoTypes?: number[];
  fetch?: Fetch;
}


export function LoginPageBase(
  {
    postLoginRequest,
    updatePasswordRequest,
    isUpdatePassword,
    isNewPassword,
    isKickOut,
    submittingSSOType,
    totpUrl,
    isOptionalTOTP,
    createPasswordRequest,
    isComplexPassRequired,
    passwordComplexity,
    asyncValidating,
    setLoginState,
    dispatch,
    getSSO,
    fetch,
    classes,
    handleSubmit,
    isTOTP,
    isNewTOTP,
    isBasic = true,
    strongPasswordValidation,
    passwordChangeMessage,
    isEnableTOTP,
    withNetworkFields,
    anyTouched,
    invalid,
    complexPass,
    email,
    eulaUrl,
    ssoTypes,
    resetLoginForm,
    match,
    location
  }: Props & DecoratedFormProps & RouteComponentProps<{ token: string }>) {

  const savedTFAState = useRef(null);
  const token = useRef('');
  const submitRef = useRef(null);
  
  const [openCredits, setOpenCredits] = useState(false);
  const [eulaAccess, setEulaAccess] = useState(false);
  const [isInviteForm, setSsInviteForm] = useState(false);
  
  useEffect(() => {
    getSSO();
    const params: any = new URLSearchParams(location.search);
    const prefilled: any = {};
    prefilled.user = params.get("user");
    prefilled.password = params.get("password");
    prefilled.host = params.get("host");
    prefilled.port = params.get("port");

    // temporary, until web implementation is not full
    if (params.get("updatePassword") === "true") {
      isComplexPassRequired();
      setLoginState({
        isNewPassword: true,
        isUpdatePassword: true
      });
    }

    dispatch(
      initialize(FORM_NAME, {
        authCodeDigits: Array.of("", "", "", "", "", ""),
        ...prefilled
      })
    );

    if (prefilled.user != null && prefilled.password != null) {
      dispatch(touch(FORM_NAME, "user"));
    }
    
    const isInvite = match?.path === "/invite/:token";

    setSsInviteForm(isInvite);

    if (isInvite) {
      token.current = match.params.token;
      if (token.current) dispatch(getEmailByToken(token.current));
    }
    
    return () => {
      setLoginState({ isBasic: true });
    };
  }, []);
  
  useEffect(() => {
    if (asyncValidating && passwordComplexity) {
      const { score, feedback  } = passwordComplexity;
      if (score >= PASSWORD_PASS_SCORE) {
        resolveAsyncValidate("");
        return;
      }
      rejectAsyncValidate(feedback);
    }
  }, [passwordComplexity]);
  
  const kickOutWithSSO = (type: string) => {
    dispatch(postKickOutSsoAuthenticationRequest(type));
  };

  const onSubmit = values => {
    if (submittingSSOType && isKickOut) {
      kickOutWithSSO(submittingSSOType);
      return;
    }

    if (isInviteForm) {
      createPasswordRequest(token.current, strongPasswordValidation ? values.newPasswordAsync : values.newPassword);
      return;
    }

    if (isUpdatePassword && isNewPassword) {
      updatePasswordRequest(strongPasswordValidation ? values.newPasswordAsync : values.newPassword);
      return;
    }

    const totpUrlObj = totpUrl && new URL(totpUrl);
    const totpUrlParams = totpUrlObj && new URLSearchParams(totpUrlObj.search);

    postLoginRequest(
      {
        login: values.user,
        password: values.password,
        newPassword: values.newPassword || values.newPasswordAsync,
        kickOut: isKickOut || false,
        token: values.authCodeDigits ? values.authCodeDigits.join("") : "",
        secretCode: totpUrlParams && totpUrlParams.get("secret"),
        skipTfa: isOptionalTOTP,
        eulaAccess
      },
      values.host,
      values.port
    );
  };

  const autocompleteHost = (value, prev) => {
    if (value.match(/^(?!cloud\b)\b\D\w+\.$/) && (!prev || prev.length < value.length)) {
      setTimeout(() => {
        dispatch(change(FORM_NAME, "host", value + "cloud.oncourse.cc"));
        dispatch(change(FORM_NAME, "port", 443));
      });
    }
  };

  const toggleCredits = () => {
    setOpenCredits(prev => !prev);
  };

  return (
    <Form onSubmit={handleSubmit(onSubmit)}>
      <Grid container columnSpacing={3} alignItems="center">
        <Grid item xs={1} md={6} />
        <Grid item xs={12} md={6} className={classes.loginFormRight}>
          <Slide direction="right" in timeout={300}>
            <span className={classes.sideImageWrapper} />
          </Slide>
          <Slide direction="left" in timeout={300}>
            <div className={classes.loginFormWrapper}>
              <Grid
                container
                alignItems="center"
                alignContent="space-between"
              >
                <Grid item xs={12}>
                  <Grid container columnSpacing={3} alignItems="center">
                    <Grid item xs={12} sm={9}>
                      <div className={classes.logoWrapper}>
                        <Logo />
                      </div>
                    </Grid>
                    <Grid item xs={12} sm={3} className={classes.versionText}>
                      <Typography
                        variant="body1"
                        component="span"
                        className="cursor-pointer linkDecoration"
                        onClick={() => window.open(`https://ishoncourse.readme.io/changelog/release-${String(process.env.RELEASE_VERSION).match(/([0-9]+)\.?/)[0]}`, "_blank")}
                      >
                        {$t('version', [process.env.RELEASE_VERSION])}
                      </Typography>
                    </Grid>
                  </Grid>
                </Grid>
                <div className="flex-fill" />
                <Grid item xs={12}>
                  <Collapse in={!openCredits} timeout="auto" unmountOnExit>
                    <div className={classes.loginModalWrapper}>
                      {isTOTP && (
                        <>
                          <div className={classes.textWrapper}>
                            {isNewTOTP
                              ? "Enter the six digit code that is displayed on your TOTP software to enable two factor authentication."
                              : "Enter your authentication code."}
                          </div>

                          <div className={classes.authCodefield}>
                            <FieldArray
                              name="authCodeDigits"
                              component={AuthCodeFieldRenderer}
                              dispatch={dispatch}
                              submitRef={submitRef.current}
                            />
                          </div>
                        </>
                      )}

                      {isBasic && !isInviteForm && (
                        <>
                          <div className={classes.textFieldWrapper}>
                            <Field
                              name="user"
                              placeholder={$t('email2')}
                              autoComplete="user-name"
                              component={FormTextField}
                              validate={validateSingleMandatoryField}
                                                            />
                          </div>

                          <div className={classes.textFieldWrapper}>
                            <Field
                              name="password"
                              type="password"
                              autoComplete="current-password"
                              placeholder={$t('password')}
                              component={FormTextField}
                              validate={validateSingleMandatoryField}
                                                            />
                          </div>

                          {withNetworkFields && (
                            <div className="d-flex">
                              <Field
                                name="host"
                                placeholder={$t('host')}
                                autoComplete="host"
                                className="flex-fill"
                                component={FormTextField}
                                validate={validateSingleMandatoryField}
                                onChange={(e, v, prev) => autocompleteHost(v, prev)}
                              />

                              <Field
                                name="port"
                                placeholder={$t('port')}
                                autoComplete="port"
                                className={classes.portField}
                                component={FormTextField}
                                validate={validateSingleMandatoryField}
                              />
                            </div>
                          )}
                        </>
                      )}

                      {isNewPassword && (
                        <>
                          {!isUpdatePassword && (
                            <div className={classes.textWrapper}>
                              {passwordChangeMessage
                                || "Your existing password does not meet minimum complexity requirements."}
                            </div>
                          )}

                          <div className={classes.textWrapper}>{$t('please_create_a_new_password_below')}</div>

                          <div className={classes.textFieldWrapper}>
                            {strongPasswordValidation || complexPass === "true" ? (
                              <Field
                                name="newPasswordAsync"
                                type="password"
                                autoComplete="new-password"
                                placeholder={$t('new_password')}
                                component={NewPasswordField}
                                passwordScore={passwordComplexity?.score}
                              />
                            ) : (
                              <Field
                                name="newPassword"
                                type="password"
                                autoComplete="new-password"
                                placeholder={$t('new_password')}
                                component={FormTextField}
                                validate={validatePasswordStrengthLight}
                                                                />
                            )}
                          </div>

                          <div className={classes.textFieldWrapper}>
                            <Field
                              name="newPasswordConfirm"
                              type="password"
                              autoComplete="off"
                              placeholder={$t('confirm_new_password')}
                              component={FormTextField}
                              validate={validatePasswordConfirm}
                                                            />
                          </div>
                        </>
                      )}

                      {isInviteForm && (
                        <>
                          <div className={classes.textWrapper}>
                            {email
                              ? `You have been invited to ish onCourse. Please create a password. Your username is ${email}.`
                              : "User not found."}
                          </div>

                          <div className={classes.textFieldWrapper}>
                            {strongPasswordValidation || complexPass === "true" ? (
                              <Field
                                name="newPasswordAsync"
                                type="password"
                                autoComplete="new-password"
                                placeholder={$t('new_password')}
                                component={NewPasswordField}
                                passwordScore={passwordComplexity.score}
                                helperText={passwordComplexity.feedback}
                              />
                            ) : (
                              <Field
                                name="newPassword"
                                type="password"
                                autoComplete="new-password"
                                placeholder={$t('password')}
                                component={FormTextField}
                                validate={validatePasswordStrengthLight}
                                                                />
                            )}
                          </div>

                          <div className={classes.textFieldWrapper}>
                            <Field
                              name="passwordConfirm"
                              type="password"
                              autoComplete="off"
                              placeholder={$t('confirm_password')}
                              component={FormTextField}
                              validate={validatePasswordConfirm}
                                                            />
                          </div>
                        </>
                      )}

                      {(isEnableTOTP || isOptionalTOTP) && (
                        <Grid container columnSpacing={3} alignItems="flex-start" direction="row-reverse" spacing={3}>
                          <Grid item xs={12} sm={4}>
                            {totpUrl && <QRCode className={classes.code} size={106} value={totpUrl} />}
                          </Grid>
                          <Grid item xs={12} sm={8}>
                            <div>
                              <div className={classes.textWrapper}>
                                {$t('twofactor_authentication_is_an_extra_layer_of_secu')}
                              </div>

                              <div className={classes.textWrapper}>
                                {$t('install_totp_software_on_your_phone_and_scan_the_q')}
                              </div>

                              <div className={classes.textWrapper}>
                                {$t('Download')}:
                                <a
                                  className={classes.extLink}
                                  target="_blank"
                                  href="https://play.google.com/store/apps/details?id=com.google.android.apps.authenticator2&hl=en"
                                  rel="noreferrer"
                                >
                                  {$t('Android')}
                                </a>
                                <a
                                  className={classes.extLink}
                                  target="_blank"
                                  href="https://itunes.apple.com/us/app/google-authenticator/id388497605?mt=8"
                                  rel="noreferrer"
                                >
                                  {$t('iPhone')}
                                </a>
                              </div>
                            </div>
                          </Grid>
                        </Grid>
                      )}

                      {isKickOut && (
                        <>
                          <div className={classes.textWrapper}>{$t('you_are_currently_logged_in_from_another_session')}</div>

                          <div className={classes.textWrapper}>
                            {$t('you_can_kick_out_the_other_session_without_giving')}
                          </div>
                        </>
                      )}

                      <div className="flex-fill" />

                      <div className={classes.buttonsContainer}>
                        {!isInviteForm && (isKickOut || isEnableTOTP || isTOTP || isNewPassword || isNewTOTP || isOptionalTOTP || isUpdatePassword) && (
                          <Button
                            disabled={fetch.pending}
                            type={isOptionalTOTP ? "submit" : "button"}
                            classes={{
                              root: classes.declineButton
                            }}
                            onClick={
                              isTOTP && isNewTOTP
                                ? e => {
                                  e.preventDefault();
                                  dispatch(change(FORM_NAME, "authCodeDigits", Array.of("", "", "", "", "", "")));
                                  setLoginState(savedTFAState.current);
                                }
                                : isOptionalTOTP ? undefined : resetLoginForm
                            }
                          >
                            {isOptionalTOTP ? "Maybe Later" : "Cancel"}
                          </Button>
                        )}

                        <LoadingButton
                          loading={fetch.pending}
                          ref={submitRef}
                          type="submit"
                          disabled={(!isKickOut && !anyTouched) || invalid || asyncValidating || (isInviteForm && !email)}
                          classes={{
                            root: classes.loginButton,
                            disabled: classes.loginButtonDisabled
                          }}
                          onClick={
                            isEnableTOTP || isOptionalTOTP ? e => {
                                e.preventDefault();
                                savedTFAState.current = { isEnableTOTP, isOptionalTOTP };
                                setLoginState({ isTOTP: true, isNewTOTP: true });
                                dispatch(change(FORM_NAME, "authCodeDigits", Array.of("", "", "", "", "", "")));
                              }
                              : undefined
                          }
                        >
                          {((isTOTP && !isNewTOTP) || (isBasic && !isInviteForm)) && "Login"}
                          {((isTOTP && isNewTOTP) || isEnableTOTP || isOptionalTOTP) && "Enable"}
                          {isNewPassword && "Confirm"}
                          {isKickOut && "Kick out"}
                          {isInviteForm && "Create password"}
                        </LoadingButton>
                        {eulaUrl && (
                        <EulaDialog
                          eulaUrl={eulaUrl}
                          classes={classes}
                          onCancel={() => setLoginState({
                            eulaUrl: undefined
                          })}
                          onAccept={() => setEulaAccess(true)}
                        />
                        )}
                      </div>
                    </div>
                  </Collapse>
                  <Collapse in={openCredits} timeout="auto" unmountOnExit>
                    <Credits wrapperClass={classes.creditsWrapper} itemClass={classes.creditHeader} />
                  </Collapse>
                </Grid>
                <SSOProviders providers={ssoTypes}/>
                <div className="flex-fill" />
                <Grid container columnSpacing={3} alignItems="center">
                  <div className="flex-fill">
                    <div>
                      <IconButton
                        color="inherit"
                        size="medium"
                        onClick={toggleCredits}
                        className="p-0"
                      >
                        {openCredits ? <ExpandMore fontSize="inherit" /> : <ExpandLess fontSize="inherit" />}
                      </IconButton>
                      <span>{$t('credits')}</span>
                    </div>
                    <div>
                      {$t('2005_ish_group_all_rights_reserved', [new Date().getFullYear().toString()])}
                    </div>
                  </div>
                  <div className={classes.footerIshLogo}>
                    <Logo small />
                  </div>
                </Grid>
              </Grid>
            </div>
          </Slide>
        </Grid>
      </Grid>
    </Form>
  );
}

const mapStateToProps = (state: State) => ({
  ...state.login,
  fetch: state.fetch,
  complexPass: state.preferences && state.preferences.complexPass,
  ssoTypes: state.automation.integration.ssoTypes
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  postLoginRequest: (body: LoginRequest, host?: string, port?: number) =>
    dispatch(postLoginRequest(body, host, port)),
  getSSO: () => dispatch(getSsoIntegrations()),
  updatePasswordRequest: (value: string) => dispatch(updatePasswordRequest(value)),
  setLoginState: (value: LoginState) => dispatch(setLoginState(value)),
  isComplexPassRequired: () => dispatch(isComplexPassRequired()),
  getEmailByToken: (token: string) => dispatch(getEmailByToken(token)),
  createPasswordRequest: (token: string, password: string) => dispatch(createPasswordRequest(token, password)),
  resetLoginForm: () => {
    dispatch(setLoginState({
      isBasic: true,
      passwordComplexity: null
    }));
    dispatch(clearFields(FORM_NAME, false, false, 'newPasswordAsync',  'newPasswordConfirm'));
    dispatch(touch(FORM_NAME, 'user'));
  }
});

const shouldAsyncValidate = params => {
  const { syncValidationPasses, trigger } = params;

  return !(syncValidationPasses && trigger === "submit");
};

const LoginPage = reduxForm({
  asyncValidate,
  shouldAsyncValidate,
  form: FORM_NAME,
  touchOnChange: true,
  asyncChangeFields: ["newPasswordAsync"]
})(connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withStyles(LoginPageBase, styles)));

export default LoginPage;