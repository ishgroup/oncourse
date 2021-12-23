import React, {useCallback, useEffect, useRef, useState} from 'react';
import clsx from "clsx";
import marked from "marked";
import { ResizableBox } from "react-resizable";
import "react-resizable/css/styles.css";
import {PageState} from "../reducers/State";
import {DOM} from "../../../../../utils";
import {getHistoryInstance} from "../../../../../history";
import PageService from "../../../../../services/PageService";
import {addContentMarker, getEditorSize} from "../../../utils";
import {ContentMode} from "../../../../../model";
import BlockEditor from "../../blocks/components/BlockEditor";

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
  setPageContentMode?: (id: number, contentMode: ContentMode) => any;
  setBlockContentMode?: (id: number, contentMode: ContentMode) => any;
  editMode?: any;
}

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
  setPageContentMode,
  setBlockContentMode
}) => {
  const [draftContent, setDraftContent] = useState('');
  const [scrollValue, setScrollValue] = useState(0);
  const [blockId, setBlockId] = useState(null);
  const [DOMNode, setDOMNode] = useState(null);
  const [position, setPosition] = useState({
    height: 230,
    width: 570,
    top: 0,
    left: 0,
    bottom: 0,
  });
  const [fullscreen, setFullscreen] = useState<boolean>(true);
  const [oldPosition, setOldPosition] = useState({
    top: 0,
    left: 0,
  });
  const blockWrapperRef = useRef(null);

  useEffect(() => {
    toggleEditMode(false);
    return () => toggleEditMode(false);
  }, []);

  useEffect(() => {
    document.addEventListener('scroll', onScroll);
    return () => document.removeEventListener('scroll', onScroll);
  }, [blockId]);

  const handleContainerClass = useCallback(value => {
    const body = document.getElementsByTagName('body');
    if (value) body[0].classList.add('overflow-hidden');
    else body[0].classList.remove('overflow-hidden');

    setFullscreen(value);
  }, []);

  const onClickArea = (e, pageNode) => {
    e.preventDefault();

    setDOMNode(pageNode);
    setScrollValue(0);
    setBlockId(0);

    setPosition(getEditorSize(pageNode.getBoundingClientRect()));

    setDraftContent(page.contentMode === "html" ? marked(page.content || "") : page.content);
    toggleEditMode(true);
    getHistoryInstance().push(`/page/${page.id}`);
    handleContainerClass(true);
  };

  const onClickBlock = (e, DOMBlock, id) => {
    e.preventDefault();
    const block = blocks.filter(elem => elem.id === id)[0];

    if (!block) return null;
    setDOMNode(DOMBlock);

    setScrollValue(0);

    setBlockId(id);
    setPosition(getEditorSize(DOMBlock.getBoundingClientRect()));
    setDraftContent(block.contentMode === "html" ? marked(block.content || "") : block.content);

    toggleEditMode(true);
    handleContainerClass(true);
  };

  const onScroll = () => {
    const DOMNodeElementData = DOMNode && DOMNode.getBoundingClientRect();

    if (!DOMNodeElementData) return null;

    setScrollValue(DOMNodeElementData.top);
  };

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
  }, [page]);

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
        const nodes = document.querySelectorAll(`[data-block-id='${block.id}']`);

        nodes.length && nodes.forEach((node) => {
          node.innerHTML = block.renderHTML;
        });

        needClining = true
      }
    });

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

  const handleSave = () => {
    toggleEditMode(false);

    if (blockId) {
      const block = blocks.filter(elem => elem.id === blockId)[0];
      onSaveBlock(blockId, addContentMarker(draftContent, block.contentMode));
    } else {
      onSave(page.id, addContentMarker(draftContent, page.contentMode));
    }

    handleContainerClass(false);
  };

  const handleCancel = () => {
    setDraftContent(page.content);
    toggleEditMode(false);
    handleContainerClass(false);
  };

  const onResize = useCallback((e, { handle }) => {

    const rect = blockWrapperRef && blockWrapperRef.current && blockWrapperRef.current.getBoundingClientRect();

    const top = oldPosition.top - e.clientY;
    const left = oldPosition.left - e.clientX;

    if (handle === 'sw') {
      setPosition(prev => ({
        ...prev,
        width: rect.width + left,
        height: rect.height - top,
        left: rect.left - left,
      }));
    } else if (handle === 'se') {
      setPosition(prev => ({
        ...prev,
        width: rect.width - left,
        height: rect.height - top,
      }));
    } else if (handle === 'ne') {
      setPosition(prev => ({
        ...prev,
        width: rect.width - left,
        height: rect.height + top,
        top: rect.top - top,
      }));
    } else {
      setPosition(prev => ({
        ...prev,
        width: rect.width + left,
        height: rect.height + top,
        top: rect.top - top,
        left: rect.left - left,
      }));
    }

    setOldPosition({
      top: e.clientY,
      left: e.clientX,
    });
  }, [oldPosition]);

  const onResizeStart = useCallback(e => {
    setOldPosition({
      top: e.clientY,
      left: e.clientX,
    });
  }, []);

  const onFullscreen = useCallback(() => {
    handleContainerClass(!fullscreen);
  }, [fullscreen]);

  const block = blockId ? blocks.filter(elem => elem.id === blockId)[0] : null;
  const contentMode = block ? block.contentMode : page.contentMode;

  const wrapperStyles = fullscreen ? {} : {
    width: `${position.width}px`,
    height: `${position.height}px`,
    position: "absolute",
    top: `${scrollValue || position.top}px`,
    left: `${position.left}px`,
    bottom: "auto",
    zIndex: 9999,
  };

  return (
    <div
      ref={blockWrapperRef}
      className={clsx("h-100 w-100", { "fullscreen-page-block": fullscreen })}
      style={wrapperStyles as any}
    >
      <ResizableBox
        width={position.width}
        height={position.height}
        onResize={onResize}
        onResizeStart={onResizeStart}
        resizeHandles={['sw', 'se']}
      >
        {editMode && (
          <BlockEditor
            mode={contentMode}
            content={draftContent}
            setContent={setDraftContent}
            moduleId={blockId && block ? blockId : page.id}
            setContentMode={blockId && block ? setBlockContentMode : setPageContentMode}
            handleSave={handleSave}
            handleCancel={handleCancel}
            position={position}
            enabledFullscreen
            onFullscreen={onFullscreen}
            fullscreen={fullscreen}
          />
        )}
      </ResizableBox>
    </div>
  );
};
