import React from 'react';
import {Button, Form, FormGroup, Label, Input} from 'reactstrap';
import {Theme, Layout} from "../../../../../model";
import {IconBack} from "../../../../../common/components/IconBack";

interface Props {
  theme: Theme;
  themes: Theme[];
  layouts: Layout[];
  onBack: () => void;
  onEdit?: (settings) => void;
  showError?: (title) => any;
  onDelete?: (title) => void;
  showModal?: (props) => void;
}

export class ThemeSettings extends React.Component<Props, any> {
  constructor(props) {
    super(props);

    this.state = {
      layoutId: props.theme.layoutId,
      newLink: '',
      title: props.theme.title,
      urls: [],
    };
  }

  componentDidMount() {
    const {theme} = this.props;
    if (theme.paths) this.setState({urls: theme.paths});
  }

  clickBack(e) {
    const {onBack} = this.props;
    e.preventDefault();
    onBack();
  }

  formatLink(link) {
    return (link.indexOf('/') !== 0 ? `/${link}` : link).replace(/ /g, '');
  }

  onAddNewUrl() {
    const newLink = this.formatLink(this.state.newLink);
    const {showError, themes} = this.props;

    if (!this.state.newLink) return;

    const isUsedUrl = themes.some((elem: Theme) => elem.paths
      && elem.paths.some((elem: string) => elem === newLink))
      || this.state.urls.some((elem: string) => elem === newLink);

    if (isUsedUrl) {
      showError('This url already exist');
      return;
    }

    const urls = this.state.urls.concat(newLink);
    this.setState({
      urls,
      newLink: '',
    });
  }

  onChange(event, key) {
    this.setState({
      [key]: event.target.value,
    });
  }

  onDeleteUrl(url) {
    const urls = this.state.urls.filter(item => item !== url);
    this.setState({urls});
  }

  onSave() {
    const {onEdit} = this.props;

    onEdit({
      title: this.state.title,
      layoutId: this.state.layoutId,
      paths: this.state.urls,
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
    const {theme, layouts} = this.props;
    const {layoutId, newLink, title, urls} = this.state;

    return (
      <div>
        <ul>
          <li>
            <a href="javascript:void(0)" onClick={e => this.clickBack(e)}>
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
              />
            </FormGroup>

            <FormGroup>
              <Label for="themeLayout">Layout</Label>
              <Input
                type="select"
                name="themeLayout"
                id="themeLayout"
                placeholder="Theme layout"
                value={layoutId}
                onChange={e => this.onChange(e, 'layoutId')}
              >
                {layouts.map(layout => (
                  <option key={layout.id} value={layout.id}>{layout.title}</option>
                ))}
              </Input>
            </FormGroup>

            <FormGroup>
              <Label htmlFor="pageUrl">Pages</Label>
              <div className="links">

                {urls.map((url, index) => (
                  <div className="links__item" key={index}>
                    <div
                      title={url}
                    >
                      {url}
                    </div>

                    <span
                      className="links__remove icon-close"
                      onClick={() => this.onDeleteUrl(url)}
                    />
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
