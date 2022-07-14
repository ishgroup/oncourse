import { combineEpics } from "redux-observable";
import { EpicGetWaitingList } from "./EpicGetWaitingList";
import { EpicUpdateWaitingListItem } from "./EpicUpdateWaitingListItem";
import { EpicDeleteWaitingList } from "./EpicDeleteWaitingList";

export const EpicWaitingList = combineEpics(
  EpicGetWaitingList,
  EpicUpdateWaitingListItem,
  EpicDeleteWaitingList
);
