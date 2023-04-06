import { combineEpics } from "redux-observable";
import { EpicGetAllTags } from "./EpicGetAllTags";
import { EpicCreateTag } from "./EpicCreateTag";
import { EpicDeleteTag } from "./EpicDeleteTag";
import { EpicUpdateTag } from "./EpicUpdateTag";
import { EpicGetListTags } from "./EpicGetListTags";
import { EpicGetEntityTags } from "./EpicGetEntityTags";
import { EpicGetTag } from "./EpicGetTag";

export const EpicTags = combineEpics(
  EpicGetTag,
  EpicGetAllTags,
  EpicCreateTag,
  EpicDeleteTag,
  EpicUpdateTag,
  EpicGetListTags,
  EpicGetEntityTags
);