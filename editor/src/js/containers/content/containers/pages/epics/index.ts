import {combineEpics} from "redux-observable";
import {EpicGetPages} from "./EpicGetPages";
import {EpicDeletePage} from "./EpicDeletePage";
import {EpicSavePage} from "./EpicSavePage";
import {EpicGetPageByUrl} from "./EpicGetPageByUrl";

export const EpicPages = combineEpics(
  EpicGetPages,
  EpicGetPageByUrl,
  EpicSavePage,
  EpicDeletePage,
);
