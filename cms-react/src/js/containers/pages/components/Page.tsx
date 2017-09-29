import React from 'react';
import {Container, Row, Col, Button, FormGroup} from 'reactstrap';
import {Page as PageModel} from "../../../model";
import {Editor} from "../../../common/components/Editor";

interface PageProps {
  page: PageModel;
  onSave: (pageId, html) => void;
}


export class Page extends React.Component<PageProps, any> {

  constructor(props) {
    super(props);
    this.state = {
      editMode: false,
      html: '',
      draftHtml: '',
    };
  }

  onClickArea(e) {
    this.setState({
      editMode: true,
      html: e.currentTarget.querySelector('.editor-area').innerHTML,
      draftHtml: e.currentTarget.querySelector('.editor-area').innerHTML,
    });
  }

  onChangeArea(val) {
    this.setState({draftHtml: val});
  }

  onSave() {
    const {onSave, page} = this.props;
    this.setState({editMode: false});
    onSave(page.id, this.state.draftHtml);
  }

  onCancel() {
    const {page} = this.props;

    this.setState({
      editMode: false,
      draftHtml: page.html,
    });
  }

  render() {
    const {page} = this.props;

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

        <div onClick={e => this.onClickArea(e)}>
          {!this.state.editMode &&
            <div className="editor-area" dangerouslySetInnerHTML={{__html: page.html}} />
          }
        </div>

      </div>
    );
  }
}
