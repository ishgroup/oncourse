import React from 'react';
import {Container, Row, Col, Button, FormGroup} from 'reactstrap';
import {PageState} from "../reducers/State";
import {Editor} from "../../../../../common/components/Editor";
import {DOM} from "../../../../../utils";
import {getHistoryInstance} from "../../../../../history";

interface PageProps {
  page: PageState;
  onSave: (pageId, content) => void;
  openPage: (url) => void;
  toggleEditMode: (flag: boolean) => any;
  clearRenderHtml?: (pageId: number) => void;
  editMode?: any;
}


export class Page extends React.PureComponent<PageProps, any> {

  constructor(props) {
    super(props);
    this.state = {
      editMode: false,
      content: '',
      draftContent: '',
    };
  }

  componentDidMount() {
    const {page, openPage, toggleEditMode} = this.props;
    const pageNode = DOM.findPage(page.id);

    toggleEditMode(false);
    this.setState({
      editMode: false,
      content: page.content,
      draftContent: page.content,
    });

    if (pageNode) {
      pageNode.addEventListener('click', this.onClickArea.bind(this));
      return;
    }

    // openPage(page.url);
  }

  componentWillReceiveProps(props) {
    const {clearRenderHtml, page, toggleEditMode, editMode} = this.props;

    if (props.page.id !== this.props.page.id) {
      toggleEditMode(true);

      this.setState({
        editMode: true,
        content: props.page.content,
        draftContent: props.page.content,
      });
    }

    if (editMode === false && props.editMode === true) {
      this.setState({
        editMode: true,
      });
    }

    if (props.page.renderHtml && props.page.renderHtml !== this.props.page.renderHtml) {
      this.replacePageHtml(props.page.renderHtml);
      clearRenderHtml(page.id);
    }
  }

  componentWillUnmount() {
    const {page} = this.props;
    const pageNode = DOM.findPage(page.id);
    pageNode && pageNode.removeEventListener('click', this.onClickArea.bind(this));
  }

  replacePageHtml(html) {
    const {page} = this.props;
    const pageNode = DOM.findPage(page.id);
    if (!pageNode) return;
    pageNode.innerHTML = html;
  }

  onClickArea() {
    const {page, toggleEditMode} = this.props;
    this.setState({
      editMode: true,
      content: page.content,
      draftContent: page.content,
    });
    toggleEditMode(true);
    getHistoryInstance().push(`/pages/${page.id}`);
  }

  onChangeArea(val) {
    this.setState({draftContent: val});
  }

  onSave() {
    const {onSave, page, toggleEditMode} = this.props;

    toggleEditMode(false);
    this.setState({editMode: false});
    onSave(page.id, this.state.draftContent);
  }

  onCancel() {
    const {page, toggleEditMode} = this.props;

    this.setState({
      editMode: false,
      draftContent: page.content,
    });
    toggleEditMode(false);
  }

  render() {
    return (
      <div>
        {this.state.editMode &&
        <div>
          <FormGroup>
            <Editor
              value={this.state.draftContent}
              onChange={val => this.onChangeArea(val)}
            />
          </FormGroup>

          <FormGroup>
            <Button onClick={() => this.onCancel()} color="link">Cancel</Button>
            <Button onClick={() => this.onSave()} color="primary">Save</Button>
          </FormGroup>
        </div>
        }
      </div>
    );
  }
}
