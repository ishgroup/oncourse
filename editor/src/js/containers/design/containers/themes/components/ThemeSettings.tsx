import React from 'react';
import {Container, Row, Col, Button, Form, FormGroup, Label, Input, FormText} from 'reactstrap';
import {Theme} from "../../../../../model";
import {IconBack} from "../../../../../common/components/IconBack";

interface Props {
  theme: Theme;
  onBack: () => void;
  onEdit?: (settings) => void;
  onDelete?: (id) => void;
  showModal?: (props) => void;
}

export class ThemeSettings extends React.Component<Props, any> {

  constructor(props) {
    super(props);

    this.state = {
      title: props.theme.title,
      layout: props.theme.layout,
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

  }

  onSave() {
    const {onEdit} = this.props;

    onEdit({
      title: this.state.title,
      layout: this.state.layout,
    });
  }

  onClickDelete(e) {
    e.preventDefault();
    const {onDelete, theme, showModal} = this.props;

    showModal({
      text: `You are want to delete theme '${theme.title}'. Are you sure?`,
      onConfirm: () => onDelete(theme.id),
    });
  }

  render () {
    const {theme} = this.props;
    const {title, layout} = this.state;

    return (
      <div>

        <ul>
          <li>
            <a href="#" onClick={e => this.clickBack(e)}>
              <IconBack text={theme.title}/>
            </a>
          </li>
        </ul>

        <div className="sidebar__settings">
          <Form>
            <FormGroup>
              <Label for="themeTitle">Title</Label>
              <Input
                type="text"
                name="themeTitle"
                id="themeTitle"
                placeholder="Theme title"
                value={title}
                onChange={e => this.onChange(e, 'title')}
                onBlur={e => this.onBlur('title')}
              />
            </FormGroup>

            <FormGroup>
              <Label for="themeLayout">Layout</Label>
              <Input
                type="text"
                name="themeLayout"
                id="themeLayout"
                placeholder="Theme layout"
                value={layout}
                onChange={e => this.onChange(e, 'layout')}
                onBlur={e => this.onBlur('layout')}
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
