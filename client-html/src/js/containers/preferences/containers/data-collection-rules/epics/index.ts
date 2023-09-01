import { combineEpics } from "redux-observable";
import { EpicCreateDataCollectionRule } from "./EpicCreateDataCollectionRule";
import { EpicDeleteDataCollectionRule } from "./EpicDeleteDataCollectionRule";
import { EpicGetDataCollectionRules } from "./EpicGetDataCollectionRules";
import { EpicUpdateDataCollectionRule } from "./EpicUpdateDataCollectionRule";

export const EpicDataCollectionRules = combineEpics(
  EpicGetDataCollectionRules,
  EpicCreateDataCollectionRule,
  EpicDeleteDataCollectionRule,
  EpicUpdateDataCollectionRule
);
