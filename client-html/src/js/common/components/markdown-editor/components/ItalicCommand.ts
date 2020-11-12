import { selectWord } from "react-mde/lib/js/util/MarkdownUtil.js";

export const ItalicCommandCustom = {
  buttonProps: { "aria-label": "Add italic text" },
  execute: ({ initialState, textApi }) => {
    const newSelectionRange = selectWord({
      text: initialState.text,
      selection: initialState.selection,
    });
    const state1 = textApi.setSelectionRange(newSelectionRange);
        textApi.replaceSelection(
          state1.selectedText.match(/_/g)
            ? state1.selectedText.replace(/_/g, "")
            : `_${state1.selectedText}_`
    );
  },
  handleKeyCommand: e => (e.ctrlKey || e.metaKey) && e.key == "b",
};
