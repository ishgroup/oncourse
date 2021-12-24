import React from 'react';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import clsx from 'clsx';
import { Page, Theme } from '../../../../../model';
import PageSettings from '../components/PageSettings';
import { URL } from '../../../../../routes';
import { addPage, deletePage, savePage } from '../actions';
import SidebarList from '../../../../../components/Sidebar/SidebarList';
import { showModal } from '../../../../../common/containers/modal/actions';
import { State } from '../../../../../reducers/state';
import PageService from '../../../../../services/PageService';
import { hideNavigation, setActiveUrl, showNavigation } from '../../../../../common/containers/Navigation/actions';
import { SHOW_MESSAGE } from '../../../../../common/components/message/actions';
import { getLayouts, getThemes } from '../../../../design/containers/themes/actions';
import { getBlocks } from '../../blocks/actions';

interface Props {
  pages: Page[];
  currentPage: Page;
  themes: Theme[];
  match: any;
  onEditSettings: (id, settings) => any;
  showNavigation: () => void;
  onDeletePage: (page) => any;
  onAddPage: () => any;
  showError: (title) => any;
  history: any;
  fetching: boolean;
  showModal: (props) => any;
  getThemes: () => any;
}

export class PagesSidebar extends React.Component<Props, any> {
  componentDidMount() {
    const { themes, getThemes } = this.props;
    if (!themes.length) {
      getThemes();
    }
  }

  goBack() {
    this.props.history.push(URL.CONTENT, { updateActiveUrl: true });
  }

  resetActivePage() {
    this.props.history.push(URL.PAGES);
  }

  onAddPage() {
    const { onAddPage } = this.props;
    onAddPage();
  }

  getDefaultLink(items, page) {
    const defaultUrl = items.find((item) => item.isDefault);
    return defaultUrl ? defaultUrl.link : PageService.generateBasetUrl(page).link;
  }

  render() {
    const {
      pages, match, currentPage, onEditSettings, onDeletePage, showModal, history, showNavigation, fetching, showError, themes
    } = this.props;

    return (
      <div className={clsx((fetching && 'fetching'))}>
        {!match.params.id
          && (
          <SidebarList
            items={pages}
            idKey="id"
            category="page"
            subTitleKey="urls"
            subTitleFilterFunc={(items, page) => this.getDefaultLink(items, page)}
            onBack={() => this.goBack()}
            onAdd={() => this.onAddPage()}
            showNavigation={showNavigation}
          />
          )}

        {match.params.id && currentPage
          && (
          <PageSettings
            page={currentPage}
            pages={pages}
            themes={themes}
            onBack={() => this.resetActivePage()}
            onEdit={(prop) => onEditSettings(currentPage.id, prop)}
            onDelete={(pageId) => onDeletePage(pageId)}
            showModal={showModal}
            showError={showError}
            history={history}
            showNavigation={showNavigation}
          />
          )}
      </div>
    );
  }
}

const mapStateToProps = (state: State) => ({
  currentPage: state.page.currentPage,
  pages: state.page.items,
  themes: state.theme.items,
  fetching: state.fetching,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  getThemes: () => dispatch(getThemes()),
  onEditSettings: (id, settings) => dispatch(savePage(id, settings)),
  onDeletePage: (pageId) => dispatch(deletePage(pageId)),
  showError: (title) => dispatch(
    {
      type: SHOW_MESSAGE,
      payload: { message: title, success: false },
    }
  ),
  onAddPage: () => dispatch(addPage()),
  showModal: (props) => dispatch(showModal(props)),
  showNavigation: () => dispatch(showNavigation()),
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(PagesSidebar as any);
