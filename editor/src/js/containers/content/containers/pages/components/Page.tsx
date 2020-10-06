import React, { useEffect, useState} from 'react';
import classnames from 'classnames';
import {Button, FormGroup, Input} from 'reactstrap';
import {PageState} from "../reducers/State";
import {DOM} from "../../../../../utils";
import {getHistoryInstance} from "../../../../../history";
import PageService from "../../../../../services/PageService";
import {CONTENT_MODES, DEFAULT_CONTENT_MODE_ID} from "../../../constants";
import {addContentMarker} from "../../../utils";
import MarkdownEditor from "../../../../../common/components/editor/MarkdownEditor";
import Editor from "../../../../../common/components/editor/HtmlEditor";
import marked from "marked";

interface PageProps {
  page: PageState;
  onSave: (id, content) => void;
  openPage: (url) => void;
  toggleEditMode: (flag: boolean) => any;
  clearRenderHtml?: (id: number) => void;
  editMode?: any;
}

const pluginInitEvent = new Event("plugins:init");


export const Page: React.FC<PageProps> = ({
  page,
  openPage,
  toggleEditMode,
  clearRenderHtml,
  editMode,
  onSave
}) => {
  const [editModeInner, setEditModeInner] = useState(false);
  const [draftContent, setDraftContent] = useState('');
  const [contentMode, setContentMode] = useState(DEFAULT_CONTENT_MODE_ID);

  const onClickArea = (e) => {
    e.preventDefault();
    setEditModeInner(false);
    toggleEditMode(true);
    getHistoryInstance().push(`/page/${page.id}`);
  };

  useEffect(() => {
    document.dispatchEvent(pluginInitEvent);
    const pageNode = DOM.findPage(page.title);

    toggleEditMode(false);
    setEditModeInner(false);
    setDraftContent(contentMode === "md" ? page.content : marked(page.content || ""));
    setContentMode(page.contentMode || DEFAULT_CONTENT_MODE_ID);

    if (pageNode) {
      pageNode.addEventListener('click', onClickArea);
    } else {
      const defaultPageUrl = page.urls.find(url => url.isDefault);

      const pageUrl = defaultPageUrl ? defaultPageUrl.link : PageService.generateBasetUrl(page).link;

      if (
        !page.urls.map(url => url.link).includes(document.location.pathname)
        && pageUrl !== document.location.pathname
      ) {
        openPage(pageUrl);
      }
    }

    return () => {
      if (pageNode) {
        pageNode.removeEventListener('click', onClickArea);
      }
    }
  },[]);

  const replacePageHtml = html => {
    const pageNode = DOM.findPage(page.title);
    if (!pageNode) return;
    pageNode.innerHTML = html;
  }

  useEffect(( ) => {
    setEditModeInner(false);
    setDraftContent(page.content);
  },[page.id]);

  useEffect(() => {
    if (editModeInner === false && editMode === true) {
      setEditModeInner(true);
    }
  }, [editMode]);

  useEffect(() => {
    if (page.renderHtml) {
      replacePageHtml(page.renderHtml);
      clearRenderHtml(page.id);
    }
  }, [page.renderHtml]);

  useEffect(() => {
    if (!editMode && page.content) {
      document.dispatchEvent(pluginInitEvent);
    }
  },[editMode, page && page.content]);

  const onChangeArea = val => {
    setDraftContent(val);
  }

  const handleSave = () => {
    toggleEditMode(false);
    onSave(page.id, addContentMarker(draftContent, contentMode));
  };

  const handleCancel = () => {
    setEditModeInner(false);
    setDraftContent(page.content);
    toggleEditMode(false);
  }

  const onContentModeChange = e => {
    setContentMode(e.target.value);
  }

  const renderEditor = () => {
    switch (contentMode) {
      case "md": {
        return (
          <MarkdownEditor
            value={draftContent}
            onChange={setDraftContent}
          />
        );
      }
      case "textile":
      case "html":
      default: {
        return (
          <Editor
            value={draftContent}
            onChange={onChangeArea}
            mode={contentMode}
          />
        );
      }
    }
  };

  return (
    <div>
      {editMode && <>
        <div className={
          classnames({"editor-wrapper" : true, "ace-wrapper": contentMode === "html" || contentMode === "textile"})
        }>
          <div className="content-mode-wrapper">
            <Input
              type="select"
              name="contentMode"
              id="contentMode"
              className="content-mode"
              placeholder="Content mode"
              value={contentMode}
              onChange={e => onContentModeChange(e)}
            >
              {CONTENT_MODES.map(mode => (
                <option key={mode.id} value={mode.id}>{mode.title}</option>
              ))}
            </Input>
          </div>
          {renderEditor()}
        </div>
        <div className="mt-4">
          <FormGroup>
            <Button onClick={handleCancel} color="link">Cancel</Button>
            <Button onClick={handleSave} color="primary">Save</Button>
          </FormGroup>
        </div>
      </>}
    </div>
  );
}
