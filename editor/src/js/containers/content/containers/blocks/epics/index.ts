import {combineEpics} from "redux-observable";
import {EpicDeleteBlock} from "./EpicDeleteBlock";
import {EpicGetBlocks} from "./EpicGetBlocks";
import {EpicSaveBlock} from "./EpicSaveBlock";

export const EpicBlocks = combineEpics(
  EpicDeleteBlock,
  EpicGetBlocks,
  EpicSaveBlock,
);
