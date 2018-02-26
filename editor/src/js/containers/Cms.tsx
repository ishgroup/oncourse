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
import {getHistory, publish} from "./history/actions";
import {hideModal, showModal} from "../common/containers/modal/actions";
import {AuthState} from "./auth/reducers/State";
import {ModalState} from "../common/containers/modal/reducers/State";
import {State} from "../reducers/state";
import {getPageByUrl} from "./content/containers/pages/actions";
import {Version, VersionStatus} from "../model";

interface Props {
  auth: AuthState;
  notifications: any;
  modal: ModalState;
  logout: () => any;
  hideModal: () => any;
  onPublish: (id) => any;
  showModal: () => any;
  getPageByUrl: (url) => any;
  getHistory: () => any;
  draftVersion: Version;
  history: any;
  pageEditMode: boolean;
}

const checkSlimSidebar = history => (
  history.location.pathname === '/'
);

const checkViewMode = (history, pageEditMode) => (
  history.location.pathname === '/' || (history.location.pathname.indexOf('/pages/') === 0 && !pageEditMode)
);

export class Cms extends React.Component<Props, any> {

  componentDidMount() {
    setHistoryInstance(this.props.history);

    if (this.props.auth.isAuthenticated) {
      this.props.getPageByUrl(document.location.pathname);
      this.props.getHistory();
    }
  }

  componentWillReceiveProps(props) {
    if (this.props.auth.isAuthenticated === false && props.auth.isAuthenticated === true) {
      getHistoryInstance().push(URL.SITE);
      this.props.getPageByUrl(document.location.pathname);
    }
  }

  publish() {
    const {onPublish, draftVersion} = this.props;
    draftVersion && onPublish(draftVersion.id);
  }

  render() {
    const {logout, auth, notifications, modal, hideModal, showModal, pageEditMode} = this.props;
    const {isAuthenticated, user} = auth;
    const viewMode: boolean = checkViewMode(this.props.history, pageEditMode);
    const slimSidebar: boolean = checkSlimSidebar(this.props.history);
    const globalPadding = viewMode && slimSidebar ? '70px' : '16.666667%';

    const styles = `
      .site-wrapper {
        padding-left:${globalPadding};
      }
      #content > div[class^='block-'] {
        border: 1px solid transparent;
        cursor: pointer;
      }
      #content > div[class^='block-']:hover {
        border: 1px solid red;
      }
    `;

    // set left padding for site content (sidebar width)
    const globalSiteStyle = (<style dangerouslySetInnerHTML={{__html: styles}}/>);

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
                  onPublish={() => this.publish()}
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
  pageEditMode: state.page.editMode,
  draftVersion: state.history.versions && state.history.versions.find(v => !v.published),
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    logout: () => dispatch(logout()),
    hideModal: () => dispatch(hideModal()),
    onPublish: id => dispatch(publish(id, VersionStatus.published)),
    showModal: props => dispatch(showModal(props)),
    getPageByUrl: url => dispatch(getPageByUrl(url)),
    getHistory: () => dispatch(getHistory()),
  };
};

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(Cms));

