import {
  getBreaksNeededForEmptyLineAfter,
  getBreaksNeededForEmptyLineBefore,
  selectWord
} from "react-mde/lib/js/util/MarkdownUtil.js";

export type AlterLineFunction = (line: string, index: number) => string;
export function insertBeforeEachLine(
  selectedText: string,
  insertBefore: string | AlterLineFunction
): { modifiedText: string; insertionLength: number } {
  const lines = selectedText.split(/\n/);

  let insertionLength = 0;
  const modifiedText = lines
    .map((item, index) => {
      if (typeof insertBefore === "string") {
        insertionLength += insertBefore.length;
        return insertBefore + item;
      } if (typeof insertBefore === "function") {
        const insertionResult = insertBefore(item, index);
        insertionLength += insertionResult.length;
        return insertBefore(item, index) + item;
      }
      throw Error("insertion is expected to be either a string or a function");
    })
    .join("\n");

  return { modifiedText, insertionLength };
}

export const makeList = (
  state0,
  api,
  insertBefore: string | AlterLineFunction
) => {
  const newSelectionRange = selectWord({
    text: state0.text,
    selection: state0.selection
  });
  const state1 = api.setSelectionRange(newSelectionRange);

  const breaksBeforeCount = getBreaksNeededForEmptyLineBefore(
    state1.text,
    state1.selection.start
  );
  const breaksBefore = Array(breaksBeforeCount + 1).join("\n");

  const breaksAfterCount = getBreaksNeededForEmptyLineAfter(
    state1.text,
    state1.selection.end
  );
  const breaksAfter = Array(breaksAfterCount + 1).join("\n");

  const modifiedText = insertBeforeEachLine(state1.selectedText, insertBefore);

  api.replaceSelection(
    `${breaksBefore}${modifiedText.modifiedText}${breaksAfter}`
  );

  const oneLinerOffset = state1.selectedText.indexOf("\n") === -1 ? modifiedText.insertionLength : 0;

  const selectionStart = state1.selection.start + breaksBeforeCount + oneLinerOffset;
  const selectionEnd = selectionStart + modifiedText.modifiedText.length - oneLinerOffset;

  api.setSelectionRange({
    start: selectionStart,
    end: selectionEnd
  });
};

export const UnorderedListCommand = {
  buttonProps: { "aria-label": "Add unordered list" },
  execute: ({ initialState, textApi }) => {
    makeList(initialState, textApi, "* ");
  }
};
