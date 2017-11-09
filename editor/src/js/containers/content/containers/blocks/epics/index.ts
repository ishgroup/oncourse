import {combineEpics} from "redux-observable";
import {EpicDeleteBlock} from "./EpicDeleteBlock";
import {EpicGetBlocks} from "./EpicGetBlocks";
import {EpicSaveBlock} from "./EpicSaveBlock";
import {EpicAddBlock} from "./EpicAddBlock";

export const EpicBlocks = combineEpics(
  EpicDeleteBlock,
  EpicGetBlocks,
  EpicSaveBlock,
  EpicAddBlock,
);
