import { combineEpics } from "redux-observable";
import { EpicDeleteContactRelationType } from "./EpicDeleteContactRelationType";
import { EpicGetContactRelationTypes } from "./EpicGetContactRelationTypes";
import { EpicUpdateContactRelationTypes } from "./EpicUpdateContactRelationTypes";

export const EpicContactRelationTypes = combineEpics(
  EpicGetContactRelationTypes,
  EpicUpdateContactRelationTypes,
  EpicDeleteContactRelationType
);
