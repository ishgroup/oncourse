import {combineEpics} from "redux-observable";
import {EpicGetPages} from "./EpicGetPages";
import {EpicSavePage} from "./EpicSavePage";

export const EpicPages = combineEpics(
  EpicGetPages,
  EpicSavePage,
);
