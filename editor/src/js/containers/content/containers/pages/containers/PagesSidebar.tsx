import React from 'react';
import {connect, Dispatch} from "react-redux";
import classnames from "classnames";
import {error} from 'react-notification-system-redux';
import {Page} from "../../../../../model";
import {PageSettings} from "../components/PageSettings";
import {URL} from "../../../../../routes";
import {addPage, deletePage, savePage} from "../actions";
import {SidebarList} from "../../../../../components/Sidebar/SidebarList";
import {showModal} from "../../../../../common/containers/modal/actions";
import {State} from "../../../../../reducers/state";
import {notificationParams} from "../../../../../common/utils/NotificationSettings";

interface Props {
  pages: Page[];
  match: any;
  onEditSettings: (pageId, settings) => any;
  onDeletePage: (id) => any;
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

  render() {
    const {pages, match, onEditSettings, onDeletePage, showModal, fetching, showError} = this.props;
    const activePage = match.params.id && pages.find(page => page.id == match.params.id);

    return (
      <div className={classnames({fetching})}>
        {!activePage &&
          <SidebarList
            items={pages}
            category="pages"
            subTitleKey="urls"
            subTitleFilter={(items => items.find(item => item.isDefault).link)}
            onBack={() => this.goBack()}
            onAdd={() => this.onAddPage()}
          />
        }

        {activePage &&
          <PageSettings
            page={activePage}
            pages={pages}
            onBack={() => this.resetActivePage()}
            onEdit={prop => onEditSettings(activePage.id, prop)}
            onDelete={id => onDeletePage(id)}
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
  fetching: state.page.fetching,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    onEditSettings: (pageId, settings) => dispatch(savePage(pageId, settings)),
    onDeletePage: id => dispatch(deletePage(id)),
    showError: title => dispatch(error({...notificationParams, title})),
    onAddPage: () => dispatch(addPage()),
    showModal: props => dispatch(showModal(props)),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(PagesSidebar);
