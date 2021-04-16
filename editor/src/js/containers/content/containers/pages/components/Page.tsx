import React, {useEffect, useState} from 'react';
import clsx from "clsx";
import {PageState} from "../reducers/State";
import {DOM} from "../../../../../utils";
import {getHistoryInstance} from "../../../../../history";
import PageService from "../../../../../services/PageService";
import {addContentMarker} from "../../../utils";
import MarkdownEditor from "../../../../../common/components/editor/MarkdownEditor";
import Editor from "../../../../../common/components/editor/HtmlEditor";
import marked from "marked";
import {ContentMode} from "../../../../../model";
import ContentModeSwitch from "../../../../../common/components/ContentModeSwitch";
import CustomButton from "../../../../../common/components/CustomButton";

interface PageProps {
  page: PageState;
  onSave: (id, content) => void;
  openPage: (url) => void;
  toggleEditMode: (flag: boolean) => any;
  clearRenderHtml?: (id: number) => void;
  setContentMode?: (id: number, contentMode: ContentMode) => any;
  editMode?: any;
}

const pluginInitEvent = new Event("plugins:init");

export const Page: React.FC<PageProps> = ({
  page,
  openPage,
  toggleEditMode,
  clearRenderHtml,
  editMode,
  onSave,
  setContentMode,
}) => {
  const [editModeInner, setEditModeInner] = useState(false);
  const [draftContent, setDraftContent] = useState('');

  const onClickArea = e => {
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
    setDraftContent(page.contentMode === "md" ? page.content : marked(page.content || ""));

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
    };
  },        []);

  const replacePageHtml = html => {
    const pageNode = DOM.findPage(page.title);
    if (!pageNode) return;
    pageNode.innerHTML = html;
  };

  useEffect(( ) => {
    setEditModeInner(false);
    setDraftContent(page.content);
  },        [page.id]);

  useEffect(() => {
    if (editModeInner === false && editMode === true) {
      setEditModeInner(true);
    }
  },        [editMode]);

  useEffect(() => {
    if (page.renderHtml) {
      replacePageHtml(page.renderHtml);
      clearRenderHtml(page.id);
    }
  },        [page.renderHtml]);

  useEffect(() => {
    if (!editMode && page.content) {
      document.dispatchEvent(pluginInitEvent);
    }
  },        [editMode, page && page.content]);

  const onChangeArea = val => {
    setDraftContent(val);
  };

  const handleSave = () => {
    toggleEditMode(false);
    onSave(page.id, addContentMarker(draftContent, page.contentMode));
  };

  const handleCancel = () => {
    setEditModeInner(false);
    setDraftContent(page.content);
    toggleEditMode(false);
  };

  const renderEditor = () => {
    switch (page.contentMode) {
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
            mode={page.contentMode}
          />
        );
      }
    }
  };

  return (
    <div>
      {editMode && <>
        <div className={
          clsx("editor-wrapper", (page.contentMode === "html" || page.contentMode === "textile") && "ace-wrapper")
        }>
          <ContentModeSwitch
              contentModeId={page.contentMode}
              moduleId={page.id}
              setContentMode={setContentMode}
          />
          {renderEditor()}
        </div>
        <div className="mt-3">
          <CustomButton onClick={handleCancel} styleType="cancel">Cancel</CustomButton>
          <CustomButton onClick={handleSave} styleType="submit">Save</CustomButton>
        </div>
      </>}
    </div>
  );
};
