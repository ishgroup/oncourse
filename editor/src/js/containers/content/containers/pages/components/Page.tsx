import React from 'react';
import {Button, FormGroup, Label, Input} from 'reactstrap';
import {PageState} from "../reducers/State";
import Editor from "../../../../../common/components/Editor";
import {DOM} from "../../../../../utils";
import {getHistoryInstance} from "../../../../../history";
import PageService from "../../../../../services/PageService";
import {CONTENT_MODES, DEFAULT_CONTENT_MODE_ID} from "../../../constants";
import {addContentMarker} from "../../../utils";

interface PageProps {
  page: PageState;
  onSave: (id, content) => void;
  openPage: (url) => void;
  toggleEditMode: (flag: boolean) => any;
  clearRenderHtml?: (id: number) => void;
  editMode?: any;
}

export class Page extends React.PureComponent<PageProps, any> {

  constructor(props) {
    super(props);
    this.state = {
      editMode: false,
      content: '',
      draftContent: '',
      contentMode: DEFAULT_CONTENT_MODE_ID,
    };
  }

  componentDidMount() {
    const {page, openPage, toggleEditMode} = this.props;
    const pageNode = DOM.findPage(page.title);

    toggleEditMode(false);

    this.setState({
      editMode: false,
      content: page.content,
      draftContent: page.content,
      contentMode: page.contentMode || DEFAULT_CONTENT_MODE_ID,
    });

    if (pageNode) {
      pageNode.addEventListener('click', this.onClickArea.bind(this));
      return;
    }

    const pageUrl = this.getPageDefaultUrl();

    if (
      !page.urls.map(url => url.link).includes(document.location.pathname)
      && pageUrl !== document.location.pathname
    ) {
      openPage(pageUrl);
    }
  }

  componentWillReceiveProps(props) {
    const {clearRenderHtml, page, toggleEditMode, editMode} = this.props;

    if (props.page.id !== this.props.page.id) {
      toggleEditMode(true);

      this.setState({
        editMode: true,
        content: props.page.content,
        draftContent: props.page.content,
      });
    }

    if (editMode === false && props.editMode === true) {
      this.setState({
        editMode: true,
      });
    }

    if (props.page.renderHtml && props.page.renderHtml !== this.props.page.renderHtml) {
      this.replacePageHtml(props.page.renderHtml);
      clearRenderHtml(page.id);
    }
  }

  componentWillUnmount() {
    const {page} = this.props;
    const pageNode = DOM.findPage(page.title);
    pageNode && pageNode.removeEventListener('click', this.onClickArea.bind(this));
  }

  getPageDefaultUrl = (): string => {
    const {page} = this.props;
    const defaultPageUrl = page.urls.find(url => url.isDefault);

    return defaultPageUrl ? defaultPageUrl.link : PageService.generateBasetUrl(page).link;
  }

  replacePageHtml(html) {
    const {page} = this.props;
    const pageNode = DOM.findPage(page.title);
    if (!pageNode) return;
    pageNode.innerHTML = html;
  }

  onClickArea() {
    const {page, toggleEditMode} = this.props;
    this.setState({
      editMode: true,
      content: page.content,
      draftContent: page.content,
    });
    toggleEditMode(true);
    getHistoryInstance().push(`/page/${page.id}`);
  }

  onChangeArea(val) {
    this.setState({draftContent: val});
  }

  onSave() {
    const {onSave, page, toggleEditMode} = this.props;
    const {draftContent, contentMode} = this.state;
    const newContent = addContentMarker(draftContent, contentMode);

    toggleEditMode(false);
    this.setState({editMode: false});
    onSave(page.id, newContent);
  }

  onCancel() {
    const {page, toggleEditMode} = this.props;

    this.setState({
      editMode: false,
      draftContent: page.content,
    });
    toggleEditMode(false);
  }

  onContentModeChange(e) {
    const v = e.target.value;
    this.setState({contentMode: v});
  }

  render() {
    const {contentMode} = this.state;

    return (
      <div>
        {this.state.editMode &&
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
      </div>
    );
  }
}
