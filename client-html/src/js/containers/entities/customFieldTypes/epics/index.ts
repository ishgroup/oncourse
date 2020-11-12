import { combineEpics } from "redux-observable";
import { EpicGetCustomFieldTypes } from "./EpicGetCustomFieldTypes";

export const EpicCustomFieldTypes = combineEpics(EpicGetCustomFieldTypes);
