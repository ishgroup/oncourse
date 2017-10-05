import React from 'react';
import {Container, Row, Col, Button, Form, FormGroup, Label, Input, FormText} from 'reactstrap';
import {Checkbox} from "../../../common/components/Checkbox";
import {IconBack} from "../../../common/components/IconBack";
import {Page} from "../../../model";

interface Props {
  page: Page;
  onBack: () => void;
  onEdit?: (settings) => void;
  onDelete?: (id) => void;
}

export class PageSettings extends React.Component<Props, any> {

  constructor(props) {
    super(props);

    this.state = {
      title: props.page.title,
      layout: props.page.layout,
      visible: props.page.visible,
      theme: props.page.theme,
    };
  }

  clickBack(e) {
    const {onBack} = this.props;
    e.preventDefault();
    onBack();
  }

  onChange(event, key) {
    const value = event.target.type === 'checkbox' ? event.target.checked : event.target.value;
    this.setState({
      [key]: value,
    });
  }

  onBlur(key) {
    const {onEdit, page} = this.props;

    if (page[key] !== this.state[key] || key === 'visible') {
      onEdit({[key]: this.state[key]});
    }
  }

  render () {
    const {page, onDelete} = this.props;
    const {title, visible, layout, theme} = this.state;

    const clickRemove = e => {
      e.preventDefault();

      if (confirm('Are you sure?')) {
        onDelete(page.id);
      }
    };

    return (
      <div>

        <ul>
          <li>
            <a href="#" onClick={e => this.clickBack(e)}>
              <IconBack text={page.title || 'New Page'}/>
            </a>
          </li>
        </ul>

        <div className="sidebar__settings">
          <Form>
            <FormGroup>
              <Label for="pageTitle">Title</Label>
              <Input
                type="text"
                name="pageTitle"
                id="pageTitle"
                placeholder="Page title"
                value={title}
                onChange={e => this.onChange(e, 'title')}
                onBlur={e => this.onBlur('title')}
              />
            </FormGroup>

            <FormGroup>
              <Label htmlFor="pageTheme">Theme</Label>
              <Input
                type="text"
                name="pageTheme"
                id="pageTheme"
                placeholder="Page theme"
                value={theme}
                onChange={e => this.onChange(e, 'theme')}
                onBlur={e => this.onBlur('theme')}
              />
            </FormGroup>

            <FormGroup>
              <Checkbox
                label="Visible"
                name="visible"
                checked={visible}
                onChange={e => {this.onChange(e, 'visible'); this.onBlur('visible');}}
              />
            </FormGroup>

            <FormGroup>
              <Label htmlFor="pageLayout">Layout</Label>
              <Input
                type="text"
                name="pageLayout"
                id="pageLayout"
                placeholder="Page layout"
                value={layout}
                onChange={e => this.onChange(e, 'layout')}
                onBlur={e => this.onBlur('layout')}
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
