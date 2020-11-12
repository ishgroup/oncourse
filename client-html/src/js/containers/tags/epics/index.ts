import { combineEpics } from "redux-observable";
import { EpicGetAllTags } from "./EpicGetAllTags";
import { EpicCreateTag } from "./EpicCreateTag";
import { EpicDeleteTag } from "./EpicDeleteTag";
import { EpicUpdateTag } from "./EpicUpdateTag";
import { EpicGetListTags } from "./EpicGetListTags";
import { EpicGetEntityTags } from "./EpicGetEntityTags";
import { EpicUpdateTagEditViewState } from "./EpicUpdateTagEditViewState";

export const EpicTags = combineEpics(
  EpicGetAllTags,
  EpicCreateTag,
  EpicDeleteTag,
  EpicUpdateTag,
  EpicGetListTags,
  EpicGetEntityTags,
  EpicUpdateTagEditViewState
);
