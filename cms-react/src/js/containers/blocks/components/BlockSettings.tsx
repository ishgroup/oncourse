import React from 'react';
import {Container, Row, Col, Button, Form, FormGroup, Label, Input, FormText} from 'reactstrap';
import {Block} from "../../../model";
import {IconBack} from "../../../common/components/IconBack";

interface Props {
  block: Block;
  onBack: () => void;
  onEdit?: (settings) => void;
  onDelete?: (id) => void;
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

  onBlur(key) {
    const {onEdit, block} = this.props;

    if (block[key] !== this.state[key]) {
      onEdit({[key]: this.state[key]});
    }
  }

  render () {
    const {block, onDelete} = this.props;
    const {title} = this.state;

    const clickRemove = e => {
      e.preventDefault();

      if (confirm('Are you sure?')) {
        onDelete(block.id);
      }
    };

    return (
      <div>

        <ul>
          <li>
            <a href="#" onClick={e => this.clickBack(e)}>
              <IconBack text={block.title}/>
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

            <FormGroup>
              <Button color="danger" onClick={e => clickRemove(e)}>
                <span className="icon icon-delete"/>
                Remove
              </Button>
            </FormGroup>
          </Form>
        </div>

      </div>
    );
  }
}
