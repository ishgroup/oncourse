import React from 'react';
import {connect, Dispatch} from "react-redux";
import {getHistoryInstance} from "../../../history";
import {Page} from "../../../model";
import {PagesList} from "../components/PageList";
import {PageSettings} from "../components/PageSettings";
import {URL} from "../../../routes";
import {editPageSettings} from "../actions/index";

interface Props {
  pages: Page[];
  match: any;
  editPageSettings: (pageId, settings) => any;
}

export class PagesSidebar extends React.Component<Props, any> {

  goBack() {
    getHistoryInstance().push(URL.CONTENT);
  }

  resetActivePage() {
    getHistoryInstance().push(URL.PAGES);
  }

  render() {
    const {pages, match, editPageSettings} = this.props;
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
          onEdit={prop => editPageSettings(activePage.id, prop)}
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
    editPageSettings: (pageId, settings) => dispatch(editPageSettings(pageId, settings)),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(PagesSidebar);
