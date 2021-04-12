import React from 'react';
import {connect} from "react-redux";
import {Dispatch} from "redux";
import {withRouter} from 'react-router-dom';
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
      <div className={classnames({fetching}, "d-flex align-items-center justify-content-center relative")}>
        <LoginForm onSubmit={onSubmit}/>
      </div>
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
