import React from 'react';
import {connect, Dispatch} from "react-redux";
import {Route, NavLink, Redirect, withRouter} from 'react-router-dom';
import Notifications from 'react-notification-system-redux';
import classnames from 'classnames';

import {Layout} from '../components/Layout/Layout';
import {Sidebar} from '../components/Layout/Sidebar';
import {Content} from '../components/Layout/Content';
import {getHistoryInstance, setHistoryInstance} from "../history";
import {URL} from "../routes";
import {logout} from "./auth/actions/index";

export class Cms extends React.Component<any, any> {

  componentDidMount() {
    setHistoryInstance(this.props.history);
  }

  componentWillReceiveProps(props) {
    if (this.props.auth.isAuthenticated === false && props.auth.isAuthenticated === true) {
      getHistoryInstance().push(URL.MENUS);
    }
  }

  render() {
    const {logout, auth, notifications} = this.props;
    const {isAuthenticated, user} = auth;
    const viewMode: boolean = this.props.history.location.pathname === '/';

    // set left padding for site content (sidebar width)
    const globalSiteStyle = (<style dangerouslySetInnerHTML={{__html: `.site-wrapper {padding-left: 4%}`}}/>);

    return (
      <div className={classnames("cms__container", {"cms__container--view-mode": viewMode})}>
        {globalSiteStyle}
        <Notifications notifications={notifications} />

        <Layout
          sidebar={
            isAuthenticated &&
            <Sidebar
              user={user}
              slim={viewMode}
              onLogout={() => logout()}
          />}
          content={<Content isAuthenticated={isAuthenticated}/>}
          fullHeight={true}
        />
      </div>
    );
  }
}

const mapStateToProps = state => ({
  auth: state.auth,
  notifications: state.notifications,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    logout: () => dispatch(logout()),
  };
};

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(Cms));

