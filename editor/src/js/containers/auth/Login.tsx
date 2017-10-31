import React from 'react';
import {connect, Dispatch} from "react-redux";
import {withRouter} from 'react-router-dom';

import {Container, Row, Col, Button, Form, FormGroup, Label, Input, FormText} from 'reactstrap';
import {submitLoginForm} from "./actions";
import {DefaultConfig} from "../../constants/Config";
import {CookieService} from "../../services/CookieService";
import LoginForm from "./components/LoginForm";

interface Props {
  onSubmit: (form) => void;
  history?: any;
}

export class Login extends React.Component<Props, any> {
  componentDidMount() {
    CookieService.delete(DefaultConfig.COOKIE_NAME);
  }

  render() {
    const {onSubmit} = this.props;

    return (
      <Container>
        <Row>
          <Col md="4" className="mx-auto">
            <LoginForm onSubmit={onSubmit}/>
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
