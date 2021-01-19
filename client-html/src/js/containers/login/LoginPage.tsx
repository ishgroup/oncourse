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

import * as React from "react";
import { withStyles } from "@material-ui/core/styles";
import { connect } from "react-redux";
import { Dispatch, Action } from "redux";
import QRCode from "qrcode.react";
import {
 Field, FieldArray, reduxForm, initialize, change, touch
} from "redux-form";
import Slide from "@material-ui/core/Slide";
import Button from "@material-ui/core/Button";
import Grid from "@material-ui/core/Grid";
import Typography from "@material-ui/core/Typography";
import { LoginRequest } from "@api/model";
import { darken, fade } from "@material-ui/core/styles/colorManipulator";
import Collapse from "@material-ui/core/Collapse";
import ExpandLess from "@material-ui/icons/ExpandLess";
import ExpandMore from "@material-ui/icons/ExpandMore";
import IconButton from "@material-ui/core/IconButton";
import AuthCodeFieldRenderer from "./components/AuthCodeFieldRenderer";
import NewPasswordField from "./components/NewPasswordField";
import { FormTextField } from "../../common/components/form/form-fields/TextField";
import { validateSingleMandatoryField } from "../../common/utils/validation";
import {
  postLoginRequest, setLoginState, updatePasswordRequest, checkPassword, getEmailByToken, createPasswordRequest
} from "../../common/actions";
import { isComplexPassRequired } from "../preferences/actions";
import { State } from "../../reducers/state";
import { LoginState } from "./reducers/state";
import onCourseLogoDark from "../../../images/onCourseLogoDark.png";
import ishLogoSmall from "../../../images/logo_small.png";

const styles: any = theme => ({
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
    minHeight: "61px"
  },
  extLink: {
    marginLeft: theme.spacing(2),
    color: "#5362b1"
  },
  portField: {
    width: theme.spacing(10),
    marginLeft: theme.spacing(3)
  },
  flexDivider: {
    flex: 1
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
    backgroundColor: fade(theme.palette.primary.main, 0.5)
  },
  sideImageWrapper: {
    position: "absolute",
    width: "100%",
    height: "100%",
    top: 0,
    left: "-100%",
    overflow: "hidden",
    display: "none",
    [theme.breakpoints.up("md")]: {
      display: "block"
    },
    "& > img": {
      position: "relative",
      maxWidth: "100%",
      minWidth: "100%",
      minHeight: "100%",
      top: "50%",
      left: "50%",
      transform: "translateX(-50%) translateY(-50%)",
      objectFit: "cover"
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
    "& > div": {
      textAlign: "center",
      "& > div > div > input": {
        padding: "9px 13px",
        [theme.breakpoints.up("sm")]: {
          padding: "18px 22px"
        },
        [theme.breakpoints.up("md")]: {
          padding: "15px 19px"
        }
      }
    }
  }
});

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

interface Props extends LoginState {
  classes: any;
  handleSubmit: any;
  anyTouched: boolean;
  invalid: boolean;
  asyncValidating: boolean;
  complexPass: any;
  totpUrl: string;
  postLoginRequest: (body: LoginRequest, host?: string, port?: number) => void;
  updatePasswordRequest: (value: string) => void;
  setLoginState: (value: LoginState) => void;
  isComplexPassRequired: () => void;
  submit: () => void;
  dispatch: (action: Action) => void;
  getEmailByToken: (value: string) => void;
  createPasswordRequest: (token: string, password: string) => void;
  email?: string;
}

export class LoginPageBase extends React.PureComponent<Props, any> {
  private savedTFAState;

  private isInviteForm: boolean = false;

  private token: string = '';

  state = { passwordScore: 0, passwordFeedback: "", openCredits: false };

  constructor(props) {
    super(props);

    const prefilled: any = {};

    if (window.location.search) {
      const params: any = new URLSearchParams(window.location.search);
      prefilled.user = params.get("user");
      prefilled.password = params.get("password");
      prefilled.host = params.get("host");
      prefilled.port = params.get("port");

      // temporary, until web implementation is not full
      if (params.get("updatePassword") === "true") {
        this.props.isComplexPassRequired();
        props.setLoginState({
          isNewPassword: true,
          isUpdatePassword: true
        });
      }
    }

    props.dispatch(
      initialize("LoginForm", {
        authCodeDigits: Array.of("", "", "", "", "", ""),
        ...prefilled
      })
    );

    if (prefilled.user != null && prefilled.password != null) {
      props.dispatch(touch("LoginForm", "user"));
    }

    this.isInviteForm = window.location.pathname.includes('invite');
  }

