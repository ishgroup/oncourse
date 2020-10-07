import * as React from "react";
import {selectWord} from "react-mde/lib/js/util/MarkdownUtil.js";

export const ItalicCommandCustom = {
  buttonProps: {"aria-label": "Add italic text"},
  execute: ({initialState, textApi}) => {
    const newSelectionRange = selectWord({
      text: initialState.text,
      selection: initialState.selection,
    });
    const state1 = textApi.setSelectionRange(newSelectionRange);
    textApi.replaceSelection(
      state1.selectedText.match(/\*/g)
        ?  state1.selectedText.replace(/\*/g,"")
        : `*${state1.selectedText}*`);
  },
  handleKeyCommand: e => (e.ctrlKey || e.metaKey) && e.key == "b",
};
