import React from 'react';
import {connect, Dispatch} from "react-redux";
import {Route, NavLink, Redirect, withRouter} from 'react-router-dom';
import Notifications from 'react-notification-system-redux';
import classnames from 'classnames';

import {Layout} from '../components/Layout/Layout';
import {Sidebar} from '../components/Layout/Sidebar';
import {Content} from '../components/Layout/Content';
import {Modal} from "../common/containers/modal/Modal";
import {getHistoryInstance, setHistoryInstance} from "../history";
import {URL} from "../routes";
import {logout} from "./auth/actions";
import {publish} from "./history/actions";
import {hideModal, showModal} from "../common/containers/modal/actions";
import {AuthState} from "./auth/reducers/State";
import {ModalState} from "../common/containers/modal/reducers/State";
import {State} from "../reducers/state";

interface Props {
  auth: AuthState;
  notifications: any;
  modal: ModalState;
  logout: () => any;
  hideModal: () => any;
  onPublish: () => any;
  showModal: () => any;
  history: any;
}

const checkSlimSidebar = history => (
  history.location.pathname === '/'
);

const checkViewMode = history => (
  history.location.pathname === '/' || history.location.pathname.indexOf('/pages/') === 0
);

export class Cms extends React.Component<Props, any> {

  componentDidMount() {
    setHistoryInstance(this.props.history);
  }

  componentWillReceiveProps(props) {
    if (this.props.auth.isAuthenticated === false && props.auth.isAuthenticated === true) {
      getHistoryInstance().push(URL.SITE);
    }
  }

  render() {
    const {logout, auth, notifications, modal, hideModal, onPublish, showModal} = this.props;
    const {isAuthenticated, user} = auth;
    const viewMode: boolean = checkViewMode(this.props.history);
    const slimSidebar: boolean = checkSlimSidebar(this.props.history);
    const globalPadding = viewMode && slimSidebar ? '4%' : '16.666667%';

    // set left padding for site content (sidebar width)
    const globalSiteStyle = (<style dangerouslySetInnerHTML={{__html: `.site-wrapper {padding-left: ${globalPadding}}`}}/>);

    return (
      <div className="cms">
        <div className={classnames("cms__container", {"cms__container--view-mode": viewMode})}>
          {globalSiteStyle}
          <Notifications notifications={notifications} />
          <Modal {...modal} onHide={hideModal}/>
          <Layout
            sidebar={
              isAuthenticated &&
                <Sidebar
                  user={user}
                  slim={slimSidebar}
                  onLogout={() => logout()}
                  onPublish={() => onPublish()}
                  showModal={showModal}
                />
            }
            content={<Content isAuthenticated={isAuthenticated}/>}
            fullHeight={true}
          />
        </div>
      </div>
    );
  }
}

const mapStateToProps = (state: State) => ({
  auth: state.auth,
  notifications: state.notifications,
  modal: state.modal,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    logout: () => dispatch(logout()),
    hideModal: () => dispatch(hideModal()),
    onPublish: () => dispatch(publish()),
    showModal: props => dispatch(showModal(props)),
  };
};

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(Cms));

