import React from 'react';
import {connect} from "react-redux";
import {Dispatch} from "redux";
import clsx from "clsx";
import {clearRenderHtml, getPages, savePage, setPageContentMode, toggleEditMode} from "./actions";
import {Block, ContentMode, Page as PageModel} from "../../../../model";
import {Page} from "./components/Page";
import {State} from "../../../../reducers/state";
import {clearBlockRenderHtml, getBlocks, saveBlock} from "../blocks/actions";

interface Props {
  pages: PageModel[];
  onInit: () => any;
  match?: any;
  onEditHtml: (html) => any;
  toggleEditMode: (flag: boolean) => any;
  clearRenderHtml: (pageId: number) => any;
  clearBlockRenderHtml: () => any;
  setContentMode?: (id: number, contentMode: ContentMode) => any;
  onSaveBlock: (id, html) => any;
  history: any;
  editMode: any;
  fetching: boolean;
  blocks: Block[];
}

export class Pages extends React.Component<Props, any> {
  componentDidMount() {
    this.props.onInit();
  }

  openPage(url) {
    document.location.href = url;
  }

  render() {
    const {match, pages, blocks, onEditHtml, onSaveBlock, toggleEditMode,
      clearRenderHtml, clearBlockRenderHtml, editMode, fetching, setContentMode} = this.props;
    const activePage = match.params.id && pages.find(page => page.id == match.params.id);

    return (
      <>
        {activePage &&
          <div className={clsx(fetching && "fetching")}>
            <Page
              page={activePage}
              onSave={onEditHtml}
              onSaveBlock={onSaveBlock}
              openPage={this.openPage}
              toggleEditMode={toggleEditMode}
              clearRenderHtml={clearRenderHtml}
              clearBlockRenderHtml={clearBlockRenderHtml}
              editMode={editMode}
              setContentMode={setContentMode}
              blocks={blocks}
            />
          </div>
        }
      </>
    );
  }
}

const mapStateToProps = (state: State) => ({
  blocks: state.block.items,
  pages: state.page.items,
  fetching: state.fetching,
  editMode: state.page.editMode,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    onInit: () => {
      dispatch(getPages());
      dispatch(getBlocks());
    },
    onEditHtml: (id, content) => dispatch(savePage(id, {content}, true)),
    onSaveBlock: (id, content) => dispatch(saveBlock(id, {content}, true)),
    toggleEditMode: flag => dispatch(toggleEditMode(flag)),
    clearRenderHtml: id => dispatch(clearRenderHtml(id)),
    clearBlockRenderHtml: () => dispatch(clearBlockRenderHtml()),
    setContentMode: (id: number, contentMode: ContentMode) => dispatch(setPageContentMode(id,contentMode)),
  };
};

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(Pages as any);
