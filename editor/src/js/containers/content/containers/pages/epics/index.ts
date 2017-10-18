import {combineEpics} from "redux-observable";
import {EpicGetPages} from "./EpicGetPages";
import {EpicDeletePage} from "./EpicDeletePage";
import {EpicSavePage} from "./EpicSavePage";

export const EpicPages = combineEpics(
  EpicGetPages,
  EpicSavePage,
  EpicDeletePage,
);
