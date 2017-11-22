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
    const activePage = match.params.number && pages.find(page => page.number == match.params.number);

    return (
      <div>
        {activePage &&
          <div className={classnames({fetching})}>
            <Page
              page={activePage}
              onSave={onEditHtml}
              openPage={url => this.openPage(url)}
              toggleEditMode={flag => toggleEditMode(flag)}
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
    onEditHtml: (id, html) => dispatch(savePage(id, {html}, true)),
    toggleEditMode: flag => dispatch(toggleEditMode(flag)),
    clearRenderHtml: pageId => dispatch(clearRenderHtml(pageId)),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(Pages);
