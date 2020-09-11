import React from 'react';
import {Button, FormGroup, Label, Input} from 'reactstrap';
import classnames from 'classnames';
import Editor from "../../../../../common/components/Editor";
import {BlockState} from "../reducers/State";
import {CONTENT_MODES, DEFAULT_CONTENT_MODE_ID} from "../../../constants";
import {addContentMarker} from "../../../utils";
import marked from "marked";
import reactHtmlParser from 'react-html-parser';

interface Props {
  block: BlockState;
  onSave: (blockId, html) => void;
}

// custom event to reinitialize site plugins on editing content
const pluginInitEvent = new Event("plugins:init");

export class Block extends React.Component<Props, any> {

  constructor(props) {
    super(props);
    this.state = {
      editMode: false,
      draftContent: "",
      contentMode: DEFAULT_CONTENT_MODE_ID,
    };
  }

  componentDidMount() {
    const {block} = this.props;

    document.dispatchEvent(pluginInitEvent);

    this.setState({
      contentMode: block.contentMode || DEFAULT_CONTENT_MODE_ID,
    });
  }

  onClickArea(e) {
    e.preventDefault();

    const {block} = this.props;

    this.setState({
      editMode: true,
      draftContent: block.content || "",
    });
  }

  onChangeArea(val) {
    this.setState({
      draftContent: val,
    });
  }

  onSave() {
    const {onSave, block} = this.props;
    const {draftContent, contentMode} = this.state;
    const newContent = addContentMarker(draftContent, contentMode);

    this.setState({
      editMode: false,
    });

    onSave(block.id, newContent);
  }

  onCancel() {
    const {block} = this.props;

    this.setState({
      editMode: false,
      draftContent: block.content || "",
    });
  }

  componentDidUpdate() {
    const {editMode} = this.state;

    if (!editMode && this.props.block.content) {
      document.dispatchEvent(pluginInitEvent);
    }
  }

  onContentModeChange(e) {
    const v = e.target.value;
    this.setState({contentMode: v});
  }

  render() {
    const {block} = this.props;
    const {editMode, contentMode} = this.state;

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
                <div className="row">
                    <div className="col-md-4 col-lg-3">
                        <Label htmlFor="contentMode">Content mode</Label>
                        <Input
                            type="select"
                            name="contentMode"
                            id="contentMode"
                            placeholder="Content mode"
                            value={contentMode}
                            onChange={e => this.onContentModeChange(e)}
                        >
                          {CONTENT_MODES.map(mode => (
                            <option key={mode.id} value={mode.id}>{mode.title}</option>
                          ))}
                        </Input>
                    </div>
                </div>
            </FormGroup>

            <FormGroup>
              <Button onClick={() => this.onCancel()} color="link">Cancel</Button>
              <Button onClick={() => this.onSave()} color="primary">Save</Button>
            </FormGroup>
          </div>

        }

        <div onClick={e => this.onClickArea(e)}>
          {!editMode &&
            <div className={classnames("editor-area", {'editor-area--empty': !block.content})}>
              {contentMode === "md" ? reactHtmlParser(marked(block.content)) : block.content}
            </div>
          }
        </div>

      </div>
    );
  }
}
