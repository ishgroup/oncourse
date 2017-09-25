import React from 'react';
import {connect, Dispatch} from "react-redux";
import {getHistoryInstance} from "../../../history";
import {Page} from "../../../model";
import {PagesList} from "../components/PageList";
import {PageSettings} from "../components/PageSettings";
import {URL} from "../../../routes";
import {deletePage, savePage} from "../actions";

interface Props {
  pages: Page[];
  match: any;
  onEditSettings: (pageId, settings) => any;
  onDeletePage: (id) => any;
}

export class PagesSidebar extends React.Component<Props, any> {

  goBack() {
    getHistoryInstance().push(URL.CONTENT);
  }

  resetActivePage() {
    getHistoryInstance().push(URL.PAGES);
  }

  render() {
    const {pages, match, onEditSettings, onDeletePage} = this.props;
    const activePage = match.params.id && pages.find(page => page.id == match.params.id);

    return (
      <div>
        {!activePage &&
          <PagesList pages={pages} onBack={this.goBack}/>
        }

        {activePage &&
        <PageSettings
          page={activePage}
          onBack={this.resetActivePage}
          onEdit={prop => onEditSettings(activePage.id, prop)}
          onDelete={id => onDeletePage(id)}
        />
        }
      </div>
    );
  }
}

const mapStateToProps = state => ({
  pages: state.page.pages,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    onEditSettings: (pageId, settings) => dispatch(savePage(pageId, settings)),
    onDeletePage: id => dispatch(deletePage(id)),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(PagesSidebar);
