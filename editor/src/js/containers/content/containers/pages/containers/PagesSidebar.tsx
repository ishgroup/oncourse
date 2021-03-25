import React from 'react';
import {connect} from "react-redux";
import {Dispatch} from "redux";
import classnames from "classnames";
import {error} from 'react-notification-system-redux';
import {Page, Theme} from "../../../../../model";
import {PageSettings} from "../components/PageSettings";
import {URL} from "../../../../../routes";
import {addPage, deletePage, savePage} from "../actions";
import {SidebarList} from "../../../../../components/Sidebar/SidebarList";
import {showModal} from "../../../../../common/containers/modal/actions";
import {State} from "../../../../../reducers/state";
import {notificationParams} from "../../../../../common/utils/NotificationSettings";
import PageService from "../../../../../services/PageService";

interface Props {
  pages: Page[];
  themes: Theme[];
  match: any;
  onEditSettings: (id, settings) => any;
  onDeletePage: (page) => any;
  onAddPage: () => any;
  showError: (title) => any;
  history: any;
  fetching: boolean;
  showModal: (props) => any;
}

export class PagesSidebar extends React.Component<Props, any> {

  goBack() {
    this.props.history.push(URL.CONTENT, {updateActiveUrl: true});
  }

  resetActivePage() {
    this.props.history.push(URL.PAGES);
  }

  onAddPage() {
    const {onAddPage} = this.props;
    onAddPage();
  }

  getDefaultLink(items, page) {
    const defaultUrl = items.find(item => item.isDefault);
    return defaultUrl ? defaultUrl.link : PageService.generateBasetUrl(page).link;
  }

  render() {
    const {pages, match, onEditSettings, onDeletePage, showModal, fetching, showError, themes} = this.props;
    const activePage = match.params.id && pages.find(page => page.id == match.params.id);

    return (
      <div className={classnames({fetching})}>
        {!activePage &&
          <SidebarList
            items={pages}
            idKey="id"
            category="page"
            subTitleKey="urls"
            subTitleFilterFunc={(items, page) => this.getDefaultLink(items, page)}
            onBack={() => this.goBack()}
            onAdd={() => this.onAddPage()}
          />
        }

        {activePage &&
          <PageSettings
            page={activePage}
            pages={pages}
            themes={themes}
            onBack={() => this.resetActivePage()}
            onEdit={prop => onEditSettings(activePage.id, prop)}
            onDelete={pageId => onDeletePage(pageId)}
            showModal={showModal}
            showError={showError}
          />
        }
      </div>
    );
  }
}

const mapStateToProps = (state: State) => ({
  pages: state.page.items,
  themes: state.theme.items,
  fetching: state.fetching,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    onEditSettings: (id, settings) => dispatch(savePage(id, settings)),
    onDeletePage: pageId => dispatch(deletePage(pageId)),
    showError: title => dispatch(error({...notificationParams, title})),
    onAddPage: () => dispatch(addPage()),
    showModal: props => dispatch(showModal(props)),
  };
};

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(PagesSidebar as any);
