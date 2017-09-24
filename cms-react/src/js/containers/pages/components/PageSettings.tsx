import React from 'react';
import {Container, Row, Col, Button, Form, FormGroup, Label, Input, FormText} from 'reactstrap';
import {Page} from "../../../model";
import {IconBack} from "../../../common/components/IconBack";

interface Props {
  page: Page;
  onBack: () => void;
  onEdit?: (settings) => void;
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

    if (page[key] !== this.state[key]) {
      onEdit({[key]: this.state[key]});
    }
  }

  render () {
    const {page} = this.props;
    const {title, visible, layout, theme} = this.state;

    const clickRemove = e => {
      e.preventDefault();
    };

    return (
      <div>

        <ul>
          <li>
            <a href="#" onClick={e => this.clickBack(e)}>
              <IconBack text={page.title}/>
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
              <Label check>
                <Input
                  type="checkbox"
                  checked={visible}
                  onChange={e => {this.onChange(e, 'visible'); this.onBlur('visible');}}
                />
                {' '} Visible
              </Label>
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
