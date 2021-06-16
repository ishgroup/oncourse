import React, {useEffect, useState} from 'react';
import clsx from "clsx";
import {createStyles, makeStyles, Paper} from "@material-ui/core";
import marked from "marked";
import {PageState} from "../reducers/State";
import {DOM} from "../../../../../utils";
import {getHistoryInstance} from "../../../../../history";
import PageService from "../../../../../services/PageService";
import {addContentMarker, getEditorSize} from "../../../utils";
import MarkdownEditor from "../../../../../common/components/editor/MarkdownEditor";
import Editor from "../../../../../common/components/editor/HtmlEditor";
import {ContentMode} from "../../../../../model";
import ContentModeSwitch from "../../../../../common/components/ContentModeSwitch";
import CustomButton from "../../../../../common/components/CustomButton";

const blocksType = ["block", "flex", "grid", "table"];

interface PageProps {
  page: PageState;
  onSave: (id, content) => void;
  onSaveBlock: (id, html) => any;
  openPage: (url) => void;
  blocks: any[];
  clearBlockRenderHtml: () => void;
  toggleEditMode: (flag: boolean) => any;
  clearRenderHtml?: (id: number) => void;
  setContentMode?: (id: number, contentMode: ContentMode) => any;
  editMode?: any;
}

const useStyles = makeStyles(theme =>
  createStyles({
    paperWrapper: {
      maxHeight: "100%",
    }
  }),
);

const pluginInitEvent = new Event("plugins:init");

