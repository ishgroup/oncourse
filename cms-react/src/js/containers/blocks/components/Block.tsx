import React from 'react';
import {Container, Row, Col, Button, FormGroup} from 'reactstrap';
import {Block as BlockModel} from "../../../model";

interface Props {
  block: BlockModel;
  onSave: (blockId, html) => void;
}

export class Block extends React.Component<Props, any> {
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
    const {onSave, block} = this.props;
    this.setState({editMode: false});
    onSave(block.id, this.state.draftHtml);
  }

  onCancel() {
    const {block} = this.props;

    this.setState({
      editMode: false,
      draftHtml: block.html,
    });

    setTimeout(() => this.initEventsHandler(), 300);
  }

  render() {
    const {block} = this.props;

    return (
      <div className="content-white">
        {this.state.editMode &&
          <div>
            <FormGroup>
                <textarea
                  className="form-control"
                  name="block-editor"
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
            <div className="editor-area">
              <div dangerouslySetInnerHTML={{__html: block.html}} />
            </div>
          }
        </div>

      </div>
    );
  }
}
