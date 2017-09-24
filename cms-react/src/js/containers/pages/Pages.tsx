import React from 'react';
import {connect, Dispatch} from "react-redux";
import {Container, Row, Col, Button, FormGroup} from 'reactstrap';
import {withRouter} from 'react-router-dom';
import {getPages, savePage} from "./actions";
import {Page as PageModel} from "../../model";


interface PageProps {
  page: PageModel;
  onSave: (pageId, html) => void;
}

export class Page extends React.Component<PageProps, any> {
  private editor;

  constructor(props) {
    super(props);
    this.state = {
      editMode: false,
      html: '',
      draftHtml: '',
    };
  }

  componentDidMount() {
    this.initEventsHandler();
  }

  componentWillReceiveProps(props) {
    setTimeout(() => this.initEventsHandler(), 300);
  }

  initEventsHandler() {
    this.editor.querySelector('.editor-area').addEventListener('click', e => this.onClickArea(e));
  }

  onClickArea(e) {
    this.setState({
      editMode: true,
      html: this.editor.querySelector('.editor-area').innerHTML,
      draftHtml: this.editor.querySelector('.editor-area').innerHTML,
    });
  }

  onChangeArea(e) {
    this.setState({draftHtml: e.target.value});
  }

  onSave() {
    const {onSave, page} = this.props;
    this.setState({editMode: false});
    onSave(page.id, `<div class='editor-area'>${this.state.draftHtml}</div>`);
  }

  onCancel() {
    const {page} = this.props;

    this.setState({
      editMode: false,
      draftHtml: page.html,
    });

    setTimeout(() => this.initEventsHandler(), 300);
  }

  render() {
    const {page} = this.props;

    return (
      <div className="content-white">
        {this.state.editMode &&
        <div>
          <FormGroup>
              <textarea
                className="form-control"
                name="page-editor"
                rows={20}
                value={this.state.draftHtml}
                onChange={e => this.onChangeArea(e)}
              />
          </FormGroup>

          <FormGroup>
            <Button onClick={() => this.onSave()} color="success">Save</Button>
            <Button onClick={() => this.onCancel()} color="secondary">Cancel</Button>
          </FormGroup>
        </div>

        }

        <div ref={editor => this.editor = editor}>
          {!this.state.editMode &&
            <div dangerouslySetInnerHTML={{__html: page.html}} />
          }
        </div>

      </div>
    );
  }
}

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
  pages: state.page.pages,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    onInit: () => dispatch(getPages()),
    onEditHtml: (id, html) => dispatch(savePage(id, {html})),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(Pages);
