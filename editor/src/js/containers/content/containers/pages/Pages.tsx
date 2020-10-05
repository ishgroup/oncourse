import React from 'react';
import {connect, Dispatch} from "react-redux";
import classnames from 'classnames';
import {clearRenderHtml, getPages, savePage, toggleEditMode} from "./actions";
import {Page as PageModel} from "../../../../model";
import {Page} from "./components/Page";
import {State} from "../../../../reducers/state";
import {getThemes} from "../../../design/containers/themes/actions/index";

interface Props {
  pages: PageModel[];
  onInit: () => any;
  match?: any;
  onEditHtml: (html) => any;
  toggleEditMode: (flag: boolean) => any;
  clearRenderHtml: (pageId: number) => any;
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
    const {match, pages, onEditHtml, toggleEditMode, clearRenderHtml, editMode, fetching} = this.props;
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
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(Pages as any);
