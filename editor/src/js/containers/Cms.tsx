import React from 'react';
import {connect} from "react-redux";
import {Route, NavLink, Redirect, withRouter} from 'react-router-dom';
import Notifications from 'react-notification-system-redux';
import classnames from 'classnames';
import {Layout} from '../components/Layout/Layout';
import {Sidebar} from '../components/Layout/Sidebar';
import {Content} from '../components/Layout/Content';
import {Modal} from "../common/containers/modal/Modal";
import {getHistoryInstance, setHistoryInstance} from "../history";
import {URL} from "../routes";
import {getUser, logout} from "./auth/actions";
import {getHistory, publish} from "./history/actions";
import {hideModal, showModal} from "../common/containers/modal/actions";
import {AuthState} from "./auth/reducers/State";
import {ModalState} from "../common/containers/modal/reducers/State";
import {State} from "../reducers/state";
import {getPageByUrl} from "./content/containers/pages/actions";
import {Version, VersionStatus} from "../model";
import {BrowserWarning} from "../common/components/BrowserWarning";
import {Browser} from "../utils";
import {Dispatch} from "redux";

interface Props {
  auth: AuthState;
  notifications: any;
  modal: ModalState;
  logout: () => any;
  hideModal: () => any;
  onPublish: (id) => any;
  showModal: () => any;
  getUser: () => any;
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
  history.location.pathname === '/' || (history.location.pathname.indexOf('/page/') === 0 && !pageEditMode)
);

export class Cms extends React.Component<Props, any> {

  componentDidMount() {
    setHistoryInstance(this.props.history);

    if (this.props.auth.isAuthenticated) {
      this.props.getUser();
      this.props.getHistory();
      this.props.getPageByUrl(document.location.pathname);
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
    const hasBrowserWarning = isAuthenticated && Browser.unsupported();
    const warningPadding = hasBrowserWarning ? '37px' : '0px';

    const styles = `
      .site-wrapper {
        padding-left:${globalPadding};
        padding-top: ${warningPadding};
      }
      #content div[class^='block-'] {
        border: 1px solid transparent;
        cursor: pointer;
      }
      #content div[class^='block-']:hover {
        border: 1px solid red;
      }
    `;

    // set left padding for site content (sidebar width)
    const globalSiteStyle = (<style dangerouslySetInnerHTML={{__html: styles}}/>);

    return (
      <div className="cms">
        <div className={classnames("cms__container", {"cms__container--view-mode": viewMode})}>
          {globalSiteStyle}
          {hasBrowserWarning && <BrowserWarning />}
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
  draftVersion: state.history.versions && state.history.versions.find(v => v.status === VersionStatus.draft),
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    logout: () => dispatch(logout()),
    hideModal: () => dispatch(hideModal()),
    onPublish: id => dispatch(publish(id, VersionStatus.published)),
    showModal: props => dispatch(showModal(props)),
    getPageByUrl: url => dispatch(getPageByUrl(url)),
    getHistory: () => dispatch(getHistory()),
    getUser: () => dispatch(getUser()),
  };
};

export default withRouter(connect<any,any,any>(mapStateToProps, mapDispatchToProps)(Cms as any));

