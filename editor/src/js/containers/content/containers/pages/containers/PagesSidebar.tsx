import React from 'react';
import {connect, Dispatch} from "react-redux";
import {Page} from "../../../../../model";
import {PageSettings} from "../components/PageSettings";
import {URL} from "../../../../../routes";
import {addPage, deletePage, savePage} from "../actions";
import {SidebarList} from "../../../../../components/Sidebar/SidebarList";
import {showModal} from "../../../../../common/containers/modal/actions";
import {defaultPage} from "../Pages";

interface Props {
  pages: Page[];
  match: any;
  onEditSettings: (pageId, settings) => any;
  onDeletePage: (id) => any;
  onAddPage: () => any;
  history: any;
  showModal: (props) => any;
}

export class PagesSidebar extends React.Component<Props, any> {

  goBack() {
    this.props.history.push(URL.CONTENT);
  }

  resetActivePage() {
    this.props.history.push(URL.PAGES);
  }

  onAddPage() {
    const {onAddPage} = this.props;
    onAddPage();
  }

  render() {
    const {pages, match, onEditSettings, onDeletePage, showModal} = this.props;
    const activePage = match.params.id && (pages.find(page => page.id == match.params.id) || defaultPage);

    return (
      <div>
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
            onBack={() => this.resetActivePage()}
            onEdit={prop => onEditSettings(activePage.id, prop)}
            onDelete={id => onDeletePage(id)}
            showModal={showModal}
          />
        }
      </div>
    );
  }
}

const mapStateToProps = state => ({
  pages: state.page.items,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    onEditSettings: (pageId, settings) => dispatch(savePage(pageId, settings)),
    onDeletePage: id => dispatch(deletePage(id)),
    onAddPage: () => dispatch(addPage()),
    showModal: props => dispatch(showModal(props)),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(PagesSidebar);
