import React from "react";
import clsx from "clsx";
import useComponentVisible from "../../../hooks/UseComponentVisible";
import {selectWord} from "react-mde/lib/js/util/MarkdownUtil.js";

let optsRef;

const MdeHeadersDropdown: React.FC<any>  = () => {
  const {ref, isComponentVisible, setIsComponentVisible} = useComponentVisible(false);

  function setHeader( prefix: string) {
    const newSelectionRange = selectWord({
      text: optsRef.initialState.text,
      selection: optsRef.initialState.selection,
    });

    const state1 = optsRef.textApi.setSelectionRange(newSelectionRange);
    const replacement = prefix
      ? `${prefix} ${state1.selectedText.replace(/#+(\s+)?/,"")}`
      : state1.selectedText.replace(/#+(\s+)?/,"");
    const state2 = optsRef.textApi.replaceSelection(replacement);

    optsRef.textApi.setSelectionRange({
      start: state2.selection.end - replacement.length,
      end: state2.selection.end,
    });

    setIsComponentVisible(false);
  }

  return (
    <div
      id="mdeHeadersDropdown"
      ref={ref}
      onClick={() => setIsComponentVisible(true)}
      className="ck ck-dropdown ck-heading-dropdown">
      <button
        className="ck ck-button ck-on ck-button_with-text ck-dropdown__button"
        type="button"
        tabIndex={-1}
        aria-haspopup="true">
          <span className="ck ck-tooltip ck-tooltip_s"><span className="ck ck-tooltip__text">Heading</span></span>
          <span className="ck ck-button__label">Paragraph</span>
        <svg className="ck ck-icon ck-dropdown__arrow" viewBox="0 0 10 10">
          <path
            d="M.941 4.523a.75.75 0 1 1 1.06-1.06l3.006 3.005 3.005-3.005a.75.75 0 1 1 1.06 1.06l-3.549 3.55a.75.75 0 0 1-1.168-.136L.941 4.523z"></path>
        </svg>
      </button>
      <div className={clsx("ck ck-reset ck-dropdown__panel ck-dropdown__panel_se",
          isComponentVisible && "ck-dropdown__panel-visible"
        )}>
        <ul className="ck ck-reset ck-list">
          <li className="ck ck-list__item">
            <button
              onClick={() => {
                setHeader("");
              }}
              className="ck ck-button ck-heading_paragraph ck-off ck-button_with-text" type="button" tabIndex={-1}>
              <span className="ck ck-button__label">Paragraph</span>
            </button>
          </li>
          <li className="ck ck-list__item">
            <button
              onClick={() => {
                setHeader("#");
              }}
              className="ck ck-button ck-heading_heading1 ck-off ck-button_with-text" type="button" tabIndex={-1}>
              <span className="ck ck-button__label">Heading 1</span>
            </button>
          </li>
          <li className="ck ck-list__item">
            <button
              onClick={() => {
                setHeader("##");
              }}
              className="ck ck-button ck-heading_heading2 ck-off ck-button_with-text" type="button" tabIndex={-1}>
              <span className="ck ck-button__label">Heading 2</span>
            </button>
          </li>
          <li className="ck ck-list__item">
            <button
              onClick={() => {
                setHeader("###");
              }}
              className="ck ck-button ck-heading_heading3 ck-off ck-button_with-text" type="button" tabIndex={-1}>
              <span className="ck ck-button__label">Heading 3</span>
            </button>
          </li>
          <li className="ck ck-list__item">
            <button
              onClick={() => {
                setHeader("####");
              }}
              className="ck ck-button ck-heading_heading4 ck-off ck-button_with-text"
              type="button"
              tabIndex={-1}
            >
              <span className="ck ck-button__label">Heading 4</span>
            </button>
          </li>
        </ul>
      </div>
    </div>
  );
};

export const HeaderCommand = {
  name: "custom-header-command",
  icon: () => (
    <MdeHeadersDropdown editorRef={this} />
  ),
  execute: opts => {
    optsRef = opts;
  },
};
