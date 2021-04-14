import * as  React from 'react';
import {Form, Field, reduxForm} from 'redux-form';
import clsx from "clsx";
import {withStyles} from "@material-ui/core/styles";
import {TextField} from "@material-ui/core";
import {darken, fade} from "@material-ui/core/styles/colorManipulator";
import BrowserWarning from "../../../common/components/BrowserWarning";
import CustomButton from "../../../common/components/CustomButton";

const styles: any = theme => ({
  loginFormContainer: {
    marginTop: "200px",
    borderRadius: "5px",
    padding: "40px 30px 15px 30px",
    background: theme.palette.background.paper,
    width: "300px",
    height: "200px",
  },
  loginButton: {
    marginLeft: theme.spacing(2),
  },
  loginButtonDisabled: {
    backgroundColor: fade(theme.palette.primary.main, 0.5),
  },
  input: {
    width: "100%",
    marginBottom: "20px",
  }
});

const input = props => {
  const {className, type, input, placeholder, id, meta} = props;
  const {submitFailed, invalid, error} = meta;
  const showError = submitFailed && invalid && error;

  return (
    <div className={clsx(showError && 'form-error')}>
      <TextField
        type={type}
        name={input.name}
        className={className}
        error={showError}
        helperText={showError && error}
        id={id}
        placeholder={placeholder}
        onChange={input.onChange}
        fullWidth
      />
    </div>
  );
};

const validate = values => {
  const errors = {};
  if (!values.email) {
    errors['email'] = 'Login field is required';
  }

  if (!values.password) {
    errors['password'] = 'Password field is required';
  }

  return errors;
};


class LoginForm extends React.Component<any, any> {
  cancelApplication = () => {
    document.cookie = "";
    window.location.href = "";
  }

  form() {
    const {classes, handleSubmit, pristine, submitting} = this.props;

    return (
      <div>
        <BrowserWarning />
          <Form onSubmit={handleSubmit} className={classes.loginFormContainer}>
            <div>
              <Field
                id="loginEmail"
                name="email"
                type="text"
                placeholder="Email"
                className={classes.input}
                component={input}
              />
            </div>

            <Field
              id="loginPassword"
              type="password"
              name="password"
              placeholder="Password"
              className={classes.input}
              component={input}
            />
            <div className={"w-100 d-flex justify-content-end"}>
              <CustomButton
                styleType="cancel"
                disabled={submitting}
                onClick={this.cancelApplication}
              >
                Cancel
              </CustomButton>
              <CustomButton
                styleType="submit"
                type="submit"
                disabled={pristine || submitting}
                styles={classes.loginButton}
              >
                Login
              </CustomButton>
            </div>
        </Form>
      </div>
    );
  }

  render() {
    return this.form();
  }
}

export default reduxForm<any, any>({
  validate,
  form: 'login',
})(withStyles(styles)(LoginForm) as any);
