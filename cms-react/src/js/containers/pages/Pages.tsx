import React from 'react';
import {connect, Dispatch} from "react-redux";
import {withRouter} from 'react-router-dom';
import {getPages, savePage} from "./actions";
import {Page as PageModel} from "../../model";
import {Page} from "./components/Page";

interface Props {
  pages: PageModel[];
  onInit: () => any;
  match?: any;
  onEditHtml: (html) => any;
}

export class Pages extends React.Component<Props, any> {

  componentDidMount() {
    this.props.onInit();
  }

  render() {
    const {match, pages, onEditHtml} = this.props;

    return (
      <div>
        {match.params.id &&
        <Page
          page={pages.find(page => page.id == match.params.id)}
          onSave={onEditHtml}
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
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(Pages);
