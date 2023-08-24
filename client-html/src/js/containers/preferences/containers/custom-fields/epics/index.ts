import { combineEpics } from "redux-observable";
import { EpicDeleteCustomField } from "./EpicDeleteCustomField";
import { EpicGetCustomFields } from "./EpicGetCustomFields";
import { EpicUpdateCustomFields } from "./EpicUpdateCustomFields";

export const EpicCustomFields = combineEpics(EpicGetCustomFields, EpicUpdateCustomFields, EpicDeleteCustomField);
