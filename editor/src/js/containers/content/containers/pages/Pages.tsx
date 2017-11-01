import React from 'react';
import {connect, Dispatch} from "react-redux";
import {getPages, savePage, toggleEditMode} from "./actions";
import {Page as PageModel} from "../../../../model";
import {Page} from "./components/Page";

export const defaultPage = {...new PageModel(), id: -1};

interface Props {
  pages: PageModel[];
  onInit: () => any;
  match?: any;
  onEditHtml: (html) => any;
  toggleEditMode: (flag: boolean) => any;
  history: any;
}

export class Pages extends React.Component<Props, any> {

  componentDidMount() {
    this.props.onInit();
  }

  openPage(url) {
    document.location.href = url;
  }

  render() {
    const {match, pages, onEditHtml, toggleEditMode} = this.props;
    const activePage = match.params.id && (pages.find(page => page.id == match.params.id) || defaultPage);

    return (
      <div>
        {activePage &&
        <Page
          page={activePage}
          onSave={onEditHtml}
          openPage={url => this.openPage(url)}
          toggleEditMode={flag => toggleEditMode(flag)}
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
    onInit: () => dispatch(getPages()),
    onEditHtml: (id, html) => dispatch(savePage(id, {html})),
    toggleEditMode: flag => dispatch(toggleEditMode(flag)),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(Pages);
