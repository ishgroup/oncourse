import React from 'react';
import {Container, Row, Col, Button, FormGroup} from 'reactstrap';
import classnames from 'classnames';
import {Editor} from "../../../../../common/components/Editor";
import {BlockState} from "../reducers/State";

interface Props {
  block: BlockState;
  onSave: (blockId, html) => void;
}

//custom event to reinitialize site plugins on editing content
const pluginInitEvent = new Event("plugins:init");

export class Block extends React.Component<Props, any> {

  constructor(props) {
    super(props);
    this.state = {
      editMode: false,
      draftContent: ''
    };
  }

  componentDidMount() {
    document.dispatchEvent(pluginInitEvent);
  }

  onClickArea() {
    const {block} = this.props;

    this.setState({
      editMode: true,
      draftContent: block.content
    });
  }

  onChangeArea(val) {
    this.setState({
      draftContent: val,
    });
  }

  onSave() {
    const {onSave, block} = this.props;
    this.setState({
      editMode: false
    });
    onSave(block.id, this.state.draftContent);
  }

  onCancel() {
    const {block} = this.props;

    this.setState({
      editMode: false,
      draftContent: block.content
    });
  }

  componentDidUpdate() {
    const { editMode } = this.state;

    if(!editMode && this.props.block.content) {
      document.dispatchEvent(pluginInitEvent);
    }
  }

  render() {
    const { block } = this.props;
    const { editMode } = this.state;

      return (
      <div>
        {editMode &&
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

        <div onClick={() => this.onClickArea()}>
          {!editMode &&
            <div
              className={classnames("editor-area", {'editor-area--empty': !block.content})}
              dangerouslySetInnerHTML={{__html: block.content }}
            />
          }
        </div>

      </div>
    );
  }
}
