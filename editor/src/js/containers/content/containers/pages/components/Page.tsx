import React from 'react';
import {Container, Row, Col, Button, FormGroup} from 'reactstrap';
import {Page as PageModel} from "../../../../../model";
import {Editor} from "../../../../../common/components/Editor";
import {DOM} from "../../../../../utils";
import {getHistoryInstance} from "../../../../../history";

interface PageProps {
  page: PageModel;
  onSave: (pageId, html) => void;
  openPage: (url) => void;
  toggleEditMode: (flag: boolean) => any;
}


export class Page extends React.PureComponent<PageProps, any> {

  constructor(props) {
    super(props);
    this.state = {
      editMode: false,
      html: '',
      draftHtml: '',
    };
  }

  componentDidMount() {
    const {page, openPage, toggleEditMode} = this.props;
    const pageNode = DOM.findPage(page.id);
    toggleEditMode(false);

    if (pageNode) {
      pageNode.addEventListener('click', () => this.onClickArea());
      return;
    }

    // openPage(page.url);
  }

  componentWillReceiveProps(props) {
    if (props.page.id !== this.props.page.id) {
      this.setState({
        editMode: true,
        html: props.page.html,
        draftHtml: props.page.html,
      });
    }
  }

  componentWillUnmount() {
    const {page} = this.props;
    const pageNode = DOM.findPage(page.id);
    pageNode && pageNode.removeEventListener('click', () => this.onClickArea());
  }

  onClickArea() {
    const {page, toggleEditMode} = this.props;

    getHistoryInstance().push(`/pages/${page.id}`);

    toggleEditMode(true);
    this.setState({
      editMode: true,
      html: page.html,
      draftHtml: page.html,
    });
  }

  onChangeArea(val) {
    this.setState({draftHtml: val});
  }

  onSave() {
    const {onSave, page, toggleEditMode} = this.props;

    toggleEditMode(false);
    this.setState({editMode: false});
    onSave(page.id, this.state.draftHtml);
  }

  onCancel() {
    const {page, toggleEditMode} = this.props;

    toggleEditMode(false);
    this.setState({
      editMode: false,
      draftHtml: page.html,
    });
  }

  tmp(page) {
    return (
      <div>
      {this.state.editMode &&
        <div>
          <FormGroup>
            <Editor
              value={this.state.draftHtml}
              onChange={val => this.onChangeArea(val)}
            />
          </FormGroup>

          <FormGroup>
            <Button onClick={() => this.onSave()} color="primary">Save</Button>
            <Button onClick={() => this.onCancel()} color="secondary">Cancel</Button>
          </FormGroup>
        </div>
      }

      <div onClick={() => this.onClickArea()}>
        {!this.state.editMode &&
          <div className="editor-area" dangerouslySetInnerHTML={{__html: page.html}} />
        }
      </div>
    </div>
    );
  }

  render() {

    return (
      <div>
        {this.state.editMode &&
        <div>
          <FormGroup>
            <Editor
              value={this.state.draftHtml}
              onChange={val => this.onChangeArea(val)}
            />
          </FormGroup>

          <FormGroup>
            <Button onClick={() => this.onSave()} color="primary">Save</Button>
            <Button onClick={() => this.onCancel()} color="secondary">Cancel</Button>
          </FormGroup>
        </div>
        }
      </div>
    );
  }
}
