import {combineEpics} from "redux-observable";
import {EpicGetPages} from "./EpicGetPages";
import {EpicSavePageHtml} from "./EpicSavePageHtml";
import {EpicSavePageSettings} from "./EpicSavePageSettings";

export const EpicPages = combineEpics(
  EpicGetPages,
  EpicSavePageHtml,
  EpicSavePageSettings,
);
