import React from 'react';
import {Container, Row, Col, Button, FormGroup} from 'reactstrap';
import classnames from 'classnames';
import {Editor} from "../../../../../common/components/Editor";
import {BlockState} from "../reducers/State";

interface Props {
  block: BlockState;
  onSave: (blockId, html) => void;
}

export class Block extends React.Component<Props, any> {

  constructor(props) {
    super(props);
    this.state = {
      editMode: false,
      content: '',
      draftContent: '',
    };
  }

  onClickArea(e) {
    this.setState({
      editMode: true,
      content: e.currentTarget.querySelector('.editor-area').innerHTML,
      draftContent: e.currentTarget.querySelector('.editor-area').innerHTML,
    });
  }

  onChangeArea(val) {
    this.setState({draftContent: val});
  }

  onSave() {
    const {onSave, block} = this.props;
    this.setState({editMode: false});
    onSave(block.id, this.state.draftContent);
  }

  onCancel() {
    const {block} = this.props;

    this.setState({
      editMode: false,
      draftContent: block.content,
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

        <div onClick={e => this.onClickArea(e)}>
          {!this.state.editMode &&
            <div
              className={classnames("editor-area", {'editor-area--empty': !block.content})}
              dangerouslySetInnerHTML={{__html: block.content}}
            />
          }
        </div>

      </div>
    );
  }
}