  componentDidMount() {
    if (this.isInviteForm) {
      this.token = window.location.pathname.match(/(\w+)$/g)[0];
      this.token && this.props.getEmailByToken(this.token);
    }
  }

  componentWillReceiveProps(nextProps) {
    if (nextProps.passwordComplexity && nextProps.asyncValidating) {
      const {
        passwordComplexity: { score, feedback }
      } = nextProps;
      const { passwordScore, passwordFeedback } = this.state;

      if (passwordScore !== score) {
        this.setState({
          passwordScore: score
        });
      }

      if (passwordFeedback !== feedback) {
        this.setState({
          passwordFeedback: feedback
        });
      }

      if (score >= 2) {
        this.setState({
          passwordFeedback: ""
        });
        resolveAsyncValidate();

        return;
      }

      rejectAsyncValidate(feedback);
    }
  }

  componentWillUnmount() {
    this.props.setLoginState({ isBasic: true });
  }

  onSubmit = values => {
    const {
      postLoginRequest,
      updatePasswordRequest,
      isUpdatePassword,
      isNewPassword,
      isKickOut,
      totpUrl,
      isOptionalTOTP,
      createPasswordRequest,
    } = this.props;

    if (this.isInviteForm) {
      createPasswordRequest(this.token, values.newPassword);
      return;
    }

    if (isUpdatePassword && isNewPassword) {
      updatePasswordRequest(values.newPassword || values.newPasswordAsync);
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
        skipTfa: isOptionalTOTP
      },
      values.host,
      values.port
    );
  };

  validatePasswordStrengthLight = (value, values) => {
    if (!value) {
      return "Enter your new password";
    }

    if (value && values.user && value.trim() === values.user.trim()) {
      return "You must enter password which is different to login";
    }

    return value && value.trim().length < 5 ? "Password minimum length is 5 symbols" : undefined;
  };

  autocompleteHost = (value, prev) => {
    if (value.match(/^(?!cloud\b)\b\D\w+\.$/) && (!prev || prev.length < value.length)) {
      setTimeout(() => {
        this.props.dispatch(change("LoginForm", "host", value + "cloud.oncourse.cc"));
        this.props.dispatch(change("LoginForm", "port", 443));
      });
    }
  };

  toggleCredits = () => {
    const { openCredits } = this.state;
    this.setState({ openCredits: !openCredits });
  };

  getCreditsItem = (heading, creditPersons) => {
    const { classes } = this.props;
    return (
      <div className="mb-3">
        <Typography variant="body1" className={classes.creditHeader}>
          {heading}
        </Typography>
        {creditPersons.map((person, i) => (
          <Typography key={i} variant="body2">
            {person}
          </Typography>
        ))}
      </div>
    );
  };

  render() {
    const {
      classes,
      handleSubmit,
      isTOTP,
      isNewTOTP,
      isBasic = true,
      isNewPassword,
      isUpdatePassword,
      strongPasswordValidation,
      passwordChangeMessage,
      isEnableTOTP,
      isOptionalTOTP,
      isKickOut,
      withNetworkFields,
      anyTouched,
      totpUrl,
      invalid,
      setLoginState,
      asyncValidating,
      complexPass,
      dispatch,
      email
    } = this.props;

    const { passwordScore, passwordFeedback, openCredits } = this.state;

    return (
      <form onSubmit={handleSubmit(this.onSubmit)}>
        <Grid container alignItems="center" justify="center">
          <Grid item xs={1} md={6} />
          <Grid item xs={12} md={6} className={classes.loginFormRight}>
            <Slide direction="right" in timeout={300}>
              <span className={classes.sideImageWrapper}>
                <img src="https://www.ish.com.au/assets/onCourse/splash.jpg" alt="" />
              </span>
            </Slide>
            <Slide direction="left" in timeout={300}>
              <div className={classes.loginFormWrapper}>
                <Grid
                  container
                  className="mb-2"
                  alignItems="center"
                  justify="space-between"
                  alignContent="space-between"
                >
                  <Grid item xs={12} className="mb-2">
                    <Grid container alignItems="center">
                      <Grid item xs={12} sm={9}>
                        <div className={classes.logoWrapper}>
                          <img src={onCourseLogoDark} height={55} draggable={false} alt="Logo" />
                        </div>
                      </Grid>
                      <Grid item xs={12} sm={3} className={classes.versionText}>
                        <Typography
                          variant="body1"
                          component="span"
                          className="cursor-pointer linkDecoration"
                          onClick={() => window.open("https://www.ish.com.au/s/onCourse/doc/release-notes/", "_blank")}
                        >
                          Version
                          {' '}
                          {process.env.RELEASE_VERSION}
                        </Typography>
                      </Grid>
                    </Grid>
                  </Grid>
                  <div className={classes.flexDivider} />
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
                              <FieldArray name="authCodeDigits" component={AuthCodeFieldRenderer as any} dispatch={dispatch} />
                            </div>
                          </>
                        )}

                        {isBasic && !this.isInviteForm && (
                          <>
                            <div className={classes.textFieldWrapper}>
                              <Field
                                name="user"
                                placeholder="Email"
                                autoComplete="user-name"
                                component={FormTextField}
                                validate={validateSingleMandatoryField}
                                fullWidth
                              />
                            </div>

                            <div className={classes.textFieldWrapper}>
                              <Field
                                name="password"
                                type="password"
                                autoComplete="current-password"
                                placeholder="Password"
                                component={FormTextField}
                                validate={validateSingleMandatoryField}
                                fullWidth
                              />
                            </div>

                            {withNetworkFields && (
                              <div className="d-flex">
                                <Field
                                  name="host"
                                  placeholder="Host"
                                  autoComplete="host"
                                  className={classes.flexDivider}
                                  component={FormTextField}
                                  validate={validateSingleMandatoryField}
                                  onChange={(e, v, prev) => this.autocompleteHost(v, prev)}
                                />

                                <Field
                                  name="port"
                                  placeholder="Port"
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

                            <div className={classes.textWrapper}>Please create a new password below.</div>

                            <div className={classes.textFieldWrapper}>
                              {strongPasswordValidation || complexPass === "true" ? (
                                <Field
                                  name="newPasswordAsync"
                                  type="password"
                                  autoComplete="new-password"
                                  placeholder="New password"
                                  component={NewPasswordField}
                                  passwordScore={passwordScore}
                                  helperText={passwordFeedback}
                                  fullWidth
                                />
                              ) : (
                                <Field
                                  name="newPassword"
                                  type="password"
                                  autoComplete="new-password"
                                  placeholder="New password"
                                  component={FormTextField}
                                  validate={this.validatePasswordStrengthLight}
                                  fullWidth
                                />
                              )}
                            </div>

                            <div className={classes.textFieldWrapper}>
                              <Field
                                name="newPasswordConfirm"
                                type="password"
                                autoComplete="off"
                                placeholder="Confirm new password"
                                component={FormTextField}
                                validate={validatePasswordConfirm}
                                fullWidth
                              />
                            </div>
                          </>
                        )}

                        {this.isInviteForm && (
                          <>
                            <div className={classes.textWrapper}>
                              {email
                                ? "You have been invited to ish onCourse. Please create a password. Your username is {email}."
                                : "User not found."}
                            </div>

                            <div className={classes.textFieldWrapper}>
                              {strongPasswordValidation || complexPass === "true" ? (
                                <Field
                                  name="newPasswordAsync"
                                  type="password"
                                  autoComplete="new-password"
                                  placeholder="New password"
                                  component={NewPasswordField}
                                  passwordScore={passwordScore}
                                  helperText={passwordFeedback}
                                  fullWidth
                                />
                              ) : (
                                <Field
                                  name="newPassword"
                                  type="password"
                                  autoComplete="new-password"
                                  placeholder="Password"
                                  component={FormTextField}
                                  validate={this.validatePasswordStrengthLight}
                                  fullWidth
                                />
                              )}
                            </div>

                            <div className={classes.textFieldWrapper}>
                              <Field
                                name="passwordConfirm"
                                type="password"
                                autoComplete="off"
                                placeholder="Confirm password"
                                component={FormTextField}
                                validate={validatePasswordConfirm}
                                fullWidth
                              />
                            </div>
                          </>
                        )}

                        {(isEnableTOTP || isOptionalTOTP) && (
                          <Grid container alignItems="flex-start" direction="row-reverse" spacing={3}>
                            <Grid item xs={12} sm={4}>
                              {totpUrl && <QRCode className={classes.code} size={106} value={totpUrl} />}
                            </Grid>
                            <Grid item xs={12} sm={8}>
                              <div>
                                <div className={classes.textWrapper}>
                                  Two-factor authentication is an extra layer of security for your onCourse application.
                                </div>

                                <div className={classes.textWrapper}>
                                  Install TOTP software on your phone and scan the QR code before pressing 'enable'.
                                </div>

                                <div className={classes.textWrapper}>
                                  Download:
                                  <a
                                    className={classes.extLink}
                                    target="_blank"
                                    href="https://play.google.com/store/apps/details?id=com.google.android.apps.authenticator2&hl=en"
                                  >
                                    Android
                                  </a>
                                  <a
                                    className={classes.extLink}
                                    target="_blank"
                                    href="https://itunes.apple.com/us/app/google-authenticator/id388497605?mt=8"
                                  >
                                    iPhone
                                  </a>
                                </div>
                              </div>
                            </Grid>
                          </Grid>
                        )}

                        {isKickOut && (
                          <>
                            <div className={classes.textWrapper}>You are currently logged in from another session.</div>

                            <div className={classes.textWrapper}>
                              You can kick out the other session without giving that user time to save their work.
                            </div>
                          </>
                        )}

                        <div className={classes.flexDivider} />

                        <div className={classes.buttonsContainer}>
                          {!this.isInviteForm && (
                            <>
                              <a href="Quit" className={classes.link} draggable={false} tabIndex={-1}>
                                <Button
                                  type={isOptionalTOTP ? "submit" : "button"}
                                  classes={{
                                    root: classes.declineButton
                                  }}
                                  onClick={
                                    isTOTP && isNewTOTP
                                      ? e => {
                                          e.preventDefault();
                                          dispatch(change("LoginForm", "authCodeDigits", Array.of("", "", "", "", "", "")));
                                          setLoginState(this.savedTFAState);
                                        }
                                      : undefined
                                  }
                                >
                                  {(isBasic
                                    || (isNewPassword && !isUpdatePassword)
                                    || isKickOut
                                    || isEnableTOTP
                                    || (isTOTP && !isNewTOTP))
                                    && "Quit"}
                                  {((isTOTP && isNewTOTP) || (isNewPassword && isUpdatePassword)) && "Cancel"}
                                  {isOptionalTOTP && "Maybe Later"}
                                </Button>
                              </a>
                            </>
                          )}

                          <Button
                            type="submit"
                            disabled={!anyTouched || invalid || asyncValidating || (this.isInviteForm && !email)}
                            classes={{
                              root: classes.loginButton,
                              disabled: classes.loginButtonDisabled
                            }}
                            onClick={
                              isEnableTOTP || isOptionalTOTP
                                ? e => {
                                    e.preventDefault();
                                    this.savedTFAState = {
                                      isEnableTOTP,
                                      isOptionalTOTP
                                    };
                                    setLoginState({
                                      isTOTP: true,
                                      isNewTOTP: true
                                    });
                                  }
                                : undefined
                            }
                          >
                            {((isTOTP && !isNewTOTP) || (isBasic && !this.isInviteForm)) && "Login"}
                            {((isTOTP && isNewTOTP) || isEnableTOTP || isOptionalTOTP) && "Enable"}
                            {isNewPassword && "Confirm"}
                            {isKickOut && "Kick out"}
                            {this.isInviteForm && "Create password"}
                          </Button>
                        </div>
                      </div>
                    </Collapse>
                    <Collapse in={openCredits} timeout="auto" unmountOnExit>
                      <Grid container className={classes.creditsWrapper}>
                        <Grid item xs={12} sm={6}>
                          <div className="">
                            {this.getCreditsItem("Product design", ["Aristedes Maniatis", "Natalie Morton"])}
                            {this.getCreditsItem("System architecture", ["Aristedes Maniatis"])}
                            {this.getCreditsItem("Engineering leads", [
                              "Artyom Kravchenko",
                              "Andrey Koyro",
                              "Anton Sakalouski",
                              "Lachlan Deck",
                              "Marek Wawrzyczny"
                            ])}
                            {this.getCreditsItem("Software engineering", [
                              "Dzmitry Kazimirchyk",
                              "Andrey Narut",
                              "Viacheslav Davidovich",
                              "Xenia Khailenka",
                              "Olga Tkachova",
                              "Marcin Skladaniec",
                              "Nikita Timofeev",
                              "Arseni Bulatski",
                              "Rostislav Zenov",
                              "Maxim Petrusevich",
                              "Alexandr Petkov",
                              "Artyom Kochetkov",
                              "Pavel Nikanovich",
                              "Andrey Davidovich",
                              "Victor Yarmolovich",
                              "Yury Yasuchenya"
                            ])}
                          </div>
                        </Grid>
                        <Grid item xs={12} sm={6}>
                          {this.getCreditsItem("Quality assurance", [
                            "George Filipovich",
                            "Yury Harachka",
                            "Aliaksei Haiduchonak",
                            "Rex Chan"
                          ])}
                          {this.getCreditsItem("Icon design", ["Bruce Martin"])}
                          {this.getCreditsItem("UI development", ["Chintan Kotadia"])}
                          {this.getCreditsItem("Additional programming", [
                            "Matthias Moeser",
                            "Abdul Abdul-Latif",
                            "Mosleh Uddin",
                            "Savva Kolbachev",
                            "Jackson Mills",
                            "Ruslan Ibragimov",
                            "Sasha Shestak"
                          ])}
                          {this.getCreditsItem("Documentation", [
                            "Stephen McIlwaine",
                            "Charlotte Tanner",
                            "James Swinbanks"
                          ])}
                        </Grid>
                      </Grid>
                    </Collapse>
                  </Grid>
                  <div className={classes.flexDivider} />
                  <Grid container alignItems="center" className="mt-3">
                    <div className="flex-fill">
                      <div>
                        <IconButton
                          color="inherit"
                          size="medium"
                          onClick={this.toggleCredits.bind(this)}
                          className="p-0"
                        >
                          {openCredits ? <ExpandMore fontSize="inherit" /> : <ExpandLess fontSize="inherit" />}
                        </IconButton>
                        <span>Credits</span>
                      </div>
                      <div>
                        @2005-
                        {new Date().getFullYear()}
                        {' '}
                        ish group. All rights reserved.
                      </div>
                    </div>
                    <div>
                      <img src={ishLogoSmall} className={classes.footerIshLogo} alt="" />
                    </div>
                  </Grid>
                </Grid>
              </div>
            </Slide>
          </Grid>
        </Grid>
      </form>
    );
  }
}

const mapStateToProps = (state: State) => ({
  ...state.login,
  complexPass: state.preferences && state.preferences.complexPass
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
    postLoginRequest: (body: LoginRequest, host?: string, port?: number) =>
      dispatch(postLoginRequest(body, host, port)),
    updatePasswordRequest: (value: string) => dispatch(updatePasswordRequest(value)),
    setLoginState: (value: LoginState) => dispatch(setLoginState(value)),
    isComplexPassRequired: () => dispatch(isComplexPassRequired()),
    getEmailByToken: (token: string) => dispatch(getEmailByToken(token)),
    createPasswordRequest: (token: string, password: string) => dispatch(createPasswordRequest(token, password)),
  });

const shouldAsyncValidate = params => {
  const { syncValidationPasses, trigger } = params;

  return !(syncValidationPasses && trigger === "submit");
};

const LoginPage = reduxForm({
  asyncValidate,
  shouldAsyncValidate,
  form: "LoginForm",
  touchOnChange: true,
  asyncChangeFields: ["newPasswordAsync"]
})(connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withStyles(styles)(LoginPageBase)));

export default LoginPage;
