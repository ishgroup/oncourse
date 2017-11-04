import React from 'react';
import {connect, Dispatch} from "react-redux";
import {clearRenderHtml, getPages, savePage, toggleEditMode} from "./actions";
import {Page as PageModel} from "../../../../model";
import {Page} from "./components/Page";
import {DOM} from "../../../../utils";

export const defaultPage = {...new PageModel(), id: -1};

interface Props {
  pages: PageModel[];
  onInit: () => any;
  match?: any;
  onEditHtml: (html) => any;
  toggleEditMode: (flag: boolean) => any;
  clearRenderHtml: (pageId: number) => any;
  history: any;
  editMode: any;
}

export class Pages extends React.Component<Props, any> {

  componentDidMount() {
    this.props.onInit();
  }

  openPage(url) {
    document.location.href = url;
  }

  onClickArea() {
    // this.setState({
    //   editMode: true,
    //   html: page.html,
    //   draftHtml: page.html,
    // });
    // toggleEditMode(true);

    // console.log(page);
    // getHistoryInstance().push(`/pages/${page.id}`);
  }


  render() {
    const {match, pages, onEditHtml, toggleEditMode, clearRenderHtml, editMode} = this.props;
    const activePage = match.params.id && pages.find(page => page.id == match.params.id);

    // if (activePage) {
    //   const pageNode = DOM.findPage(activePage.id);
    //   pageNode.addEventListener('click', this.onClickArea.bind(this));
    // }

    return (
      <div>
        {activePage &&
        <Page
          page={activePage}
          onSave={onEditHtml}
          openPage={url => this.openPage(url)}
          toggleEditMode={flag => toggleEditMode(flag)}
          clearRenderHtml={clearRenderHtml}
          editMode={editMode}
        />
        }
      </div>
    );
  }
}

const mapStateToProps = state => ({
  pages: state.page.items,
  editMode: state.page.editMode,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    onInit: () => dispatch(getPages()),
    onEditHtml: (id, html) => dispatch(savePage(id, {html}, true)),
    toggleEditMode: flag => dispatch(toggleEditMode(flag)),
    clearRenderHtml: pageId => dispatch(clearRenderHtml(pageId)),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(Pages);
