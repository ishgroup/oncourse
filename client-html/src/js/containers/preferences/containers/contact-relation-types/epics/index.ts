import { combineEpics } from "redux-observable";
import { EpicGetContactRelationTypes } from "./EpicGetContactRelationTypes";
import { EpicUpdateContactRelationTypes } from "./EpicUpdateContactRelationTypes";
import { EpicDeleteContactRelationType } from "./EpicDeleteContactRelationType";

export const EpicContactRelationTypes = combineEpics(
  EpicGetContactRelationTypes,
  EpicUpdateContactRelationTypes,
  EpicDeleteContactRelationType
);
