import {combineEpics} from "redux-observable";
import {EpicGetSpecialPages} from "./EpicGetSpecialPages";
import {EpicUpdateSpecialPages} from "./EpicUpdateSpecialPages";

export const EpicSpecialPages = combineEpics(
  EpicGetSpecialPages,
  EpicUpdateSpecialPages,
);
