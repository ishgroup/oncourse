import React from 'react';
import {Button, Form, FormGroup, Label, Input} from 'reactstrap';
import classnames from 'classnames';
import {Checkbox} from "../../../../../common/components/Checkbox";
import {IconBack} from "../../../../../common/components/IconBack";
import PageService from "../../../../../services/PageService";
import {addContentMarker} from "../../../utils";
import {PageState} from "../reducers/State";

interface Props {
  page: PageState;
  pages: PageState[];
  onBack: () => void;
  onEdit?: (settings) => void;
  onDelete?: (id) => void;
  showModal?: (props) => any;
  showError?: (title) => any;
  themes?: any;
}

export class PageSettings extends React.PureComponent<Props, any> {

  constructor(props) {
    super(props);

    this.state = {
      title: props.page.title,
      urls: props.page.urls,
      visible: props.page.visible,
      themeId: props.page.themeId,
      newLink: '',
      suppressOnSitemap: props.page.suppressOnSitemap,
    };
  }

  componentWillReceiveProps(props) {
    if (props.page.id !== this.props.page.id) {
      this.setState({
        title: props.page.title,
        urls: props.page.urls,
        visible: props.page.visible,
        themeId: props.page.themeId,
        newLink: '',
        suppressOnSitemap: props.page.suppressOnSitemap,
      });
    }
  }

  clickBack(e) {
    const {onBack} = this.props;
    e.preventDefault();
    onBack();
  }

  onSave = () => {
    const {onEdit, page} = this.props;

    onEdit({
      title: this.state.title,
      urls: this.state.urls,
      visible: this.state.visible,
      themeId: this.state.themeId,
      suppressOnSitemap: this.state.suppressOnSitemap,
      content: addContentMarker(page.content, page.contentMode)
    });
  }

  onChange(event, key) {
    const value = event.target.type === 'checkbox' ? event.target.checked : event.target.value;
    this.setState({
      [key]: value,
    });
  }

  onSetDefaultUrl(url) {
    const urls = this.state.urls
      .map(item => item.link === url.link ? {...item, isDefault: true} : {...item, isDefault: false});

    this.setState({urls});
  }

  onDeleteUrl(url) {
    const urls = this.state.urls.filter(item => item.link !== url.link);
    this.setState({urls});
  }

  onClickDelete = (e) => {
    e.preventDefault();
    const {onDelete, page, showModal} = this.props;

    showModal({
      text: `You are want to delete page '${page.title}'. Are you sure?`,
      onConfirm: () => onDelete(page.id),
    });
  }

  onAddNewUrl() {
    const newLink = this.formatLink(this.state.newLink);
    const {pages, page, showError} = this.props;
    const actualPages = pages.map(p => p.id === page.id ? {...p, urls: this.state.urls} : p);

    if (!this.state.newLink) return;
    if (!PageService.isValidPageUrl(newLink, actualPages)) {
      showError('This url already exist');
      return;
    }

    const newUrl = {
      link: newLink,
      isDefault: false,
    };

    const urls = this.state.urls.concat(newUrl);
    this.setState({
      urls,
      newLink: '',
    });
  }

  formatLink(link) {
    return (link.indexOf('/') !== 0 ? `/${link}` : link).replace(/ /g, '');
  }

  render () {
    const {page, themes} = this.props;
    const {title, visible, themeId, urls, newLink, suppressOnSitemap} = this.state;
    const defaultPageUrl = PageService.generateBasetUrl(page);

    return (
      <div>
        <ul>
          <li>
            <a href="javascript:void(0)" onClick={e => this.clickBack(e)}>
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
              />
            </FormGroup>

            <FormGroup>
              <Label htmlFor="pageUrl">Page Links (URLs)</Label>

              <div className="links">

                <div className="links__item">
                  <div
                    onClick={() => urls.find(url => url.isDefault) && this.onSetDefaultUrl(defaultPageUrl)}
                    className={classnames("links__title links__title--base", {
                      "links__title--default": !urls.find(url => url.isDefault),
                    })}
                    title={defaultPageUrl.link}
                  >
                    {defaultPageUrl.link}
                  </div>
                </div>


                {urls.map((url, index) => (
                  <div className="links__item" key={index}>
                    <div
                      onClick={() => !url.isDefault && this.onSetDefaultUrl(url)}
                      className={classnames("links__title", {"links__title--default": url.isDefault})}
                      title={url.link}
                    >
                      {url.link}
                    </div>

                    {!url.isDefault &&
                      <span
                        className="links__remove icon-close"
                        onClick={() => !url.isDefault && this.onDeleteUrl(url)}
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

            {/*<FormGroup>*/}
            {/*  <Label htmlFor="pageTheme">Theme</Label>*/}
            {/*  <Input*/}
            {/*    type="select"*/}
            {/*    name="pageTheme"*/}
            {/*    id="pageTheme"*/}
            {/*    placeholder="Page theme"*/}
            {/*    value={themeId}*/}
            {/*    onChange={e => this.onChange(e, 'themeId')}*/}
            {/*  >*/}
            {/*    {themes.map(theme => (*/}
            {/*      <option key={theme.id} value={theme.id}>{theme.title}</option>*/}
            {/*    ))}*/}
            {/*  </Input>*/}
            {/*</FormGroup>*/}

            <FormGroup>
              <Checkbox
                label="Visible"
                name="visible"
                checked={visible}
                onChange={e => {this.onChange(e, 'visible');}}
              />
            </FormGroup>

            <FormGroup>
              <Checkbox
                label="Hide from sitemap"
                name="suppressOnSitemap"
                checked={suppressOnSitemap}
                onChange={e => {this.onChange(e, 'suppressOnSitemap');}}
              />
            </FormGroup>

            <FormGroup className="actions-group">
              <div className="buttons-inline">
                <Button
                  color="danger"
                  className="outline"
                  onClick={this.onClickDelete}
                >
                  <span className="icon icon-delete"/>
                  Remove
                </Button>

                <Button
                  color="primary"
                  onClick={this.onSave}
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
