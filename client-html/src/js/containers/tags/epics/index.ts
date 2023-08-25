import { combineEpics } from "redux-observable";
import { EpicCreateTag } from "./EpicCreateTag";
import { EpicDeleteTag } from "./EpicDeleteTag";
import { EpicGetAllTags } from "./EpicGetAllTags";
import { EpicGetEntityTags } from "./EpicGetEntityTags";
import { EpicGetListTags } from "./EpicGetListTags";
import { EpicGetTag } from "./EpicGetTag";
import { EpicUpdateTag } from "./EpicUpdateTag";

export const EpicTags = combineEpics(
  EpicGetTag,
  EpicGetAllTags,
  EpicCreateTag,
  EpicDeleteTag,
  EpicUpdateTag,
  EpicGetListTags,
  EpicGetEntityTags
);