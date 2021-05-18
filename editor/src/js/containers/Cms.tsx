import React from 'react';
import {connect} from "react-redux";
import clsx from "clsx";
import {Dispatch} from "redux";
import {withRouter} from 'react-router-dom';
import {ThemeProvider, withStyles} from "@material-ui/core/styles";
import ScopedCssBaseline from "@material-ui/core/ScopedCssBaseline";
import Layout from '../components/Layout/Layout';
import Sidebar from '../components/Layout/Sidebar';
import {Content} from '../components/Layout/Content';
import Modal from "../common/containers/modal/Modal";
import {getHistoryInstance, setHistoryInstance} from "../history";
import {URL} from "../routes";
import {getUser, logout} from "./auth/actions";
import {getHistory, publish} from "./history/actions";
import {hideModal, showModal} from "../common/containers/modal/actions";
import {AuthState} from "./auth/reducers/State";
import {ModalState} from "../common/containers/modal/reducers/State";
import {NavigationState} from "../common/containers/Navigation/reducers/State";
import {State} from "../reducers/state";
import {getPageByUrl} from "./content/containers/pages/actions";
import {Page, Version, VersionStatus, Theme} from "../model";
import BrowserWarning from "../common/components/BrowserWarning";
import {Browser} from "../utils";
import {ThemeContext} from "../styles/ThemeContext";
import {defaultTheme} from "../styles/ishTheme";
import GlobalStylesProvider from "../styles/GlobalStylesProvider";
import {BlockState} from "./content/containers/blocks/reducers/State";
import {hideNavigation, setActiveUrl} from "../common/containers/Navigation/actions";
import SwipeableNavigation from "../common/containers/Navigation/SwipeableNavigation";
import MessageProvider from "../common/components/message/MessageProvider";

const styles: any = theme => ({
  cms: {
    position: "fixed",
    top: 0,
    left: 0,
    width: "100%",
    height: "100%",
    pointerEvents: "none",
    zIndex: "999",
    color: theme.palette.text.primary,
  },
  cmsContainer: {
    background: "#fbf9f0",
    pointerEvents: "all",
  },
  cmsContainerViewMode: {
    pointerEvents: "none",
    background: "transparent",
  },
});

interface Props {
  auth: AuthState;
  classes: any;
  notifications: any;
  modal: ModalState;
  navigation: NavigationState;
  logout: () => any;
  hideModal: () => any;
  onPublish: (id) => any;
  showModal: () => any;
  hideNavigation: () => any;
  getUser: () => any;
  getPageByUrl: (url) => any;
  getHistory: () => any;
  setActiveUrl: () => void;
  draftVersion: Version;
  history: any;
  pageEditMode: boolean;
  blocks: BlockState[];
  pages: Page[];
  themes: Theme[];
}

const checkSlimSidebar = history => (
  history.location.pathname === '/'
);

const checkViewMode = (history, pageEditMode) => (
  history.location.pathname === '/' || (history.location.pathname.indexOf('/page/') === 0 && !pageEditMode)
);

class Cms extends React.Component<Props, any> {
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
    const {
      classes,
      blocks,
      logout,
      auth,
      notifications,
      modal,
      hideNavigation,
      navigation,
      hideModal,
      showModal,
      pageEditMode,
      pages,
      themes,
      setActiveUrl
    } = this.props;
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
      <ThemeContext.Provider
        value={{themeName: "default"}}
      >
        <ThemeProvider theme={defaultTheme}>
          <ScopedCssBaseline/>
          <GlobalStylesProvider/>
          <div className={classes.cms}>
            <div className={clsx(classes.cmsContainer, viewMode && classes.cmsContainerViewMode)}>
              {globalSiteStyle}
              {hasBrowserWarning && <BrowserWarning />}
              <Modal {...modal} onHide={hideModal}/>
              <SwipeableNavigation open={navigation.showNavigation} hideNavigation={hideNavigation}/>

              <Layout
                sidebar={
                  isAuthenticated &&
                    <Sidebar
                      user={user}
                      slim={slimSidebar}
                      onLogout={() => logout()}
                      navigation={navigation}
                      blocks={blocks}
                      pages={pages}
                      themes={themes}
                      hideNavigation={hideNavigation}
                      onPublish={() => this.publish()}
                      showModal={showModal}
                      setActiveUrl={setActiveUrl}
                    />
                }
                content={<Content isAuthenticated={isAuthenticated}/>}
                fullHeight={true}
              />
            </div>
          </div>
          <MessageProvider/>
        </ThemeProvider>
      </ThemeContext.Provider>
    );
  }
}

const mapStateToProps = (state: State) => ({
  auth: state.auth,
  notifications: state.notifications,
  modal: state.modal,
  blocks: state.block.items,
  pages: state.page.items,
  themes: state.theme.items,
  navigation: state.navigation,
  pageEditMode: state.page.editMode,
  draftVersion: state.history.versions && state.history.versions.find(v => v.status === VersionStatus.draft),
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    logout: () => dispatch(logout()),
    hideModal: () => dispatch(hideModal()),
    hideNavigation: () => dispatch(hideNavigation()),
    showModal: props => dispatch(showModal(props)),
    getPageByUrl: url => dispatch(getPageByUrl(url)),
    getHistory: () => dispatch(getHistory()),
    getUser: () => dispatch(getUser()),
    setActiveUrl: (url) => dispatch(setActiveUrl(url)),
  };
};

export default withRouter(connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withStyles(styles)(Cms) as any));

