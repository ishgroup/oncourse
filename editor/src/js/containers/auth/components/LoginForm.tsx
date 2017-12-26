import React from 'react';
import {Field, reduxForm} from 'redux-form';
import classnames from 'classnames';
import {Container, Row, Col, Button, Form, FormGroup, Label, Input, FormFeedback} from 'reactstrap';

const input = props => {
  const {type, input, placeholder, id, meta} = props;
  const {submitFailed, invalid, error} = meta;
  const showError = submitFailed && invalid && error;

  return (
    <div className={classnames({'form-error': showError})}>
      <Input
        type={type}
        name={input.name}
        id={id}
        placeholder={placeholder}
        onChange={input.onChange}
      />
      {showError && <FormFeedback>{error}</FormFeedback>}
    </div>
  );
};

const validate = values => {
  const errors = {}
  if (!values.email) {
    errors['email'] = 'Login field is required';
  }

  if (!values.password) {
    errors['password'] = 'Password field is required';
  }

  return errors;
};

class LoginForm extends React.Component<any, any> {

  render() {
    const {handleSubmit, pristine, submitting} = this.props;

    return (
      <Form onSubmit={handleSubmit} className="login-form">

        <FormGroup>
          <Field
            id="loginEmail"
            name="email"
            type="text"
            placeholder="Email"
            component={input}
          />
        </FormGroup>

        <FormGroup>
          <Field
            id="loginPassword"
            type="password"
            name="password"
            placeholder="Password"
            component={input}
          />
        </FormGroup>

        <Button disabled={pristine || submitting} color="primary">Log in</Button>

      </Form>
    );
  }
}

export default reduxForm({
  validate,
  form: 'login',
})(LoginForm);
