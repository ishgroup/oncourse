import React from 'react';
import {Container, Row, Col, Button, Form, FormGroup, Label, Input, FormText} from 'reactstrap';
import {Block} from "../../../../../model";
import {IconBack} from "../../../../../common/components/IconBack";

interface Props {
  block: Block;
  onBack: () => void;
  onEdit?: (settings) => void;
  onDelete?: (id) => void;
  showModal?: (props) => any;
}

export class BlockSettings extends React.Component<Props, any> {

  constructor(props) {
    super(props);

    this.state = {
      title: props.block.title,
    };
  }

  clickBack(e) {
    const {onBack} = this.props;
    e.preventDefault();
    onBack();
  }

  onChange(event, key) {
    this.setState({
      [key]: event.target.value,
    });
  }

  onSave() {
    const {onEdit} = this.props;

    onEdit({
      title: this.state.title,
    });
  }

  onBlur(key) {

  }

  onClickDelete(e) {
    e.preventDefault();
    const {onDelete, block, showModal} = this.props;

    showModal({
      text: `You are want to delete block '${block.title}'. Are you sure?`,
      onConfirm: () => onDelete(block.id),
    });
  }

  render () {
    const {block} = this.props;
    const {title} = this.state;

    return (
      <div>

        <ul>
          <li>
            <a href="#" onClick={e => this.clickBack(e)}>
              <IconBack text={block.title || 'New Block'}/>
            </a>
          </li>
        </ul>

        <div className="sidebar__settings">
          <Form>
            <FormGroup>
              <Label for="blockTitle">Title</Label>
              <Input
                type="text"
                name="blockTitle"
                id="blockTitle"
                placeholder="Block title"
                value={title}
                onChange={e => this.onChange(e, 'title')}
                onBlur={e => this.onBlur('title')}
              />
            </FormGroup>

            <FormGroup className="actions-group">
              <div className="buttons-inline">
                <Button
                  color="danger"
                  className="outline"
                  onClick={e => this.onClickDelete(e)}
                >
                  <span className="icon icon-delete"/>
                  Remove
                </Button>

                <Button
                  color="primary"
                  onClick={e => this.onSave()}
                >
                  Save
                </Button>
              </div>
            </FormGroup>
          </Form>
        </div>

      </div>
    );
  }
}
