import React from 'react';
import {connect} from "react-redux";
import {Dispatch} from "redux";
import classnames from 'classnames';
import {clearRenderHtml, getPages, savePage, setPageContentMode, toggleEditMode} from "./actions";
import {ContentMode, Page as PageModel} from "../../../../model";
import {Page} from "./components/Page";
import {State} from "../../../../reducers/state";
import {getThemes} from "../../../design/containers/themes/actions";

interface Props {
  pages: PageModel[];
  onInit: () => any;
  match?: any;
  onEditHtml: (html) => any;
  toggleEditMode: (flag: boolean) => any;
  clearRenderHtml: (pageId: number) => any;
  setContentMode?: (id: number, contentMode: ContentMode) => any;
  history: any;
  editMode: any;
  fetching: boolean;
}

export class Pages extends React.Component<Props, any> {

  componentDidMount() {
    this.props.onInit();
  }

  openPage(url) {
    document.location.href = url;
  }

  render() {
    const {match, pages, onEditHtml, toggleEditMode, clearRenderHtml, editMode, fetching, setContentMode} = this.props;
    const activePage = match.params.id && pages.find(page => page.id == match.params.id);

    return (
      <div>
        {activePage &&
          <div className={classnames({fetching})}>
            <Page
              page={activePage}
              onSave={onEditHtml}
              openPage={this.openPage}
              toggleEditMode={toggleEditMode}
              clearRenderHtml={clearRenderHtml}
              editMode={editMode}
              setContentMode={setContentMode}
            />
          </div>
        }
      </div>
    );
  }
}

const mapStateToProps = (state: State) => ({
  pages: state.page.items,
  fetching: state.fetching,
  editMode: state.page.editMode,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    onInit: () => {
      dispatch(getPages());
      dispatch(getThemes());
    },
    onEditHtml: (id, content) => dispatch(savePage(id, {content}, true)),
    toggleEditMode: flag => dispatch(toggleEditMode(flag)),
    clearRenderHtml: id => dispatch(clearRenderHtml(id)),
    setContentMode: (id: number, contentMode: ContentMode) => dispatch(setPageContentMode(id,contentMode)),
  };
};

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(Pages as any);
