import {combineEpics} from "redux-observable";
import {EpicGetPages} from "./EpicGetPages";
import {EpicDeletePage} from "./EpicDeletePage";
import {EpicSavePage} from "./EpicSavePage";
import {EpicGetPageByUrl} from "./EpicGetPageByUrl";
import {EpicAddPage, EpicRedirect} from "./EpicAddPage";

export const EpicPages = combineEpics(
  EpicGetPages,
  EpicGetPageByUrl,
  EpicSavePage,
  EpicAddPage,
  EpicDeletePage,
  EpicRedirect(),
);
