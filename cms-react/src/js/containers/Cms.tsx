import React from 'react';
import {connect, Dispatch} from "react-redux";
import {Route, NavLink, Redirect, withRouter} from 'react-router-dom';
import classnames from 'classnames';
import {Layout} from '../components/Layout/Layout';
import {Sidebar} from '../components/Layout/Sidebar';
import {Content} from '../components/Layout/Content';
import {getHistoryInstance, setHistoryInstance} from "../history";

export class Cms extends React.Component<any, any> {

  componentDidMount() {
    setHistoryInstance(this.props.history);
  }

  componentWillReceiveProps(props) {
    if (this.props.auth.isAuthenticated === false && props.auth.isAuthenticated === true) {
      getHistoryInstance().push('/');
    }
  }

  render() {
    const {isAuthenticated, user} = this.props.auth;
    const viewMode: boolean = this.props.history.location.pathname === '/';

    // set left padding for site content (sidebar width)
    const globalSiteStyle = (<style dangerouslySetInnerHTML={{__html: `.site-wrapper {padding-left: 4%}`}}/>);

    return (
      <div className={classnames("cms__container", {"cms__container--view-mode": viewMode})}>
        {globalSiteStyle}
        <Layout
          sidebar={isAuthenticated && <Sidebar user={user} slim={viewMode}/>}
          content={<Content isAuthenticated={isAuthenticated}/>}
          fullHeight={true}
        />
      </div>
    );
  }
}

const mapStateToProps = state => ({
  auth: state.auth,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {};
};

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(Cms));

