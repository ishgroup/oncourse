import React from 'react';
import {Container, Row, Col, Button, FormGroup} from 'reactstrap';
import {Block as BlockModel} from "../../../model";
import {Editor} from "../../../common/components/Editor";

interface Props {
  block: BlockModel;
  onSave: (blockId, html) => void;
}

export class Block extends React.Component<Props, any> {

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
  }

  render() {
    const {block} = this.props;

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
            <div className="editor-area" dangerouslySetInnerHTML={{__html: block.html}} />
          }
        </div>

      </div>
    );
  }
}