export const Page: React.FC<PageProps> = ({
  page,
  openPage,
  toggleEditMode,
  clearRenderHtml,
  clearBlockRenderHtml,
  editMode,
  onSave,
  onSaveBlock,
  blocks,
  setContentMode,
}) => {
  const [draftContent, setDraftContent] = useState('');
  const [scrollValue, setScrollValue] = useState(0);
  const [blockId, setBlockId] = useState(0);
  const [DOMNode, setDOMNode] = useState(null);
  const [position, setPosition] = useState({
    height: 230,
    width: 570,
    top: 0,
    left: 0,
    bottom: 0,
  })

  const classes = useStyles();

  useEffect(() => {
    return () => toggleEditMode(false);
  }, [])

  useEffect(() => {
    document.addEventListener('scroll', onScroll);
    return () => document.removeEventListener('scroll', onScroll);
  }, [blockId]);

  const onClickArea = (e, pageNode) => {
    e.preventDefault();
    setDOMNode(pageNode);
    setScrollValue(0);
    setBlockId(0);
    setPosition(getEditorSize(pageNode.getBoundingClientRect()));

    setDraftContent(page.contentMode === "html" ? marked(page.content || "") : page.content);
    toggleEditMode(true);
    getHistoryInstance().push(`/page/${page.id}`);
  };

  const onClickBlock = (e, DOMBlock, id) => {
    e.preventDefault();
    const block = blocks.filter(elem => elem.id === id)[0];
    if (!block) return null
    setDOMNode(DOMBlock);

    setScrollValue(0);

    setBlockId(id);
    setPosition(getEditorSize(DOMBlock.getBoundingClientRect()));
    setDraftContent(block.contentMode === "md" ? block.content : marked(block.content || ""));

    toggleEditMode(true);
  }

  const getReverseValue = (height, top) => {
    const windowHeight = window.innerHeight;

    return window.innerHeight - height - top < 0 && height < windowHeight;
  }

  const onScroll = () => {
    const DOMNodeElementData = DOMNode && DOMNode.getBoundingClientRect();

    if (!DOMNodeElementData) return null;

    const reverse = getReverseValue(position.height, DOMNodeElementData.top);

    DOMNodeElementData && reverse ? setScrollValue(DOMNodeElementData.bottom) : setScrollValue(DOMNodeElementData.top);
  }

  useEffect(() => {
    document.dispatchEvent(pluginInitEvent);
    const pageNode = DOM.findPage(page.title);

    if (pageNode) {
      pageNode.addEventListener('click', (e) => onClickArea(e, pageNode));
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
      // toggleEditMode(false);

      if (pageNode) {
        pageNode.removeEventListener('click', (e) => onClickArea(e, pageNode));
      }
    };
  }, [page.id]);

  const replacePageHtml = html => {
    const pageNode = DOM.findPage(page.title);
    if (!pageNode) return;
    pageNode.innerHTML = html;
  };

  useEffect(( ) => {
    setDraftContent(page.content);
  }, [page.id]);

  useEffect(() => {
    let needClining = false;
    const DOMBlocks = DOM.findBlocks();

    if (DOMBlocks) {
      for (let key in DOMBlocks) {
        const DOMBlock = DOMBlocks[+key];

        if (DOMBlock) {
          let displaValue = "inline";

          for (let key in DOMBlock.children) {
            const child = DOMBlock.children[+key];
            if (!child) continue;

            const styleDeclaration = window.getComputedStyle(child);
            if (!styleDeclaration) continue;

            const displayProperty = styleDeclaration.getPropertyValue('display');
            if (blocksType.includes(displayProperty)) {
              displaValue = "block";
              break;
            }
          }

          if (displaValue === "inline") DOMBlock.style.display = "inline";

          DOMBlock && DOMBlock.addEventListener('click', (e) => (
            onClickBlock(e, DOMBlock, +DOMBlock.getAttribute("data-block-id")))
          )
        }
      }
    }

    blocks.forEach((block) => {
      if (block.renderHTML) {
        const nodes = document.querySelectorAll(`[data-block-id='${block.id}']`)

        nodes.length && nodes.forEach((node) => {
          node.innerHTML = block.renderHTML;
        })

        needClining = true
      }
    })

    needClining && clearBlockRenderHtml();

    return () => {
      // toggleEditMode(false);

      if (DOMBlocks) {
        for (let key in DOMBlocks) {
          const DOMBlock = DOMBlocks[+key];
          DOMBlock && DOMBlock.removeEventListener('click', (e) => (
            onClickBlock(e, DOMBlock, +DOMBlock.getAttribute("data-block-id")))
          )
        }
      }
    };
  }, [blocks]);

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
  }, [editMode, page && page.content]);

  const onChangeArea = val => {
    setDraftContent(val);
  };

  const handleSave = () => {
    toggleEditMode(false);

    if (blockId) {
      onSaveBlock(blockId, addContentMarker(draftContent, page.contentMode));
    } else {
      onSave(page.id, addContentMarker(draftContent, page.contentMode));
    }
  };

  const handleCancel = () => {
    setDraftContent(page.content);
    toggleEditMode(false);
  };

  const renderEditor = (height) => {
    switch (page.contentMode) {
      case "md": {
        return (
          <MarkdownEditor
            height={height - 67 - 45}
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
            height={`${height - 67 - 44}px`}
            value={draftContent}
            onChange={onChangeArea}
            mode={page.contentMode}
          />
        );
      }
    }
  };

  const reverse = getReverseValue(position.height, position.top);

  return (
    <div style={{width: `${position.width}px`, height: `${position.height}px`, position: "absolute",
      top: reverse ? "auto" : `${scrollValue || position.top}px`, left: `${position.left}px`,
      bottom: reverse ? `${(scrollValue && window.innerHeight - scrollValue) || window.innerHeight - position.bottom}px` : "auto"}}
    >
      {editMode && (
        <Paper className={clsx("p-1 h-100", classes.paperWrapper)}>
          <div className={
            clsx("editor-wrapper", (page.contentMode === "html" || page.contentMode === "textile") && "ace-wrapper")
          }>
            <ContentModeSwitch
                contentModeId={page.contentMode}
                moduleId={page.id}
                setContentMode={setContentMode}
            />
            {renderEditor(position.height)}
          </div>
          <div className="mt-3">
            <CustomButton onClick={handleCancel} styleType="cancel" styles={"mr-2"}>Cancel</CustomButton>
            <CustomButton onClick={handleSave} styleType="submit">Save</CustomButton>
          </div>
        </Paper>
      )}
    </div>
  );
}
