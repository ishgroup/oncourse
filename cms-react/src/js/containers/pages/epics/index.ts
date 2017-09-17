import {combineEpics} from "redux-observable";
import {EpicGetPages} from "./EpicGetPages";

export const EpicPages = combineEpics(
  EpicGetPages,
);
