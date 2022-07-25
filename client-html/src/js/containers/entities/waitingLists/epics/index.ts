import { combineEpics } from "redux-observable";
import { EpicGetWaitingList } from "./EpicGetWaitingList";
import { EpicUpdateWaitingListItem } from "./EpicUpdateWaitingListItem";

export const EpicWaitingList = combineEpics(
  EpicGetWaitingList,
  EpicUpdateWaitingListItem
);
