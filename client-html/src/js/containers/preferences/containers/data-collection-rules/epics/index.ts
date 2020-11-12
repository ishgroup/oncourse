import { combineEpics } from "redux-observable";
import { EpicGetDataCollectionRules } from "./EpicGetDataCollectionRules";
import { EpicCreateDataCollectionRule } from "./EpicCreateDataCollectionRule";
import { EpicDeleteDataCollectionRule } from "./EpicDeleteDataCollectionRule";
import { EpicUpdateDataCollectionRule } from "./EpicUpdateDataCollectionRule";

export const EpicDataCollectionRules = combineEpics(
  EpicGetDataCollectionRules,
  EpicCreateDataCollectionRule,
  EpicDeleteDataCollectionRule,
  EpicUpdateDataCollectionRule
);
