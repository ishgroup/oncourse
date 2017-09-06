import React from 'react';
import {connect, Dispatch} from "react-redux";
import {withRouter} from 'react-router-dom';

import {Container, Row, Col, Button, Form, FormGroup, Label, Input, FormText} from 'reactstrap';
import {submitLoginForm} from "./actions/index";

interface Props {
  onSubmit: (form) => void;
  history?: any;
}

export class Login extends React.Component<Props, any> {
  handleSubmit(e) {
    const {onSubmit} = this.props;
    e.preventDefault();
    onSubmit({
      email: e.target.email.value,
      password: e.target.password.value,
    });
  }

  render() {
    return (
      <Container>
        <Row>
          <Col md="4" className="mx-auto">

            <Form onSubmit={e => this.handleSubmit(e)} className="login-form">

              <FormGroup>
                <Input type="email" name="email" id="loginEmail" placeholder="Email" />
              </FormGroup>

              <FormGroup>
                <Input type="password" name="password" id="loginPassword" placeholder="Password" />
              </FormGroup>

              <Button color="primary">Log in</Button>

            </Form>

          </Col>
        </Row>
      </Container>
    );
  }
}

const mapStateToProps = state => ({ });

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    onSubmit(form) {
      dispatch(submitLoginForm(form));
    },
  };
};

export default withRouter(connect<any, any, any>(mapStateToProps, mapDispatchToProps)(Login));
