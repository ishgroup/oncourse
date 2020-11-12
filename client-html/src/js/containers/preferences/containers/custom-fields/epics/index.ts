import { combineEpics } from "redux-observable";
import { EpicGetCustomFields } from "./EpicGetCustomFields";
import { EpicUpdateCustomFields } from "./EpicUpdateCustomFields";
import { EpicDeleteCustomField } from "./EpicDeleteCustomField";

export const EpicCustomFields = combineEpics(EpicGetCustomFields, EpicUpdateCustomFields, EpicDeleteCustomField);
