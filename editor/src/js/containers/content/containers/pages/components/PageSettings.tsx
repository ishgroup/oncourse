import React from 'react';
import {Container, Row, Col, Button, Form, FormGroup, Label, Input, FormText} from 'reactstrap';
import classnames from 'classnames';
import {Checkbox} from "../../../../../common/components/Checkbox";
import {IconBack} from "../../../../../common/components/IconBack";
import {Page} from "../../../../../model";

interface Props {
  page: Page;
  onBack: () => void;
  onEdit?: (settings) => void;
  onDelete?: (id) => void;
  showModal?: (props) => any;
}

export class PageSettings extends React.PureComponent<Props, any> {

  constructor(props) {
    super(props);

    this.state = {
      title: props.page.title,
      urls: props.page.urls,
      layout: props.page.layout,
      visible: props.page.visible,
      theme: props.page.theme,
      newLink: '',
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

  onSetDefaultUrl(url) {
    const {onEdit} = this.props;
    const urls = this.state.urls
      .map(item => item.link === url.link ? {...item, isDefault: true} : {...item, isDefault: false});

    this.setState({urls});
    onEdit({urls});
  }

  onDeleteUrl(url) {
    const {onEdit} = this.props;
    const urls = this.state.urls.filter(item => item.link !== url.link);

    this.setState({urls});
    onEdit({urls});
  }

  onClickDelete(e) {
    e.preventDefault();
    const {onDelete, page, showModal} = this.props;

    showModal({
      text: `You are want to delete page '${page.title}'. Are you sure?`,
      onConfirm: () => onDelete(page.id),
    });
  }

  onAddNewUrl() {
    const {onEdit} = this.props;
    const newLink = this.formatLink(this.state.newLink);

    if (!this.state.newLink || !newLink || this.state.urls.find(i => i.link === newLink)) {
      return;
    }

    const newUrl = {
      link: newLink,
      isDefault: false,
      isBase: false,
    };

    const urls = this.state.urls.concat(newUrl);
    this.setState({
      urls,
      newLink: '',
    });
    onEdit({urls});
  }

  formatLink(link) {
    return (link.indexOf('/') !== 0 ? `/${link}` : link).replace(/ /g, '');
  }

  render () {
    const {page} = this.props;
    const {title, visible, layout, theme, urls, newLink} = this.state;

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
              <Label htmlFor="pageUrl">Page Links (URLs)</Label>

              <div className="links">
                {urls.map((url, index) => (
                  <div className="links__item" key={index}>
                    <div
                      onClick={() => !url.isDefault && this.onSetDefaultUrl(url)}
                      className={classnames("links__title", {
                        "links__title--default": url.isDefault,
                        "links__title--base": url.isBase,
                      })}
                    >
                      {url.link}
                    </div>

                    {!url.isBase && !url.isDefault &&
                      <span
                        className="links__remove icon-close"
                        onClick={() => !url.isDefault && !url.isBase && this.onDeleteUrl(url)}
                      />
                    }
                  </div>
                ))}
              </div>

              <div className="input-icon">
                <Input
                  type="text"
                  name="newLink"
                  id="newLink"
                  placeholder="New Page Url"
                  value={newLink}
                  onChange={e => this.onChange(e, 'newLink')}
                  onKeyDown={e => e.key === 'Enter' && this.onAddNewUrl()}
                />
                <span className="icon icon-add btn-icon-add" onClick={() => this.onAddNewUrl()}/>
              </div>

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
              <Button
                color="danger"
                className="outline"
                onClick={e => this.onClickDelete(e)}
              >
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
