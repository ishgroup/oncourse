import {combineEpics} from "redux-observable";
import {EpicGetHistory} from "./EpicGetHistory";
import {EpicSetVersion} from "./EpicSetVersion";
import {EpicPublish} from "./EpicPublish";

export const EpicHistory = combineEpics(
  EpicGetHistory,
  EpicSetVersion,
  EpicPublish,
);
