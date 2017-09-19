import React from 'react';
import {Container, Row, Col, Button, Form, FormGroup, Label, Input, FormText} from 'reactstrap';
import {Page} from "../../../model";
import {IconBack} from "../../../common/components/IconBack";

interface Props {
  page: Page;
  onBack: () => void;
  onEdit?: (settings) => void;
}

export const PageSettings = (props: Props) => {
  const {page, onBack, onEdit} = props;

  const clickBack = e => {
    e.preventDefault();
    onBack();
  };

  const onChange = (event, key) => {
    const value = event.target.type === 'checkbox' ? event.target.checked : event.target.value;
    onEdit({[key]: value});
  };

  const clickRemove = e => {
    e.preventDefault();
  };

  return (
    <div>

      <ul>
        <li>
          <a href="#" onClick={e => clickBack(e)}>
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
              value={page.title}
              onChange={e => onChange(e, 'title')}
            />
          </FormGroup>

          <FormGroup>
            <Label htmlFor="pageTheme">Theme</Label>
            <Input
              type="text"
              name="pageTheme"
              id="pageTheme"
              placeholder="Page theme"
              value={page.theme}
              onChange={e => onChange(e, 'theme')}
            />
          </FormGroup>

          <FormGroup>
            <Label check>
              <Input type="checkbox" checked={page.visible} onChange={e => onChange(e, 'visible')}/>{' '}
              Visible
            </Label>
          </FormGroup>

          <FormGroup>
            <Label htmlFor="pageLayout">Layout</Label>
            <Input
              type="text"
              name="pageLayout"
              id="pageLayout"
              placeholder="Page layout"
              value={page.layout}
              onChange={e => onChange(e, 'layout')}
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
};
