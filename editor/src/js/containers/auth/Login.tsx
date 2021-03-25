import React from 'react';
import {connect} from "react-redux";
import {Dispatch} from "redux";
import {withRouter} from 'react-router-dom';
import {Container, Row, Col} from 'reactstrap';
import classnames from "classnames";
import {submitLoginForm} from "./actions";
import {DefaultConfig} from "../../constants/Config";
import {CookieService} from "../../services/CookieService";
import LoginForm from "./components/LoginForm";

interface Props {
  onSubmit: (form) => void;
  history?: any;
  fetching: boolean;
}

export class Login extends React.Component<Props, any> {
  componentDidMount() {
    CookieService.delete(DefaultConfig.COOKIE_NAME);
  }

  render() {
    const {onSubmit, fetching} = this.props;

    return (
      <Container>
        <Row>
          <Col md="4" className="mx-auto">
            <div className={classnames("login-wrapper", {fetching})}>
              <LoginForm onSubmit={onSubmit}/>
            </div>
          </Col>
        </Row>
      </Container>
    );
  }
}

const mapStateToProps = state => ({
  fetching: state.fetching,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    onSubmit(form) {
      dispatch(submitLoginForm(form));
    },
  };
};

export default withRouter(connect<any, any, any>(mapStateToProps, mapDispatchToProps)(Login as any));
